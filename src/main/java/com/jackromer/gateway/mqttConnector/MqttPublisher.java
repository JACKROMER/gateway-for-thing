/*
* File name: MqttPublisher.java								
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
* 1.0			"zhouqiang"		Jul 24, 2018
* ...			...			...
*
***************************************************/

package com.jackromer.gateway.mqttConnector;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackromer.gateway.gatewayServer.initGatewayPropertys.GatewayGlobalRelation;

/**
* @Description:	
* @author: jackromer
* @version: 1.0, Jul 24, 2018
*/

public class MqttPublisher {
	
	private static Logger logger = LoggerFactory.getLogger(MqttPublisher.class);
	
	volatile private static MqttPublisher instance = null;
	
	private MqttPublisher(){}
	 
	public static MqttPublisher getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (MqttPublisher.class) {
				if(instance == null){//二次检查
					instance = new MqttPublisher();
				}
			}
		} 
		
		return instance;
	}
	/**
	 * 发布消息/上报消息
	* @Description:synchronized
	* @param gatewayId
	* @param publishMessage
	* @param publishNum  0代表正常上报 1代表首次获取设备影子
	* @throws MqttPersistenceException
	* @throws MqttException
	 */
	public  synchronized void  publishMessage(String gatewayId, String publishMessage, int publishNum) throws MqttPersistenceException, MqttException {
		
		if (publishNum == 1) {//1代表首次获取设备影子信息
			logger.info("首次连接,PUBLISH 以 GET设备影子,分析是否有下发命令>>>" + publishMessage);
		} else {
			logger.info("准备正常发布消息 >>>" + publishMessage);
		}
		
		if (!GatewayGlobalRelation.gatewayIdAndClientMap.get(gatewayId).isConnected()) {//未连接则重连
			GatewayGlobalRelation.gatewayIdAndClientMap.get(gatewayId).reconnect();
			logger.info("MQTT 重连成功！");
			//重连后再次订阅
			
			//如果已经连接则订阅相关主题,只订阅一次
			String topicSubscribe = "sefon/v1/iot-hub/gateway/shadow/" + gatewayId;
			
			String[] topicFilters = new String[] { topicSubscribe };
			
			final int[] qos = { 0 };
			
			GatewayGlobalRelation.gatewayIdAndClientMap.get(gatewayId).subscribe(topicFilters, qos);
			
			logger.info("订阅成功！");
		}
		
		//通过gatewayID找到这个类并且发布数据
		final String topicPublish = "sefon/v1/things/gateway/shadow/" + gatewayId;// 主题 -sefon/v1/things/gateway/shadow/aiTMOQDix2qymFjL716f
		
		try {
			logger.info("publish Message---------------------------------------------------------------------------------------");
			if(GatewayGlobalRelation.gatewayIdAndClientMap.get(gatewayId).isConnected()) 
			GatewayGlobalRelation.gatewayIdAndClientMap.get(gatewayId).publish(topicPublish, publishMessage.getBytes(), 0, false);//不需要做持久化，只是发送数据
		} catch (Exception e) {
			logger.info("消息发布失败！>>>" + publishMessage);
			e.printStackTrace();
		}
		
	}

}
