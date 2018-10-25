/*
 * File name: package-info.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 "zhouqiang" Jun 13, 2018
 * ... ... ...
 *
 ***************************************************/

/**
 * @Description:
 * @author: jackromer
 * @version: 1.0, Jun 13, 2018
 */

package com.jackromer.gateway.poolDataProcessor.processPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackromer.gateway.gatewayServer.GatewayBaseData;
import com.jackromer.gateway.poolDataProcessor.Threads.MqttConsumerThread;
import com.jackromer.gateway.poolDataProcessor.observerClass.MqttObserver;

/***
 * 处理MQTT返回消息的线程池
 * 
 * @Description:
 * @author: jackromer
 * @version: 1.0, Jun 13, 2018
 */
public class ProcessArrivalMessagePool {
	
	
	private static ExecutorService pool = null;//线程池
	
	private static final Logger	logger	= LoggerFactory.getLogger(ProcessArrivalMessagePool.class);
	
	private static final int corePoolSize = 9;//核心线程个数
	
	private static final int maxPoolSize = 100;//最大线程个数
	
	//private static final String gatewayIdNode = "gatewayId";//最大线程个数
	
	public static ThreadPoolExecutor tpe =  null;
	
	volatile private static ProcessArrivalMessagePool instance = null;
	
	private ProcessArrivalMessagePool(){}
	 
	public static ProcessArrivalMessagePool getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (ProcessArrivalMessagePool.class) {
				if(instance == null){//二次检查
					instance = new ProcessArrivalMessagePool();
				}
			}
		} 
		
		return instance;
	}
	
	
	public static void main(String[] args) throws Exception {
		//初始化线程池
		//initPool();
	}
	
	
	/**
	 * 初始化MQTT消息处理线程池
	* @Description:
	 */
	public void initPool() {
		try {
			ProcessArrivalMessagePool.pool =  new ThreadPoolExecutor(corePoolSize, maxPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100000));//使用无边界线程task队列
			tpe = ((ThreadPoolExecutor) pool);
			logger.info("线程池初始化完毕！");
		} catch (Exception e) {
			logger.info("线程池初始化失败！");
			e.printStackTrace();
			return;
		}
	}
	
	
	/**
	 * 调用线程池处理MQTT消息
	* @Description:
	* @param channelName
	* @param gateWayId
	* @param message
	* @return
	* @throws Exception
	 */
	public void processArrivalMqttMessage(GatewayBaseData gatewayBaseData, MqttObserver mqttObserver, String mqttMessage)  {
		
		try {
			
			//JSONObject messageJson = JSONObject.parseObject(mqttMessage);
			
			//String gatewayId = messageJson.getString(gatewayIdNode);
			
			MqttConsumerThread consumerTask = new MqttConsumerThread(gatewayBaseData, mqttObserver, mqttMessage);//有数据到达则调用callable去执行Task
			
			ProcessArrivalMessagePool.pool.submit(consumerTask);

		} catch (Exception e) {
			logger.info("处理MQTT消息异常！" + mqttMessage);
			e.printStackTrace();
		} 
			
	}

}
