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
* @Description:	观察者接口
* @author: jackromer
* @version: 1.0, Jul 26, 2018
*/

public interface MqttObserverable {
	
	public void registerObserver(MqttObserver o);
	
    public void removeObserver(MqttObserver o);
    
    public void notifyObserver(String gatewayId, String message);
}
