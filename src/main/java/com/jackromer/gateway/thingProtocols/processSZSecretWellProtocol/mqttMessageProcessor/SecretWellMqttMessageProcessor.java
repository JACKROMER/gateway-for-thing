/*
 * File name: CurrencyMqttMessageProcessor.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 "zhouqiang" Jul 20, 2018
 * ... ... ...
 *
 ***************************************************/

package com.jackromer.gateway.thingProtocols.processSZSecretWellProtocol.mqttMessageProcessor;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.jackromer.gateway.gatewayServer.initGatewayPropertys.GatewayGlobalRelation;
import com.jackromer.gateway.poolDataProcessor.observerClass.MqttObserver;
import com.jackromer.gateway.thingCommand.CommandThings;
import com.jackromer.gateway.utils.Base64Util;
import com.jackromer.gateway.utils.GatewayDateUtil;
import com.jackromer.gateway.utils.XorUtil;

/**
 * @Description: 密码设备网关解析MQTT消息类
 * @author: jackromer
 * @version: 1.0, Jul 20, 2018
 */

public class SecretWellMqttMessageProcessor implements MqttObserver{

	public static final Logger	logger			= LoggerFactory.getLogger(SecretWellMqttMessageProcessor.class);

	private static final String	SUBTHINGIDSTR	= "subThingId";													// 小写

	private static final String	TIMESTAMP		= "timestamp";													// 小写

	private static final String	BODY			= "body";														// 小写

	private static final String	STATE			= "state";														// 小写

	private static final String	RESPONSE		= "response";													// 小写

	private static final String	DESIRED			= "desired";													// 小写

	private static final String	REPORTED		= "reported";

	private static final String	CS				= "cs";															// 小写

	private static final String	OPEN_COMMAND	= "open";
	
	private static final String METADATA = "metadata";

	public SecretWellMqttMessageProcessor() {}

	volatile private static SecretWellMqttMessageProcessor instance = null;

	public static SecretWellMqttMessageProcessor getInstance() {

		if (instance != null) {// 懒汉式

		} else {
			// 创建实例之前可能会有一些准备性的耗时工作
			synchronized (SecretWellMqttMessageProcessor.class) {
				if (instance == null) {// 二次检查
					instance = new SecretWellMqttMessageProcessor();
				}
			}
		}

		return instance;
	}


	@Override
	public void processMqttMessage(String gatewayId, String mqttMessage) {

		logger.info("密码设备MQTT-PROCESSOR类收到消息！");

		try {
			JSONObject messageJson = JSONObject.parseObject(mqttMessage);

			String subThingId = messageJson.getString(SUBTHINGIDSTR);// 获取子设备Id

			boolean ignoreFlag = true;// 是否是忽略的消息

			boolean checkFlag = false;// 是否检查命令超时

			JSONObject effectiveJson = messageJson.getJSONObject(BODY);// 默认为GET获取的数据包含response

			if (effectiveJson.containsKey(RESPONSE)) {// 处理GET获取的数据
				effectiveJson = effectiveJson.getJSONObject(RESPONSE);
				checkFlag = true;
			}

			JSONObject metaDataJson = effectiveJson.containsKey(METADATA)?effectiveJson.getJSONObject(METADATA) : null;
			
			JSONObject metaDataDesiredJson = (metaDataJson != null ? (metaDataJson.containsKey(DESIRED) ? metaDataJson.getJSONObject(DESIRED) : null) : null);// 获取timestamp
			
			JSONObject metaDataIcsJson = (metaDataDesiredJson != null ? (metaDataDesiredJson.containsKey(CS) ? metaDataDesiredJson.getJSONObject(CS) : null) : null);

			String timeStamp = (metaDataIcsJson != null ? ( metaDataIcsJson.containsKey(TIMESTAMP) ? metaDataIcsJson.getString(TIMESTAMP) : null) : null);//获取上次下发的时间戳
			
			JSONObject stateJson = effectiveJson.containsKey(STATE) ? effectiveJson.getJSONObject(STATE) : null;// 获取state

			JSONObject reportedJson = (stateJson != null ? (stateJson.containsKey(REPORTED) ? stateJson.getJSONObject(REPORTED) : null) : null);// 获取上一次上报的数据

			JSONObject desiredJson = (stateJson != null ? stateJson.getJSONObject(DESIRED) : null);// 获取desired

			if (null != desiredJson && null != reportedJson && null != metaDataJson && timeStamp != null) {// 作下发命令处理

				if (desiredJson.containsKey(CS)) {

					logger.info("处理GET获取的下发的命令" + messageJson);

					commandSubThing(subThingId, timeStamp, desiredJson, checkFlag);

					ignoreFlag = false;
				}

			}

			if (ignoreFlag) {
				logger.info("消息中未解析到需要下发的命令,忽略:" + messageJson);
			}
		} catch (Exception e) {
			logger.info("MQTT消息解析失败" + mqttMessage);
			e.printStackTrace();
		}
	}



