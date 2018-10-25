/*
* File name: GatewayInitParameter.java								
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
* 1.0			"zhouqiang"		Jul 27, 2018
* ...			...			...
*
***************************************************/

package com.jackromer.gateway.gatewayServer;

import com.jackromer.gateway.mqttConnector.mqttInterface.ConnectMqttClientImpl;

import io.netty.channel.SimpleChannelInboundHandler;

/**
* @Description:	初始化数据通道的参数类
* @author: jackromer
* @version: 1.0, Jul 27, 2018
*/

public class GatewayDataChannelParameter {

	private GatewayBaseData gatewayBaseData;
	
	private SimpleChannelInboundHandler<String> simpleChannelInboundHandler;
	
	private ConnectMqttClientImpl connectMqttClientImpl;//网关连接实现类

	

	/**
	 * @param gatewayBaseData
	 * @param simpleChannelInboundHandler
	 * @param connectMqttClientImpl
	 */
	
	public GatewayDataChannelParameter(GatewayBaseData gatewayBaseData,
			SimpleChannelInboundHandler<String> simpleChannelInboundHandler,
			ConnectMqttClientImpl connectMqttClientImpl) {
		super();
		this.gatewayBaseData = gatewayBaseData;
		this.simpleChannelInboundHandler = simpleChannelInboundHandler;
		this.connectMqttClientImpl = connectMqttClientImpl;
	}

	
	/**
	 * @return the gatewayBaseData
	 */
	public GatewayBaseData getGatewayBaseData() {
		return gatewayBaseData;
	}

	/**
	 * @param gatewayBaseData the gatewayBaseData to set
	 */
	public void setGatewayBaseData(GatewayBaseData gatewayBaseData) {
		this.gatewayBaseData = gatewayBaseData;
	}

	/**
	 * @return the simpleChannelInboundHandler
	 */
	public SimpleChannelInboundHandler<String> getSimpleChannelInboundHandler() {
		return simpleChannelInboundHandler;
	}

	/**
	 * @param simpleChannelInboundHandler the simpleChannelInboundHandler to set
	 */
	public void setSimpleChannelInboundHandler(SimpleChannelInboundHandler<String> simpleChannelInboundHandler) {
		this.simpleChannelInboundHandler = simpleChannelInboundHandler;
	}

	/**
	 * @return the connectMqttClientImpl
	 */
	public ConnectMqttClientImpl getConnectMqttClientImpl() {
		return connectMqttClientImpl;
	}

	/**
	 * @param connectMqttClientImpl the connectMqttClientImpl to set
	 */
	public void setConnectMqttClientImpl(ConnectMqttClientImpl connectMqttClientImpl) {
		this.connectMqttClientImpl = connectMqttClientImpl;
	}

	
	
}
