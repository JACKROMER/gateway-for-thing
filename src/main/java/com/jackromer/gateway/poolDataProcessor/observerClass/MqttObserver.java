/*
* File name: handlerObserverable.java								
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
* 1.0			"zhouqiang"		Jul 26, 2018
* ...			...			...
*
***************************************************/

package com.jackromer.gateway.poolDataProcessor.observerClass;

/**
* @Description:	MQTT消息处理观察者接口
* @author: jackromer
* @version: 1.0, Jul 26, 2018
*/

public abstract interface MqttObserver {
	
	/**
	 * 处理MQTT消息
	* @Description:
	* @param gatewayId
	* @param message
	 */
	
	public void processMqttMessage(String gatewayId, String message);
}
