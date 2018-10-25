/*
 * File name: CurrencyMqttMessageProcessor.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 "zhouqiang" Jul 20, 2018
 * ... ... ...
 *
 ***************************************************/

package com.jackromer.gateway.thingProtocols.processCurrencyProrocol.mqttMessageProcessor;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.jackromer.gateway.poolDataProcessor.observerClass.MqttObserver;
import com.jackromer.gateway.thingCommand.CommandThings;
import com.jackromer.gateway.thingProtocols.processCurrencyProrocol.initCurrencyData.InitCurrencyByteData;
import com.jackromer.gateway.utils.GatewayDateUtil;

/**
 * @Description: 通用网关解析MQTT消息类
 * @author: jackromer
 * @version: 1.0, Jul 20, 2018
 */

public class CurrencyMqttMessageProcessor implements MqttObserver {

	public static final Logger								logger			= LoggerFactory
			.getLogger(CurrencyMqttMessageProcessor.class);

	private static final String								SUBTHINGIDSTR	= "subThingId";	// 小写

	private static final String								TIMESTAMP		= "timestamp";	// 小写

	private static final String								RESPONSE		= "response";	// 小写

	private static final String								BODY			= "body";		// 小写

	private static final String								STATE			= "state";		// 小写

	private static final String								DESIRED			= "desired";	// 小写

	private static final String								ICS				= "ics";		// 小写

	private static final String								OPEN_COMMAND	= "open";		// 小写

	private static final String								REPORTED		= "reported";	// 小写

	volatile private static CurrencyMqttMessageProcessor	instance		= null;

	public CurrencyMqttMessageProcessor() {}

	public static CurrencyMqttMessageProcessor getInstance() {

		if (instance != null) {// 懒汉式
		} else {
			// 创建实例之前可能会有一些准备性的耗时工作
			synchronized (CurrencyMqttMessageProcessor.class) {
				if (instance == null) {// 二次检查
					instance = new CurrencyMqttMessageProcessor();
				}
			}
		}

		return instance;
	}


