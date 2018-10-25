/*
 * File name: ConnectionMqtt.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Xu Apr 16, 2018 ... ... ...
 *
 ***************************************************/

package com.jackromer.gateway.mqttConnector.mqttInterface;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.alibaba.fastjson.JSONObject;
import com.jackromer.gateway.enums.SignMethodType;
import com.jackromer.gateway.gatewayServer.GatewayBaseData;
import com.jackromer.gateway.utils.AuthUtil;


/**
 * @Description:实现类
 * @author: jackromer
 * @version: 1.0, Apr 16, 2018
 */

public class ConnectMqttClientImpl implements ConnectMqttClientInterface {
	
	public static boolean connectMqttFlag = false;
	
	private static final String HMACMD5STR = "hmacmd5";//小写
	
	private static final String GATEWAYSTR = "gateway";//小写
	
	private  MqttClient  mqttClient;//继承类可用
	
	private MqttCallbackExtended mqttCallbackExtended;
	
	private GatewayBaseData gatewayBaseData;
	
	/**
	 * @param mqttClient
	 * @param callback
	 * @param broker
	 * @param gatewayId
	 * @param gatewayPassword
	 */
	public ConnectMqttClientImpl(MqttClient mqttClient,MqttCallbackExtended mqttCallbackExtended, GatewayBaseData gatewayBaseData) {
		super();
		this.mqttClient = mqttClient;
		this.mqttCallbackExtended = mqttCallbackExtended;
		this.gatewayBaseData = gatewayBaseData;
	}


	public static ConcurrentHashMap<String, JSONObject> keysMap  = new ConcurrentHashMap<String,JSONObject>();//小写
	
	@Override
	public  void connectMqttBroker() {
		
		String sign;
		
		MemoryPersistence persistence = new MemoryPersistence();
		
		try {
			String timestamp = getNowTimeStamp();
			
			String trueClientId = gatewayBaseData.getGatewayId() + "&" + timestamp;
			
			mqttClient = new MqttClient(gatewayBaseData.getBroker(), trueClientId, persistence);
			
			final MqttConnectOptions connOpts = new MqttConnectOptions();
			
			mqttClient = new MqttClient(gatewayBaseData.getBroker(), trueClientId, persistence);
			
			// signature签名算法获得，即：signMethod(thingKey, content), content = ${thingId} + ${signMethod} + ${thingType} + ${timestamp}。
			
			String content = gatewayBaseData.getGatewayId() + HMACMD5STR + GATEWAYSTR + timestamp;
			
			sign = AuthUtil.cipherMac(SignMethodType.HMACMD5, gatewayBaseData.getGatewayPassword(), content);
			
			connOpts.setUserName(gatewayBaseData.getGatewayId() + "&" + HMACMD5STR + "&" + GATEWAYSTR);
			
			connOpts.setServerURIs(new String[] { gatewayBaseData.getBroker() });
			
			connOpts.setPassword(sign.toCharArray());
			
			connOpts.setCleanSession(true);
			
			connOpts.setKeepAliveInterval(90);
			
			connOpts.setAutomaticReconnect(true);
			
			connOpts.setConnectionTimeout(30);//连接超时时间三十秒
			
			//回调函数
			mqttClient.setCallback(mqttCallbackExtended);
			
			//此段代码将才会真的创建连接
			mqttClient.connect(connOpts);
			
		} catch (Exception me) {
			me.printStackTrace();
		}
	}
	

    
	 /**
     * 取得当前时间戳（精确到秒）
     *
     * @return nowTimeStamp
     */
    public static String getNowTimeStamp() {
        long time = System.currentTimeMillis();
        String nowTimeStamp = String.valueOf(time / 1000);
        return nowTimeStamp;
    }





	/**
	 * @see com.sefon.gateway.mqttConnector.ConnectMqttClientInterface#disconnectMqttBroker()
	 */
	@Override
	public void disconnectMqttBroker() {
		if(null != mqttClient) {
			try {
				mqttClient.disconnect();
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
	}



	/**
	 * @see com.sefon.gateway.mqttConnector.ConnectMqttClientInterface#reconnectMqttBroker()
	 */
	@Override
	public void reconnectMqttBroker() {
		if(null != mqttClient) {
			try {
				mqttClient.reconnect();
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * @return the gatewayBaseData
	 */
	public GatewayBaseData getGatewayBaseData() {
		return gatewayBaseData;
	}

	/**
	 * @param gatewayBaseData the gatewayBaseData to set
	 */
	public void setGatewayBaseData(GatewayBaseData gatewayBaseData) {
		this.gatewayBaseData = gatewayBaseData;
	}

	/**
	 * @return the mqttClient
	 */
	public MqttClient getMqttClient() {
		return mqttClient;
	}

	/**
	 * @param mqttClient the mqttClient to set
	 */
	public  void setMqttClient(MqttClient mqttClient) {
		this.mqttClient = mqttClient;
	}



	/**
	 * @return the mqttCallbackExtended
	 */
	public MqttCallbackExtended getMqttCallbackExtended() {
		return mqttCallbackExtended;
	}



	/**
	 * @param mqttCallbackExtended the mqttCallbackExtended to set
	 */
	public void setMqttCallbackExtended(MqttCallbackExtended mqttCallbackExtended) {
		this.mqttCallbackExtended = mqttCallbackExtended;
	}


	
	
}