	/**
	 * @Description:处理下发命令
	 * @param subThingId
	 *            子设备id
	 * @param timeStamp
	 *            时间戳
	 * @param desiredJson
	 *            需要解析的下发数据
	 * @param checkTime
	 *            是否需要校验超过三分钟
	 */
	public void commandSubThing(String subThingId, String timeStamp, JSONObject desiredJson, boolean checkTime) {

		try {
			String innerOpen = desiredJson.getString(CS);

			if (checkTime) {// 检验时间
				if (GatewayDateUtil.getInstance().checkCommandTime(new Date(), timeStamp)) {// 超过三分钟
					logger.info("命令时间已经超过三分钟[命令过期],时间戳为>>>" + timeStamp);
					return;
				}
			}

			// 是close开锁命令
			if (innerOpen.equals(OPEN_COMMAND)) {
				if (GatewayGlobalRelation.subThingIdAndChannelMap.keySet().size() == 0) {
					logger.info("当前网关对应子设备没有活跃的TCP连接, 网关下总活跃TCP连接数为" + GatewayGlobalRelation.subThingIdAndChannelMap.keySet().size() + ", 下发命令目标子设备 " + subThingId + " 未在线！");
					return;
				}

				try {// 将数据转发给客户端
						// 是open开锁命令
					if (innerOpen.equals(OPEN_COMMAND)) {
						//JSONObject reportedJson = desiredJson.getJSONObject(REPORTED);
						//从reported中获取基础数据
						//String protocalVersiion = reportedJson.getString("pv");// 协议版本
						//String deviceCode = reportedJson.getString("dc");// 设备编码
						//String fireVersion = reportedJson.getString("fv");// 固件版本
						//String hardVersion = reportedJson.getString("hv");// 硬件版本
						
						String commandStr = initData(subThingId, OPEN_COMMAND);// 获取下发命令数据流
						
						CommandThings.getInstance().sendCommandToSubThing(subThingId, commandStr);
					} else {
						logger.info("并非开锁命令, 忽略下发消息！");
					}
				} catch (Exception e) {
					logger.info("下发失败 >>> " + subThingId);
					e.printStackTrace();
				}
			} else {
				logger.info("并非开锁命令, 忽略下发消息！");
			}
		} catch (Exception e) {
			logger.info("命令下发失败>>>" + subThingId + desiredJson);
			e.printStackTrace();
		}
	}



	private String initData(String subThingId, String data) {
		// 数据帧头
		String header = "*" + subThingId + "*";

		// 数据帧尾
		String end = "#";

		// payload XOR加密
		byte[] xorPayload = XorUtil.getInstance().xorEncode((data).getBytes());

		// payload base64加密
		byte[] base64Payload = Base64Util.getInstance().encodeData(xorPayload);

		return header + new String(base64Payload) + end;
	}


}
