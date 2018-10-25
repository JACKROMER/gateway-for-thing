/*
 * File name: ConnectionMqtt.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Xu Apr 16, 2018 ... ... ...
 *
 ***************************************************/

package com.jackromer.gateway.mqttConnector.mqttInterface;

/**
 * @Description:MQTT连接接口
 * @author: jackromer
 * @version: 1.0, Apr 16, 2018
 */

public abstract interface ConnectMqttClientInterface{
	/**
	 * 创建MQTT连接
	* @Description:
	 */
	
	public void connectMqttBroker();
    
	/**
	 * 断开MQTT连接
	* @Description:
	 */
	
	public void disconnectMqttBroker();
	
	/**
	 * 重连MQTT连接
	* @Description:
	 */
	
	public void reconnectMqttBroker();
}
