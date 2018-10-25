package com.jackromer.gateway.gatewayServer.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackromer.gateway.gatewayServer.GatewayBaseData;
import com.jackromer.gateway.gatewayServer.initGatewayPropertys.GatewayGlobalRelation;
import com.jackromer.gateway.poolDataProcessor.observerClass.TcpObserver;
import com.jackromer.gateway.poolDataProcessor.processPool.ProcessTcpDataPool;
import com.jackromer.gateway.utils.ChannelUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
* @Description:	处理通用设备协议的TCP包数据
* @author: jackromer
* @version: 1.0, Jul 24, 2018
*/

@Sharable//可共享的CHANNEL
public class CurrencyChannelHandler extends SimpleChannelInboundHandler<String> {

			
	private static Logger									logger						= LoggerFactory.getLogger(CurrencyChannelHandler.class);

	private StringBuffer									channelBuffer				= new StringBuffer();// 每次TCP连接会创建一个CHANNELHANDLER

	private GatewayBaseData baseData;
	
	private TcpObserver tcpObserver;//Tcp消息接口
	
	public CurrencyChannelHandler(GatewayBaseData baseData, TcpObserver tcpObserver) {
		this.baseData = baseData;
		this.tcpObserver = tcpObserver;
	}
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		try {
			if (ctx.channel().isOpen()) {
				
				CharSequence initialResult = CharSequenceValueConverter.INSTANCE.convertObject(msg);// MSG先转为charSequence

				ByteBuf result = Unpooled.copiedBuffer(initialResult, CharsetUtil.UTF_8);// 再转为byteBuf

				System.out.println("hello I'am currency handler: " + result.toString(CharsetUtil.UTF_8));

				if (result.isReadable() && ctx.channel().isActive() && result.array().length > 0) {

					String reportStr = result.toString(CharsetUtil.UTF_8);

					String resultStr = "";

					channelBuffer.append(reportStr);// 每次都应该追加同一个channelId的StringBuffer中

					result.release();// reportStr使用后立马释放资源

					if (channelBuffer.length() < 3) {//错误数据
						logger.error("错误数据为>>>：" + reportStr);
						return;
					}


					Matcher currencyMatcher = Pattern.compile("^[0-9]{3}").matcher(reportStr); //channelBuffer长度大于等于3则可参与正则匹配

					boolean currencyMatchFlag = false;// 是否匹配到通用协议正则的标志

					String realHeader = "";//初始化消息头

					while (currencyMatcher.find()) {// 匹配正则表达式
						currencyMatchFlag = true;
						realHeader = currencyMatcher.group(0);
						break;
					}

					if(!currencyMatchFlag) {//正则未匹配到消息头如050,是错误消息,channelBuffer清空
						logger.info("数据处理异常,正则表达式未匹配!");
						channelBuffer.setLength(0);
						return ;
					}

					// 如果没有return则证明数据长度超过了3且正则表达式已匹配035
					String normalResultStr = channelBuffer.toString();

					// 暂时清空channelBuffer,等待下面的校验
					channelBuffer.setLength(0);

					int payloadLength = Integer.parseInt(realHeader);// 获取payload的长度

					String payload = normalResultStr.substring(3);

					if (payload.length() != payloadLength) {// 处理异常的数据包

						if (payload.length() > payloadLength) {// 大于length代表多数据

							String overPayload = payload;// 超出的payload

							payload = overPayload.substring(0, payloadLength);

							String lastPayload = overPayload.substring(payloadLength);// 剩余部分放入buffer中

							channelBuffer.append(lastPayload);

						} else {
							channelBuffer.append(normalResultStr);// 少数据,重新放入清空的channelBuffer中待下一次使用

							return;
						}
					}

					resultStr = realHeader + payload;// 完整数据包
					
					// 全部交给线程处理

					ProcessTcpDataPool.getInstance().processTcpMessage(ctx, baseData, resultStr, tcpObserver);
				
				} else {
					logger.info(ctx.channel().id().toString() + "此channel为不可用、不可读状态，或者其读取的参数为空！");
				}
			}
		} catch (Exception e) {
			logger.error("数据处理异常,清空当前channelBuffer!" + channelBuffer.toString());
			channelBuffer.setLength(0);// 清空当前channelBuffer
			e.printStackTrace();
		}
	}



	// 当新连接接入
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		logger.info(ctx.channel().id().toString() + "新连接加入！MAP-SIZE:" + GatewayGlobalRelation.subThingIdAndChannelMap.size());
	}



	/**
	 * 一段时间未进行读写操作 回调函数
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent idleStateEvent = (IdleStateEvent) evt;

			String eventType = null;
			switch (idleStateEvent.state()) {
				case READER_IDLE:
					eventType = "读空闲";
					break;
				case WRITER_IDLE:
					eventType = "写空闲";
					break;
				case ALL_IDLE:
					eventType = "读写空闲";
					break;
			}

			String baseLogStr = ctx.channel().id().toString() + "超时事件:" + eventType;

			logTcpInfo(ctx, baseLogStr);// 从MAP中移除对应的channel

			ChannelUtil.getInstance().closeChannel(ctx.channel());

		}
	}


	// 当连接断开-只断开连接
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

		String baseLogStr = ctx.channel().id().toString() + "连接断开";

		logTcpInfo(ctx, baseLogStr);
	}



	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("TCP---" + ctx.channel().id().toString() + "---客户端已连接！channel is active MAP-SIZE:"
				+ GatewayGlobalRelation.subThingIdAndChannelMap.size());
	}



	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info(ctx.channel().id().toString() + "客户端对应的channel is not active!");
	}



	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.info("" + " 出现异常信息-LocalizedMessage.ERROR" + cause);
	}



	/**
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext,
	 *      java.lang.Object)
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		// TODO Auto-generated method stub

	}



	/**
	 * @Description:打印断开的日志信息
	 * @param ctx
	 * @param baseLogStr
	 *            基础日志信息
	 */

	public static void logTcpInfo(ChannelHandlerContext ctx, String baseLogStr) {

		String channelId = ctx.channel().id().toString();// 当前超时的channelId
		logger.info(baseLogStr + "TCP连接断开： [" + channelId + "]-MAP-SIZE:" + GatewayGlobalRelation.subThingIdAndChannelMap.size());

	}


	public static void main(String[] args) {

		// 正则表达式[a-zA-Z]{1}$
		Matcher matcher1 = Pattern.compile("^[0-9]{2}[a-zA-Z]{1}$").matcher("600");// 设备topic
		Matcher matcher2 = Pattern.compile("^[0-9]{3}").matcher("056SSS");// 设备topic

		System.out.println(matcher1.matches());
		System.out.println(matcher2.matches());
	}

}