package com.jackromer.gateway.gatewayServer.initializer;

import java.util.concurrent.TimeUnit;

import com.jackromer.gateway.gatewayServer.GatewayDataChannelParameter;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class ServerChannelInitializer extends  ChannelInitializer<SocketChannel>  {

	
	private GatewayDataChannelParameter gatewayInitParameter;
	
    
	/**
	 * @param gatewayInitParameter
	 */
	public ServerChannelInitializer(GatewayDataChannelParameter gatewayInitParameter) {
		super();
		this.gatewayInitParameter =  gatewayInitParameter;
	}


	@Override
    protected void initChannel(SocketChannel arg0) throws Exception {
    	
        ChannelPipeline pipeline = arg0.pipeline();
        
        pipeline.addLast("docode",new StringDecoder());//添加的顺序非常重要
        
        pipeline.addLast("encode",new StringEncoder());
        
        pipeline.addLast(new IdleStateHandler(60 * 3, 60 * 10 , 60 * 10 ,TimeUnit.SECONDS));//设置超时时间180秒
        
        System.out.println("Process TCP Message >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\r\n");

        String remoteAddress = arg0.remoteAddress().toString();//设备IP
		
		String socketAddr = arg0.localAddress().toString();//本地Ip-端口
		
		int localPort  = Integer.parseInt(socketAddr.split(":")[1]);
		
		System.out.println("Local-Port: " + localPort + "Received " + remoteAddress + " MSG!");
		
		SimpleChannelInboundHandler<String> handler = gatewayInitParameter.getSimpleChannelInboundHandler();
		
		if(null != handler)  pipeline.addLast("server", handler); //处理空指针异常,必须保证观察者中只返回一个对象
		

    }


}