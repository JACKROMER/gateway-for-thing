/*
* File name: InitGateway.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			"zhouqiang"		Jul 19, 2018
* ...			...			...
*
***************************************************/

package com.jackromer.gateway.gatewayServer.initGatewayPropertys;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.paho.client.mqttv3.MqttClient;

import io.netty.channel.Channel;

/**
* @Description:	网关数据关系维护类
* @author: jackromer
* @version: 1.0, Jul 19, 2018
*/

public class GatewayGlobalRelation {
	
	public static ConcurrentHashMap<String, MqttClient> gatewayIdAndClientMap =  new ConcurrentHashMap<>();//网关和MqttClient关系MAP
	
	public static ConcurrentHashMap<String, Channel>	subThingIdAndChannelMap	= new ConcurrentHashMap<String, Channel>();//网关ID和channel的关系MAP
	
}
