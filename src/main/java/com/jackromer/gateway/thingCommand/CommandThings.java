/*
 * File name: commandThings.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 "zhouqiang" Jul 23, 2018
 * ... ... ...
 *
 ***************************************************/

package com.jackromer.gateway.thingCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackromer.gateway.gatewayServer.initGatewayPropertys.GatewayGlobalRelation;
import com.jackromer.gateway.utils.ChannelUtil;

import io.netty.channel.Channel;

/**
 * @Description:处理下发命令,负责将消息发给对应的channel,对所有设备类型是通用的。
 * @author: jackromer
 * @version: 1.0, Jul 23, 2018
 */

public class CommandThings {

	public static final Logger				logger		= LoggerFactory.getLogger(CommandThings.class);

	volatile private static CommandThings	instance	= null;



	private CommandThings() {
	}

	public static CommandThings getInstance() {

		if (instance != null) {// 懒汉式

		} else {
			// 创建实例之前可能会有一些准备性的耗时工作
			synchronized (CommandThings.class) {
				if (instance == null) {// 二次检查
					instance = new CommandThings();
				}
			}
		}

		return instance;
	}



	/**
	 * 处理下发命令
	* @Description:
	* @param subThingId
	* @param commandStr 需要下发的数据
	 */
	public  void  sendCommandToSubThing(String subThingId, String commandStr) {
		
			if(GatewayGlobalRelation.subThingIdAndChannelMap.keySet().size() == 0) {
				logger.info("当前网关对应子设备没有活跃的TCP连接, 网关下总活跃TCP连接数为" + GatewayGlobalRelation.subThingIdAndChannelMap.keySet().size() + ", 下发命令目标子设备 " + subThingId + " 未在线！");
				return;
			}
			
			try {//向所有的包含子设备的channel下发命令
				if (!subThingId.isEmpty() && GatewayGlobalRelation.subThingIdAndChannelMap.containsKey(subThingId)) {//检查是否在线，channel是否可用
					Channel channel = GatewayGlobalRelation.subThingIdAndChannelMap.get(subThingId);
					String channelId = channel.id().toString();
					if(channel.isActive()) {
						logger.info("当前网关对应子设备TCP连接活跃且可用" + channelId + ">>>下发命令子设备为  " + subThingId );
						ChannelUtil.getInstance().sendMessage(channel, commandStr, true);
					}
				} else {
					logger.info("当前网关对应子设备TCP连接不可用>>>下发命令子设备为  " + subThingId);
				}
			} catch (Exception e) {
				logger.error("下发失败 >>> " + subThingId);
				e.printStackTrace();
			}
	}
}
