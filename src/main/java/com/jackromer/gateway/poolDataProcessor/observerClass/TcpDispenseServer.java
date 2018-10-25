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

public class TcpDispenseServer implements TcpObserverable{
	
	public static List<TcpObserver> list;//此List存储的是实现类的单例
    
    public TcpDispenseServer() {
        list = new ArrayList<TcpObserver>();
    }
    
    volatile private static TcpDispenseServer instance = null;
	
	public static TcpDispenseServer getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (TcpDispenseServer.class) {
				if(instance == null){//二次检查
					instance = new TcpDispenseServer();
				}
			}
		} 
		
		return instance;
	}
    /**
	 * @see com.sefon.gateway.poolDataProcessor.TcpObserverable#registerObserver(com.sefon.gateway.poolDataProcessor.TcpObserver)
	 */
	@Override
	public void registerObserver(TcpObserver o) {
		list.add(o);
	}

	/**
	 * @see com.sefon.gateway.poolDataProcessor.TcpObserverable#removeObserver(com.sefon.gateway.poolDataProcessor.TcpObserver)
	 */
	@Override
	public void removeObserver(TcpObserver o) {
		
		if(!list.isEmpty())
            list.remove(o);
	}

	/**
	 * @see com.sefon.gateway.poolDataProcessor.TcpObserverable#notifyObserver()
	 */
	@Override
	public String[] notifyObserver(String gatewayId, String message) {
		String[] result = new String[3];
		
		for(int i = 0; i < list.size(); i++) {
            TcpObserver oserver = list.get(i);
            String[] arr = oserver.processTcpMessage(gatewayId, message);
            if(null != arr) {
            	result = arr;
            	break;
			};
        }
		
		return result;
	}
	
}
