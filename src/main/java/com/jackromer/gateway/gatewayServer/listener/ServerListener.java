/*
 * File name: package-info.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 "zhouqiang" Jun 12, 2018
 * ... ... ...
 *
 ***************************************************/

/**
 * @Description:
 * @author: jackromer
 * @version: 1.0, Jun 12, 2018
 */

package com.jackromer.gateway.gatewayServer.listener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackromer.gateway.gatewayServer.GatewayDataChannelParameter;
import com.jackromer.gateway.gatewayServer.initializer.ServerChannelInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerListener {

	private static Logger								logger						= LoggerFactory.getLogger(ServerListener.class);

	volatile private static ServerListener				instance					= null;

	private ServerListener() {}
	
	public static ServerListener getInstance() {

		if (instance != null) {// 懒汉式

		} else {
			// 创建实例之前可能会有一些准备性的耗时工作
			synchronized (ServerListener.class) {
				if (instance == null) {// 二次检查
					instance = new ServerListener();
				}
			}
		}

		return instance;
	}


	/**
	 * 初始化网关的TCP监听,此方法将阻塞MAIN函数
	* @Description:
	* @param gatewayInitParameterList
	 */
	public void run(List<GatewayDataChannelParameter> gatewayInitParameterList) {

		for (int i = 0; i < gatewayInitParameterList.size(); i++) {
			// 下两句是设置reactor线程池
			EventLoopGroup acceptor = new NioEventLoopGroup();// 用于设置服务器端接受客户端的连接

			EventLoopGroup worker = new NioEventLoopGroup();// 用于网络事件处理

			ServerBootstrap bootstrap = new ServerBootstrap();// bootstrap用于设置服务端的启动相关参数

			bootstrap.option(ChannelOption.SO_BACKLOG, 1024);

			bootstrap.group(acceptor, worker);

			bootstrap.channel(NioServerSocketChannel.class);

			ServerChannelInitializer initializer = new ServerChannelInitializer(gatewayInitParameterList.get(i));// 初始化数据通道list

			bootstrap.childHandler(initializer);

			try {
				
				GatewayDataChannelParameter gatewayInitParameter = gatewayInitParameterList.get(i);

				Integer port = gatewayInitParameter.getGatewayBaseData().getPort();
				
				ChannelFuture channelFuture = bootstrap.bind(port).sync().addListener(new ChannelFutureListener() {// 绑定端口启动服务
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						logger.info("port is bound!");
					}
				});
				
				logger.info("bind to localhost port:" + port);
				
				if (i != gatewayInitParameterList.size() - 1) {

					channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {

						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							logger.info("channel初始化已完毕！");// 不会阻塞主线程
						}
					});
				} else {

					logger.info("初始化最后的initializer, 并启动监听!");
					channelFuture.channel().closeFuture().sync().addListener(new ChannelFutureListener() {

						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							logger.info("所有子线程结束,主线程结束！");// 正常状态此打印信息不执行上一行代码会阻塞MAIN函数
						}
					});
					
					 acceptor.shutdownGracefully();
					 
					 worker.shutdownGracefully();
					 
					 System.out.println("netty监听优雅的关闭！");
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}



	/**
	 * 初始化各个网关的TCP监听端口并监听TCP消息通知
	 * 
	 * @Description:
	 * @return
	 */
	public void initGateWayServerListener(List<GatewayDataChannelParameter> gatewayInitParameterList) {

		// 启动生产者
		try {
			logger.info("初始化监听端口开始！");
			
			run(gatewayInitParameterList);

		} catch (Exception e) {
			logger.info("初始化监听端口失败！");
			e.printStackTrace();
			return;
		}
	}

}