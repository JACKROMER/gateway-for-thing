/*
* File name: TcpDispenseServer.java								
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

import java.util.ArrayList;
import java.util.List;

/**
* @Description:	
* @author: jackromer
* @version: 1.0, Jul 26, 2018
*/

public class MqttDispenseServer implements MqttObserverable{
	
	public static List<MqttObserver> list;
    
    public MqttDispenseServer() {
        list = new ArrayList<MqttObserver>();
    }
    
    volatile private static MqttDispenseServer instance = null;
	
	public static MqttDispenseServer getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (MqttDispenseServer.class) {
				if(instance == null){//二次检查
					instance = new MqttDispenseServer();
				}
			}
		} 
		
		return instance;
	}
    /**
	 * @see com.sefon.gateway.poolDataProcessor.TcpObserverable#registerObserver(com.sefon.gateway.poolDataProcessor.TcpObserver)
	 */
	@Override
	public void registerObserver(MqttObserver o) {
		list.add(o);
	}

	/**
	 * @see com.sefon.gateway.poolDataProcessor.TcpObserverable#removeObserver(com.sefon.gateway.poolDataProcessor.TcpObserver)
	 */
	@Override
	public void removeObserver(MqttObserver o) {
		
		if(!list.isEmpty())
            list.remove(o);
	}

	/**
	 * @see com.sefon.gateway.poolDataProcessor.TcpObserverable#notifyObserver()
	 */
	@Override
	public void notifyObserver(String gatewayId, String message) {
		for(int i = 0; i < list.size(); i++) {
            MqttObserver oserver = list.get(i);
            oserver.processMqttMessage(gatewayId, message);
        }
	}
}
