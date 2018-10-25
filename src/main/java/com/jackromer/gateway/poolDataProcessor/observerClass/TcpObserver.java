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
* @Description:	TCP消息处理观察者接口
* @author: jackromer
* @version: 1.0, Jul 26, 2018
*/

public abstract interface TcpObserver {
	
	/**
	 * 处理上报消息
	* @Description:多个线程调用该方法,数据保存在不同的线程中,不需要做互斥处理
	* @param gatewayId
	* @param message
	* @return reslutArray, 长度3, 【subThingId, 需要上报的设备影子数据流JSON, 需要回复设备的数据reportReply[ps:无需回复时返回null]】
	 */
	
	public String[] processTcpMessage(String gatewayId, String message);
}
