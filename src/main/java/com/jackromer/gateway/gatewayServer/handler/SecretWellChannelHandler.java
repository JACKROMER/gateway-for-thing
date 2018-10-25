package com.jackromer.gateway.gatewayServer.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackromer.gateway.gatewayServer.GatewayBaseData;
import com.jackromer.gateway.poolDataProcessor.observerClass.TcpObserver;
import com.jackromer.gateway.poolDataProcessor.processPool.ProcessTcpDataPool;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
* @Description:	处理密码设备的TCP包数据
* @author: jackromer
* @version: 1.0, Jul 24, 2018
*/

@Sharable//可共享的CHANNEL
public class SecretWellChannelHandler extends SimpleChannelInboundHandler<String> {

    private static Logger logger = LoggerFactory.getLogger(SecretWellChannelHandler.class);

    private StringBuffer channelBuffer = new StringBuffer();// 每次TCP连接会创建一个CHANNELHANDLER
	
	private GatewayBaseData baseData;
	
	private TcpObserver tcpObserver;
	
	public SecretWellChannelHandler(GatewayBaseData baseData, TcpObserver tcpObserver) {
		this.baseData = baseData;
		this.tcpObserver = tcpObserver;
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        final String regex = "\\*[0-9]{15}\\*\\S*#";

        try {

            CharSequence initialResult = CharSequenceValueConverter.INSTANCE.convertObject(msg);// MSG先转为charSequence

            ByteBuf result = Unpooled.copiedBuffer(initialResult, CharsetUtil.UTF_8);// 再转为byteBuf

            logger.info("客户端IP地址为：" + ctx.channel().remoteAddress().toString() + "数据为：" + result.toString(CharsetUtil.UTF_8));

            String reportStr = result.toString(CharsetUtil.UTF_8);

            channelBuffer.append(reportStr);// 每次都应该追加同一个channelId的StringBuffer中

            result.release();// 释放资源

            String resultStr = channelBuffer.toString();//获取当前的buffer
            
            //判断数据是否完整
            Matcher matcher = Pattern.compile(regex).matcher(resultStr);
            
            boolean wellMatchFlag = false;// 是否匹配到通用协议正则的标志
            
            while (matcher.find()) {// 匹配正则表达式
            	
            	resultStr = matcher.group(0);
            	
            	int start = matcher.start();
            	
                channelBuffer = channelBuffer.delete(0, start + resultStr.length());//清除完整数据段,保留剩余数据
                
                wellMatchFlag = true ;
                
                break;
            }
            
            if(wellMatchFlag) {  
            	ProcessTcpDataPool.getInstance().processTcpMessage(ctx, baseData, resultStr, tcpObserver);
            } else {//未匹配到,则继续拼接数据直到匹配为止
            	if (channelBuffer.length() > 300) {//如果大于300还未匹配到一个可用的正则字符串，清空无效buffer
            		channelBuffer.setLength(0);
            	}
            };
            
        } catch (Exception e) {
            logger.error("数据处理异常,清空当前channelBuffer!" + channelBuffer.toString());
            channelBuffer.setLength(0);// 清空当前channelBuffer
            e.printStackTrace();
        }

    }


    // 当新连接接入
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info(ctx.channel().id().toString() + "新连接加入！MAP-SIZE:");
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

            closeChannel(ctx.channel());

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
        logger.info("TCP---" + ctx.channel().id().toString() + "---客户端已连接！channel is active MAP-SIZE:");
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	channelBuffer.setLength(0);//连接断开的时清掉
        logger.info(ctx.channel().id().toString() + "客户端对应的channel is not active!");
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("" + " 出现异常信息-LocalizedMessage.ERROR" + cause);
    }


    /**
     * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext,
     * java.lang.Object)
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // TODO Auto-generated method stub

    }


    /**
     * @param ctx
     * @param baseLogStr 基础日志信息
     * @Description:打印断开的日志信息
     */

    public static void logTcpInfo(ChannelHandlerContext ctx, String baseLogStr) {

        String channelId = ctx.channel().id().toString();// 当前超时的channelId
        logger.info(baseLogStr + "TCP连接断开： [" + channelId + "]-MAP-SIZE:");

    }


    /**
     * 关闭channel
     *
     * @param channel
     * @Description:
     */
    public synchronized static void closeChannel(Channel channel) {

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


    public static void main(String[] args) {

        // 正则表达式[a-zA-Z]{1}$
        /*Matcher matcher1 = Pattern.compile("^[0-9]{2}[a-zA-Z]{1}$").matcher("600");// 设备topic
        Matcher matcher2 = Pattern.compile("^[0-9]{3}").matcher("056SSS");// 设备topic

        System.out.println(matcher1.matches());
        System.out.println(matcher2.matches());*/
    	//final String regex = "^\\*[0-9]{15}\\*{1}[a-zA-Z\\d=]+#$";
    	final String regex = "\\*[0-9]{15}\\*\\S*#";
    	String resultStr = "dsafdasfasdfasdf*460029961128085*UlZAUkdcBwoJBB0DAxgHBhsKCBwBAgEHGUVDWU1VakYfARsAGwkNHFhEH1cZBAcJAQAGAwUFBQUHCA0cV28=#";
    	System.out.println(resultStr);
    	Matcher matcher = Pattern.compile(regex).matcher(resultStr);
    	while(matcher.find()) {
    		System.out.println(matcher.group(0));
    		System.out.println(matcher.start());
    		break;
    	}
    	System.out.println("done!");
        
    }
}