	/**
	 * 处理通用协议MQTT消息
	 * 
	 * @Description:
	 * @param mqttMessage
	 */
	@Override
	public void processMqttMessage(String gatewayId, String mqttMessage) {

		logger.info("通用协议MQTT-PROCESSOR类收到消息！");

		try {
			
			JSONObject messageJson = JSONObject.parseObject(mqttMessage);
	
			String subThingId = messageJson.getString(SUBTHINGIDSTR);// 获取子设备Id
	
			boolean ignoreFlag = true;// 是否是忽略的消息
	
			boolean checkFlag = false;// 是否检查命令超时
	
			JSONObject effectiveJson = messageJson.getJSONObject(BODY);// 默认为GET获取的数据包含response
	
			if (effectiveJson.containsKey(RESPONSE)) {// 处理GET获取的数据
				effectiveJson = effectiveJson.getJSONObject(RESPONSE);
				checkFlag = true;
			}
	
			JSONObject metaDataJson = effectiveJson.containsKey("metadata")?effectiveJson.getJSONObject("metadata") : null;
			
			JSONObject metaDataDesiredJson = (metaDataJson != null ? (metaDataJson.containsKey(DESIRED) ? metaDataJson.getJSONObject(DESIRED) : null) : null);// 获取timestamp
			
			JSONObject metaDataIcsJson = (metaDataDesiredJson != null ? (metaDataDesiredJson.containsKey(ICS) ? metaDataDesiredJson.getJSONObject(ICS) : null) : null);
	
			String timeStamp = (metaDataIcsJson != null ? ( metaDataIcsJson.containsKey(TIMESTAMP) ? metaDataIcsJson.getString(TIMESTAMP) : null) : null);//获取上次下发的时间戳
			
			JSONObject stateJson = effectiveJson.containsKey(STATE) ? effectiveJson.getJSONObject(STATE) : null;// 获取state
	
			JSONObject reportedJson = (stateJson != null ? (stateJson.containsKey(REPORTED) ? stateJson.getJSONObject(REPORTED) : null) : null);// 获取上一次上报的数据
	
			JSONObject desiredJson = (stateJson != null ? stateJson.getJSONObject(DESIRED) : null);// 获取desired
	
			String[] baseDataArray = new String[8];

			if (null != reportedJson) {

				int deviceTypeCode = 1;

				String deviceType = reportedJson.getString("dt");// 设备类型*

				switch (deviceType) {
					case "蓝牙人设备":
						deviceTypeCode = 1;
						break;
					case "蓝牙手设备":
						deviceTypeCode = 2;
						break;
					case "不可控无线人设备":
						deviceTypeCode = 3;
						break;
					case "不可控设备通断传感器":
						deviceTypeCode = 4;
						break;
					case "一体化人设备":
						deviceTypeCode = 5;
						break;
	
					case "人设备3020(蓝牙双人设备)":
						deviceTypeCode = 6;
						break;
	
					default:
						break;
				}

				baseDataArray[0] = String.valueOf(deviceTypeCode);

				int controlTypeCode = 1;
				
				String controlType = reportedJson.getString("ct");// 控制类型*

				if (controlType.equals("不可控")) {
					controlTypeCode = 0;
				}

				baseDataArray[1] = String.valueOf(controlTypeCode);// 控制类型*
				baseDataArray[2] = reportedJson.getString("pv");// 协议版本
				baseDataArray[3] = reportedJson.getString("dc");// 设备编码
				baseDataArray[4] = reportedJson.getString("fv");// 固件版本
				baseDataArray[5] = reportedJson.getString("hv");// 硬件版本
				baseDataArray[6] = "3";// 控制类型ox03
				baseDataArray[7] = "1";// 命令KEY-开锁
			}

			// 获取设备基础数据
	
			if (null != desiredJson && null != reportedJson && null != metaDataJson && timeStamp != null) {// 作下发命令处理
	
				if (desiredJson.containsKey(ICS)) {
	
					logger.info("处理GET获取的下发的命令" + messageJson);
	
					commandSubThing(subThingId, timeStamp, desiredJson, checkFlag, baseDataArray);
	
					ignoreFlag = false;
				}
	
			}
	
			if (ignoreFlag) {
				logger.info("消息中未解析到需要下发的命令,忽略:" + messageJson);
			}
			
		}catch (Exception e) {
			logger.info("MQTT消息解析失败！");
			e.printStackTrace();
			return;
		}
		
	}



	/**
	 * @Description:处理下发命令
	 * @param subThingId
	 *            子设备id
	 * @param timeStamp
	 *            时间戳
	 * @param desiredJson
	 *            需要解析的下发数据
	 * @param checkFlag
	 *            是否需要校验超过三分钟
	 */
	public void commandSubThing(String subThingId, String timeStamp, JSONObject desiredJson, boolean checkTime, String[] baseDataArray) {

		try {
			String innerOpen = desiredJson.getString(ICS);

			if (checkTime) {// 检验时间
				if (GatewayDateUtil.getInstance().checkCommandTime(new Date(), timeStamp)) {// 超过三分钟
					logger.info("命令时间已经超过三分钟[命令过期],时间戳为>>>" + timeStamp);
					return;
				}
			}

			// 是open开锁命令
			if (innerOpen.equals(OPEN_COMMAND)) {
				String commandStr = InitCurrencyByteData.getInstance().initCommandDataByBaseData(baseDataArray);// 获取通用协议下发命令数据流
				CommandThings.getInstance().sendCommandToSubThing(subThingId, commandStr);
			} else {
				logger.info("并非开锁命令, 忽略下发消息！");
			}

		} catch (Exception e) {
			logger.info("命令下发失败>>>" + subThingId + desiredJson);
			e.printStackTrace();
		}
	}

}
