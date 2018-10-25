/*
 * File name: package-info.java
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

/**
 * @Description:
 * @author: jackromer
 * @version: 1.0, Jul 13, 2018
 */

package com.jackromer.gateway.poolDataProcessor.Threads;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackromer.gateway.gatewayServer.GatewayBaseData;
import com.jackromer.gateway.poolDataProcessor.observerClass.MqttObserver;

/**
 * @Description:MQTT消息处理线程
 * @author: jackromer
 * @version: 1.0, Jun 27, 2018
 */

public class MqttConsumerThread implements Callable<String> {

	public static final Logger	loggerConsumer	= LoggerFactory.getLogger(MqttConsumerThread.class);

	private GatewayBaseData		gatewayBaseData;

	private MqttObserver		mqttObserver;//mqtt消息处理实现类

	private String				mqttMessage;



	/**
	 * @param gatewayBaseData
	 * @param tcpObserver
	 * @param mqttMessage2
	 */
	public MqttConsumerThread(GatewayBaseData gatewayBaseData, MqttObserver mqttObserver, String mqttMessage) {
		this.gatewayBaseData = gatewayBaseData;
		this.mqttObserver = mqttObserver;
		this.mqttMessage = mqttMessage;
	}



	@Override
	public String call() throws Exception {// 错误返回null
		try {
			if (mqttMessage != null && !mqttMessage.isEmpty()) {

				loggerConsumer.info("消费的消息为：" + mqttMessage);

				mqttObserver.processMqttMessage(gatewayBaseData.getGatewayId(), mqttMessage);// 具体的实现类处理数据

			}
		} catch (Exception e) {
			loggerConsumer.info("下发命令解析异常>>>" + mqttMessage);
			e.printStackTrace();
		}

		return null;
	}

}
