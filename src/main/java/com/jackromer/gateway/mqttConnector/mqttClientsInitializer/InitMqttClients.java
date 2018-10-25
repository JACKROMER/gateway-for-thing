/*
* File name: InitMqttClients.java								
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

package com.jackromer.gateway.mqttConnector.mqttClientsInitializer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackromer.gateway.gatewayServer.GatewayDataChannelParameter;
import com.jackromer.gateway.gatewayServer.initGatewayPropertys.GatewayGlobalRelation;
import com.jackromer.gateway.mqttConnector.mqttInterface.ConnectMqttClientImpl;

/**
* @Description:	
* @author: jackromer
* @version: 1.0, Jul 19, 2018
*/

public class InitMqttClients {

	private final static Logger logger =  LoggerFactory.getLogger(InitMqttClients.class);
	
	volatile private static InitMqttClients instance = null;
	
	private InitMqttClients(){}
	 
	public static InitMqttClients getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (InitMqttClients.class) {
				if(instance == null){//二次检查
					instance = new InitMqttClients();
				}
			}
		} 
		
		return instance;
	}
	
	/** 通过传入参数创建多个网关MQTT连接
	 * 
	* @Description:
	* @param gatewayInitParameterList
	 */
	
	public void createMqttConnectClients(List<GatewayDataChannelParameter> gatewayInitParameterList) {
		
		try {
			for (GatewayDataChannelParameter gatewayChannelParameter : gatewayInitParameterList) {
				
				ConnectMqttClientImpl connectMqttClientImpl = gatewayChannelParameter.getConnectMqttClientImpl();//MQTT连接实现类
				
				connectMqttClientImpl.connectMqttBroker();//连接MQTT服务器创建MQTT连接
				
				String gatewayId = gatewayChannelParameter.getGatewayBaseData().getGatewayId();
				
				GatewayGlobalRelation.gatewayIdAndClientMap.put(gatewayId, connectMqttClientImpl.getMqttClient());//维护网关和MQTTCLIENT MAP的关系
				
				if(connectMqttClientImpl.getMqttClient().isConnected()) {
					
					logger.error("MQTT连接已经断开,将尝试重连！");
					
					//如果已经连接则订阅相关主题,只订阅一次
					String topicSubscribe = "sefon/v1/iot-hub/gateway/shadow/" + gatewayId;
					
					String[] topicFilters = new String[] { topicSubscribe };
					
					final int[] qos = { 0 };
					
					connectMqttClientImpl.getMqttClient().subscribe(topicFilters, qos);
					
					logger.info("订阅成功！");
				}
			}
		} catch (Exception e) {
			logger.error("创建MQTT连接错误！");
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		Class<?> c = Class.forName("com.sefon.gateway.gatewayServer.handler.CurrencyChannelHandler");
		System.out.println(c.getName());
	}
}
