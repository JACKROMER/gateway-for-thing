/*
* File name: Test.java								
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

package com.jackromer.gateway.mqttConnector.mqttClients;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackromer.gateway.gatewayServer.GatewayBaseData;
import com.jackromer.gateway.mqttConnector.mqttInterface.ConnectMqttClientImpl;
import com.jackromer.gateway.poolDataProcessor.observerClass.MqttObserver;
import com.jackromer.gateway.poolDataProcessor.processPool.ProcessArrivalMessagePool;

/**
* @Description:	网关连接类
* @author: jackromer
* @version: 1.0, Jul 19, 2018
*/

public class CurrencyGateWayClient extends ConnectMqttClientImpl{
	
	
	private static final Logger	logger	= LoggerFactory.getLogger(CurrencyGateWayClient.class);
	
	/**
	 * 初始化网关MQTT连接
	 * @param mqttClient
	 * @param gatewayBaseData
	 * @param mqttObserver
	 */
	
	public CurrencyGateWayClient(MqttClient mqttClient, final GatewayBaseData gatewayBaseData, final MqttObserver mqttObserver) {
		
		super(mqttClient, new MqttCallbackExtended() {
			
			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
					System.out.println("\r\n--------------------------------------------------------------------------------------\r\n");
					logger.info("@---通用协议messageArrived---"  +"@"+ new String(message.getPayload()));
					logger.info("@---Handling MQTT message !--------------------------------------");
					ProcessArrivalMessagePool.getInstance().processArrivalMqttMessage(gatewayBaseData,  mqttObserver, new String(message.getPayload()));
			}
			
			
			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {
				logger.info("消息传输完成：" + token.isComplete());
			}
			
			
			@Override
			public void connectionLost(Throwable cause) {
				logger.error("@---connectionLost---@" + System.currentTimeMillis());//MQTT连接断开
				logger.error(">>>" + cause);//MQTT连接断开
				connectMqttFlag = false;
			}
			
			
			
			@Override
			public void connectComplete(boolean reconnect, String serverURI) {
				// 连接成功，需要上传客户端所有的订阅关系,在连接建立后再订阅
				logger.info("MQTT连接成功！" + gatewayBaseData.getGatewayId());
			}
		}, gatewayBaseData);
	}

	
	public static void main(String[] args) {
		//"tcp://131.10.10.201:1883", "aiTMOQDix2qymFjL716f", "xC8+jmOmuAxEUpv4ob1riO20", "aiTMOQDix2qymFjL716g" ,null
		
		//new CurrencyGateWayClient("tcp://131.10.10.201:1883", "aiTMOQDix2qymFjL716f", "xC8+jmOmuAxEUpv4ob1riO20", "aiTMOQDix2qymFjL716g").connectMqttBroker();
	}
	
	
}
