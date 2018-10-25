package com.jackromer.gateway;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.jackromer.gateway.gatewayServer.GatewayBaseData;
import com.jackromer.gateway.gatewayServer.GatewayDataChannelParameter;
import com.jackromer.gateway.gatewayServer.GatewayPropertyInitializer;
import com.jackromer.gateway.gatewayServer.handler.CurrencyChannelHandler;
import com.jackromer.gateway.gatewayServer.handler.SecretWellChannelHandler;
import com.jackromer.gateway.gatewayServer.listener.ServerListener;
import com.jackromer.gateway.mqttConnector.mqttClients.CurrencyGateWayClient;
import com.jackromer.gateway.mqttConnector.mqttClients.SecretWellGateWayClient;
import com.jackromer.gateway.mqttConnector.mqttClientsInitializer.InitMqttClients;
import com.jackromer.gateway.mqttConnector.mqttInterface.ConnectMqttClientImpl;
import com.jackromer.gateway.poolDataProcessor.processPool.ProcessArrivalMessagePool;
import com.jackromer.gateway.poolDataProcessor.processPool.ProcessTcpDataPool;
import com.jackromer.gateway.thingProtocols.processCurrencyProrocol.mqttMessageProcessor.CurrencyMqttMessageProcessor;
import com.jackromer.gateway.thingProtocols.processCurrencyProrocol.tcpMessageProcessor.CurrencyTcpMessageProcessor;
import com.jackromer.gateway.thingProtocols.processSZSecretWellProtocol.mqttMessageProcessor.SecretWellMqttMessageProcessor;
import com.jackromer.gateway.thingProtocols.processSZSecretWellProtocol.tcpMessageProcessor.SecretWellTcpMessageProcessor;

import io.netty.channel.SimpleChannelInboundHandler;

/**
* @Description:	项目启动类, 面向接口, 根据不同的网关交给不同的实现类处理数据, 提前建立数据通道.
* @author: jackromer
* @version: 1.0, Jul 31, 2018
*/
@SpringBootApplication
public class GatewayApplicationStarter { 

	private static final Logger logger = LoggerFactory.getLogger(GatewayApplicationStarter.class);


