package com.jackromer.gateway.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class ChannelUtil {
	
	private static Logger logger = LoggerFactory.getLogger(ChannelUtil.class);
	
	volatile private static ChannelUtil instance = null;
	
	private ChannelUtil(){}
	 
	public static ChannelUtil getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (ChannelUtil.class) {
				if(instance == null){//二次检查
					instance = new ChannelUtil();
				}
			}
		} 
		
		return instance;
	}
	
	/**
	 * 
	* @Description:synchronized
	* @param channel 对应的channel
	* @param msg 发送的消息
	* @param commandOrreply true为下发命令, false为回复子设备数据数据
	 */
	public void sendMessage(Channel channel, final String msg, final boolean commandOrreply){
		
		final String channelId = channel.id().toString();
		
		if(channel.isActive()) {
			if(channel.isWritable()) {//可写状态,最好是在channel没有上报数据时在下发
				//真正的发送数据
				if(!commandOrreply) {
					logger.info(channelId + " 回复设备数据开始 "+ msg);
				}else {
					logger.info(channelId + " 下发命令开始 " + msg);
				}
				
				ChannelFuture channelFuture = channel.writeAndFlush(msg);
				
				channelFuture.addListener(new ChannelFutureListener() {
					
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {//IO监听
						
						if(future.isSuccess()) {
							if(!commandOrreply) {
								logger.info(channelId + " 回复设备数据成功" +  msg);
							}else {
								logger.info(channelId + " 下发命令成功" + msg);//channel仍然存在下发开锁命令成功但是社保锁未正常驱动,等下一次上报获取新的channel
							}
						}else {
							if(!commandOrreply) {
								logger.info(channelId + " 回复设备数据失败 " +  msg);
							}else {
								logger.info(channelId + " 下发命令失败 " + msg);
							}
						}
						
						Throwable error = future.cause();//捕捉IO错误信息
						
						if(null != error) {
							logger.info("执行IO操作报错" + error.getCause().getMessage());
						}
					}
				});
			}else {
				logger.info("当前channel不可写" + channel.id().toString());
			}
		}
	}
	
	

	/**
	 * 关闭channel
	 *
	 * @Description:
	 * @param channel
	 */
	public  void closeChannel(Channel channel) {

		ChannelFuture future = channel.close();// 之前所有包含subThingId的channel全部关闭

		final String channelId = channel.id().toString();

		future.addListener(new ChannelFutureListener() {// 添加监听

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					logger.info(channelId + " 关闭成功！");
				} else {
					logger.info(channelId + " 关闭失败！");
				}
			}

		});
		Throwable error = future.cause();

		if (null != error) {
			logger.info("关闭错误：" + error.getCause().getMessage());
		}
	}
}
