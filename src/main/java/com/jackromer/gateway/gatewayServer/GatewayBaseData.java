/*
* File name: BaseData.java								
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

/**
* @Description:	网关初始化基础数据类(包含字段：MQ服务器地址，对外提供的端口号，网关id，网关密码)
* @author: jackromer
* @version: 1.0, Jul 27, 2018
*/

public class GatewayBaseData {
	
	private String broker; //连接的MQTT服务器
	
	private Integer port; //该网关对外提供子设备连接的端口号
	
	private String gatewayId;//网关管理，IOT设备接入平台的网关ID
	
	private String gatewayPassword;//网关密码，IOT设备接入的网关密码

	/**
	 * @param broker
	 * @param port
	 * @param gatewayId
	 * @param gatewayPassword
	 */
	public GatewayBaseData(String broker, Integer port, String gatewayId, String gatewayPassword) {
		super();
		this.broker = broker;
		this.port = port;
		this.gatewayId = gatewayId;
		this.gatewayPassword = gatewayPassword;
	}

	/**
	 * @return the broker
	 */
	public String getBroker() {
		return broker;
	}

	/**
	 * @param broker the broker to set
	 */
	public void setBroker(String broker) {
		this.broker = broker;
	}

	/**
	 * @return the port
	 */
	public Integer getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * @return the gatewayId
	 */
	public String getGatewayId() {
		return gatewayId;
	}

	/**
	 * @param gatewayId the gatewayId to set
	 */
	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	/**
	 * @return the gatewayPassword
	 */
	public String getGatewayPassword() {
		return gatewayPassword;
	}

	/**
	 * @param gatewayPassword the gatewayPassword to set
	 */
	public void setGatewayPassword(String gatewayPassword) {
		this.gatewayPassword = gatewayPassword;
	}

	
	
	
}
