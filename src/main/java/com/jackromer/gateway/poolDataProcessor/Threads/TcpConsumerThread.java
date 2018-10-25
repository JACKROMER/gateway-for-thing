/*
 * File name: sss.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 "zhouqiang" Jul 13, 2018
 * ... ... ...
 *
 ***************************************************/

package com.jackromer.gateway.poolDataProcessor.Threads;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackromer.gateway.gatewayServer.initGatewayPropertys.GatewayGlobalRelation;
import com.jackromer.gateway.mqttConnector.MqttPublisher;
import com.jackromer.gateway.poolDataProcessor.observerClass.TcpObserver;
import com.jackromer.gateway.utils.ChannelUtil;
import com.jackromer.gateway.utils.PublishUtil;

import io.netty.channel.ChannelHandlerContext;

/**
 * TCP消息处理线程
 * 
 * @Description:
 * @author: jackromer
 * @version: 1.0, Jun 27, 2018
 */
public class TcpConsumerThread implements Callable<String> {

	public static final Logger		loggerConsumer	= LoggerFactory.getLogger(TcpConsumerThread.class);

	private ChannelHandlerContext	ctx;																// channel-ctx

	private String					gateWayId;

	private String					message;															// 待处理的消息

	private TcpObserver				tcpObserver;

	public static final String		SUBTHING_REPLY	= "subThingReply";									// 返回此值代表是设备返回的数据



	public TcpConsumerThread(ChannelHandlerContext ctx, String gateWayId, String message, TcpObserver tcpObserver) {
		this.ctx = ctx;
		this.gateWayId = gateWayId;
		this.message = message;
		this.tcpObserver = tcpObserver;
	}



	@Override
	public String call() throws Exception {// 错误返回null
		try {

			loggerConsumer.info("消费的消息为：" + message);

			if (null == message || message.isEmpty()) {
				return null;
			}

			String[] resultArray = tcpObserver.processTcpMessage(gateWayId, message);

			// 处理错误数据
			if (null == resultArray[0] || null == resultArray[1] || resultArray[0].isEmpty()
					|| resultArray[1].isEmpty()) {
				loggerConsumer.info("消息已忽略>>>[" + message + "]");
				return null; // 提前return则task结束
			}

			// 上报数据后回复给子设备数据流
			if (!resultArray[2].isEmpty()) {
				ChannelUtil.getInstance().sendMessage(ctx.channel(), resultArray[2], false);
			}

			// 单独处理开锁后返回后的数据
			if (resultArray[0].equals(SUBTHING_REPLY)) {
				loggerConsumer.info("下发命令后设备返回的数据已经记录！");
			} else {// 处理设备上报的状态消息
				String subThingId = resultArray[0];

				String publishJson = resultArray[1];

				boolean firstReport = true;

				String currentChannelId = ctx.channel().id().toString();// 最新的channelId

				if (GatewayGlobalRelation.subThingIdAndChannelMap.containsKey(subThingId)) {// 并非第一次上报或者连接未正常断开

					String oldChannelId = GatewayGlobalRelation.subThingIdAndChannelMap.get(subThingId).id().toString();

					if (oldChannelId.equals(currentChannelId)) {// channelId相同,不是第一次上报,维持现状

						loggerConsumer.info("子设备" + subThingId + "仍然在线, 且channelId相同, [" + oldChannelId + ">>>"
								+ currentChannelId + "] 并非第一次上报！");

						firstReport = false;

						if (GatewayGlobalRelation.subThingIdAndChannelMap.get(subThingId).isActive()) {

							loggerConsumer.info("旧的TCP连接仍然活跃：" + oldChannelId + " 无需替换为新的channel！");

						} else {

							loggerConsumer.info("旧的TCP连接超时已经自动断开：" + oldChannelId + " 需要替换为新的channel！");

							GatewayGlobalRelation.subThingIdAndChannelMap.put(subThingId, ctx.channel());// 替换新的channel
						}

					} else {// channelId不同,则关闭之前旧的channel,视为第一次上报

						loggerConsumer.info("子设备" + subThingId + "仍然在线, 但channelId不同,视为第一次上报 [" + oldChannelId + ">>>"
								+ currentChannelId + "] 关闭旧的channel,并从MAP中移除！");// channelId可能相同

						if (GatewayGlobalRelation.subThingIdAndChannelMap.get(subThingId).isActive()) {
							// 旧的TCP连接任然活跃-已经无效，关闭通道
							loggerConsumer.info("旧的TCP连接仍然活跃：" + oldChannelId + " 即将关闭channel！");

							ChannelUtil.getInstance().closeChannel(GatewayGlobalRelation.subThingIdAndChannelMap.get(oldChannelId));
						} else {
							loggerConsumer.info("旧的TCP连接超时已经自动断开：" + oldChannelId + " 无需关闭channel！");
						}

					}

				}

				
				//真的第一次上报、老的channel正常断开连接但有新的channel连接连上都视为第一次上报
				if (firstReport) {

					GatewayGlobalRelation.subThingIdAndChannelMap.put(subThingId, ctx.channel());

					loggerConsumer.info("子设备第一次上报消息 " + subThingId + "已经添加到可用MAP中！对应channelId>>>" + currentChannelId);
					
					//尝试(GET)获取设备影子，校验3分钟内是否有下发命令，如果有下发命令则下发一条数据到设备<重要>

					String messageGet = PublishUtil.getInstance().getPayLoadByParamName("get", gateWayId, subThingId).toJSONString();

					MqttPublisher.getInstance().publishMessage(gateWayId, messageGet, 1);// 首次上报获取影子信息,MQTT消息到达后会解析是否有下发命令
				}

				// 正常上报应该上报的消息
				MqttPublisher.getInstance().publishMessage(gateWayId, publishJson, 0);// 正常上报
			}
		} catch (Exception e) {
			loggerConsumer.info("消息消费失败,发布失败！");
			e.printStackTrace();
		}
		return null;
	}

}
