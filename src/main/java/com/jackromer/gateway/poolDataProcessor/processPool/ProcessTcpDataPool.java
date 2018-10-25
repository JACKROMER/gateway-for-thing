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

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackromer.gateway.gatewayServer.GatewayBaseData;
import com.jackromer.gateway.poolDataProcessor.Threads.TcpConsumerThread;
import com.jackromer.gateway.poolDataProcessor.observerClass.TcpObserver;

import io.netty.channel.ChannelHandlerContext;

/***
 * 处理网关数据的线程池
 * 
 * @Description:
 * @author: jackromer
 * @version: 1.0, Jun 13, 2018
 */
public class ProcessTcpDataPool {
	
	volatile private static ProcessTcpDataPool instance = null;
	
	private ProcessTcpDataPool(){}
	 
	public static ProcessTcpDataPool getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (ProcessTcpDataPool.class) {
				if(instance == null){//二次检查
					instance = new ProcessTcpDataPool();
				}
			}
		} 
		
		return instance;
	}
	
	public static int  insertCount,consumerCount,publishCount ;//插入消息数量/消费消息数量/发布消息的次数
	
	//public static Queue<String>	queue		= new ConcurrentLinkedQueue<String>();////消息队列,此消息队列线程安全
	
	private static ExecutorService pool = null;//线程池
	
	private static final Logger	logger	= LoggerFactory.getLogger(ProcessTcpDataPool.class);
	
	private static final int corePoolSize = 9;//核心线程个数
	
	private static final int maxPoolSize = 100;//最大线程个数
	
	public static ThreadPoolExecutor tpe =  null;
	
	/**
	 * 存放活跃的TCP连接和子设备关系
	 */
	public static HashMap<String, String> channelIdAndSubThingIdMap =  new HashMap<String,String>();
	
	/**
	 * 初始化Netty-TCP 线程池
	* @Description:
	 */
	public void initPool() {
		try {
			ProcessTcpDataPool.pool =  new ThreadPoolExecutor(corePoolSize, maxPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100000));//使用无边界线程task队列
			tpe = ((ThreadPoolExecutor) pool);
			logger.info("线程池初始化完毕！");
		} catch (Exception e) {
			logger.info("线程池初始化失败！");
			e.printStackTrace();
			return;
		}
	}
	
	
	/**
	 * 调用线程池处理TCP消息
	* @Description:
	* @param channelName
	* @param gateWayId
	* @param message
	* @return
	* @throws Exception
	 */
	public   void processTcpMessage(ChannelHandlerContext ctx, GatewayBaseData gatewayBaseData, String message, TcpObserver tcpObserver) throws Exception {
		
			TcpConsumerThread consumerTask = new TcpConsumerThread(ctx, gatewayBaseData.getGatewayId(), message , tcpObserver);//有数据到达则调用callable去执行Task
			
			ProcessTcpDataPool.pool.submit(consumerTask); 
	}


	/**
	 * 从消息队列中取出消息链表FIFO原则
	* @Description:
	* @param queue
	* @return
	 */
	public synchronized static String get(Queue<String>	queue) {
		if (queue.isEmpty()) {
			return null;
		}
		return queue.poll();
	}
	
	
	
	public static void main(String[] args) throws Exception {
		
		
	}
}