	public static void main(String[] args) throws MqttException {//args传递初始化配置文件地址

		for (int i = 0; i< args.length; i++) {
			System.out.println("参数" + i + ":" + args[i]);
		}
		
		//获取各个初始化类单例
		
		InitMqttClients connectMqttClientInstence 			= InitMqttClients.getInstance();
		
		ProcessTcpDataPool processPoolInstence 				= ProcessTcpDataPool.getInstance();

		ProcessArrivalMessagePool processMqttPoolInstence 	= ProcessArrivalMessagePool.getInstance();

		ServerListener serverListenerInstence 				= ServerListener.getInstance();
		
		//初始化基础数据List
		
		List<GatewayDataChannelParameter> gatewayInitParameterList = new ArrayList<GatewayDataChannelParameter>();//初始化网关数据通道LIST
		
		
		//判断有无初始化参数
		String propertyFileDir = "config/baseGateways.properties";
		
		if(args.length > 0) {
			propertyFileDir = args[0];
		}
		
		List<GatewayBaseData> list = GatewayPropertyInitializer.initGatewayProperty(propertyFileDir);//从指定配置文件初始化启动参数
		
		for (int i = 0; i < list.size(); i++) {
			
			GatewayDataChannelParameter gatewayChannelParameter = null;
			
			GatewayBaseData gatewayBaseData = list.get(i);
			
			Integer port = gatewayBaseData.getPort();
			
			/**
			 * 通过端口号初始化消息通道-需要指定配置,其他网关请使用不同端口号对外提供，并在初始化期间绑定该网关对应协议的消息处理类（ps:currencyTcpMessageProcessor）
			 */
			
			switch (port) {
			
				case 8888:
					
					CurrencyTcpMessageProcessor  currencyTcpMessageProcessor 	= CurrencyTcpMessageProcessor.getInstance();//TCP消息处理实现类
					
					CurrencyMqttMessageProcessor currencyMqttMessageProcessor 	= CurrencyMqttMessageProcessor.getInstance();//MQTT消息处理实现类
					
					/**
					 * 初始化TCP-CHANNEL消息处理类（currencyChannelHandler）并绑定该网关对应的协议解析类（currencyTcpMessageProcessor）.
					 * 当该handler收到消息后将消息提交到TCP消息处理线程池由线程池处理.
					 * TcpMessageProcessor的主要工作是解析消息（包括设备上报的消息和回复的消息）.
					 */
					SimpleChannelInboundHandler<String> currencyChannelHandler 	= new CurrencyChannelHandler(gatewayBaseData, currencyTcpMessageProcessor);
					
					//MQTT连接
					
					String currencyTimestamp 		= ConnectMqttClientImpl.getNowTimeStamp();
					
					String currencyTrueClientId 	= gatewayBaseData.getGatewayId() + "&" + currencyTimestamp;//密码校验
					
					MqttClient currencyMqttClient 	= new MqttClient(gatewayBaseData.getBroker(), currencyTrueClientId, new MemoryPersistence());//初始化MQTT客户端连接
					
					/**
					 *  初始化MQTT连接CLIENT(CurrencyGateWayClient), 绑定MQTT消息处理类(currencyMqttMessageProcessor).
					 *  该网关对应的MQTT-CLIENT收到消息后将消息提交给MQTT-POOL处理.
					 *  MQTT消息如果处理类如果检测到有下发命令,则会首先找到当前网关子设备对应的CHANNEL,并向指定的子设备下发组装后的命令.
					 */
					ConnectMqttClientImpl currencyMqttClientImpl  = new CurrencyGateWayClient(currencyMqttClient, gatewayBaseData, currencyMqttMessageProcessor);
					
					gatewayChannelParameter = new GatewayDataChannelParameter(gatewayBaseData, currencyChannelHandler, currencyMqttClientImpl);
					
					break;
					
				case 9999:
					
					SecretWellTcpMessageProcessor  secretWellTcpMessageProcessor 	= SecretWellTcpMessageProcessor.getInstance();//TCP消息处理实现类
					
					SecretWellMqttMessageProcessor secretWellMqttMessageProcessor 	= SecretWellMqttMessageProcessor.getInstance();//MQTT消息处理实现类
					
					/**
					 * 初始化TCP-CHANNEL消息处理类（SecretWellChannelHandler）并绑定该网关对应的协议解析类（secretWellTcpMessageProcessor）.
					 * 当该handler收到消息后将消息提交到TCP-POOL处理.
					 * TcpMessageProcessor的主要工作是解析消息（包括设备上报的消息和回复的消息）.
					 */
					
					SimpleChannelInboundHandler<String> secretWellChannelHandler 	= new SecretWellChannelHandler(gatewayBaseData, secretWellTcpMessageProcessor);
					
					//MQTT连接
					
					String secretWellTimestamp 		= ConnectMqttClientImpl.getNowTimeStamp();
					
					String secretWellTrueClientId 	= gatewayBaseData.getGatewayId() + "&" + secretWellTimestamp;//密码校验
					
					MqttClient secretWellMqttClient = new MqttClient(gatewayBaseData.getBroker(), secretWellTrueClientId, new MemoryPersistence());
					
					/**
					 *  初始化MQTT连接CLIENT(SecretWellGateWayClient), 绑定MQTT消息处理类(secretWellMqttMessageProcessor).
					 *  该网关对应的MQTT-CLIENT收到消息后将消息提交给MQTT-POOL处理.
					 *  MQTT消息如果处理类如果检测到有下发命令,则会首先找到当前网关子设备对应的CHANNEL, 并向指定的子设备下发组装后的命令.(ps:具体参看MQTT消息处理类)
					 */
					ConnectMqttClientImpl secretWellMqttClientImpl  = new SecretWellGateWayClient(secretWellMqttClient, gatewayBaseData, secretWellMqttMessageProcessor);
					
					gatewayChannelParameter = new GatewayDataChannelParameter(gatewayBaseData, secretWellChannelHandler, secretWellMqttClientImpl);
					
					break;
					
				default:
					break;
			}
			
			if(gatewayChannelParameter != null) gatewayInitParameterList.add(gatewayChannelParameter);//绑定MQTT连接客户端,MQTT连接,TCP消息处理类等工作完毕
		}

		// 通过初始化的数据创建网关和MQTT服务器的连接
		connectMqttClientInstence.createMqttConnectClients(gatewayInitParameterList);
		logger.info("-----------------------gateway-connect-----------------------------");

		// 初始化TCP消息处理线程池
		processPoolInstence.initPool();
		logger.info("-----------------------pool TCP----------------------------------------");

		// 初始化MQTT消息处理线程池
		processMqttPoolInstence.initPool();
		logger.info("-----------------------pool MQTT----------------------------------------");

		// 通过初始化的数据初始化网关TCP端口监听
		serverListenerInstence.initGateWayServerListener(gatewayInitParameterList);
		logger.info("-----------------------gateway-listener----------------------------");

	}
}
