/*
* File name: processData.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			"zhouqiang"		Jun 20, 2018
* ...			...			...
*
***************************************************/

package com.jackromer.gateway.thingProtocols.processCurrencyProrocol.tcpMessageProcessor;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackromer.gateway.enums.ThingType;
import com.jackromer.gateway.poolDataProcessor.observerClass.TcpObserver;
import com.jackromer.gateway.thingProtocols.processCurrencyProrocol.CurrencySubThingData;
import com.jackromer.gateway.thingProtocols.processCurrencyProrocol.initCurrencyData.CurrencyThingDataStreamMaker;
import com.jackromer.gateway.thingProtocols.processCurrencyProrocol.protocolParser.CurrencyProtocolParser;
import com.jackromer.gateway.utils.Base64Util;
import com.jackromer.gateway.utils.BaseByteUtil;
import com.jackromer.gateway.utils.XorUtil;

/**
* @Description:	网关处理子设备TCP消息
* @author: jackromer
* @version: 1.0, Jun 20, 2018
*/

public class CurrencyTcpMessageProcessor  implements TcpObserver{

	public static  Logger logger = LoggerFactory.getLogger(CurrencyTcpMessageProcessor.class);
	
	private static final Charset charset = Charset.forName("UTF-8");
	
	volatile private static CurrencyTcpMessageProcessor instance = null;
	
	public CurrencyTcpMessageProcessor() {}
	
	public static CurrencyTcpMessageProcessor getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (CurrencyTcpMessageProcessor.class) {
				if(instance == null){//二次检查
					instance = new CurrencyTcpMessageProcessor();
				}
			}
		} 
		
		return instance;
	}
	
	/**
	 * 处理上报消息
	* @Description:多个线程调用该方法,数据保存在不同的线程中，不需要做互斥处理
	* @Example 076NDMyMlZSRF5LWjwzNzMTNDQ4OCEyPz4DNFVUR1qIEzInVVxiendodFhKAUVMW3FSdQcABFTHSQ==
	* @param data
	* @return reslutArray, 长度3, 【subThingId, 设备影子数据流JSON, reportReply】
	 */
	@Override
	public  String [] processTcpMessage(String gatewayId, String data) {
		
		logger.info("通用协议TCP-PROCESSOR收到消息！");
		
		String [] reslutArray =  new String[3];
		
		//校验上报数据是否正确应该是以三位数字开头+中英文字符组成
		if(!standardData(data)) {
			logger.info("数据格式校验失败,请确认上报数据格式是否正确>>>" + data);
			return reslutArray;
		};
		
		//校验成功 符合规范
		try {
			
			CurrencySubThingData std = new CurrencySubThingData();
			
			//获取payload，取header后的所有，header长度为三位
			String payload  = data.substring(3);
			
			//转为byte数组
			byte[] reversePayloadByte = payload.getBytes(charset);
			
			//base64解密
			byte[] base64Reverse = Base64Util.getInstance().decodeData(reversePayloadByte);
			
			//异或解密
			byte[] xorReverse = XorUtil.getInstance().xorDecode(base64Reverse);
			
			
			//开始解析数据
			//------------------------parse thing type to protocal----------
			
			byte thingType = xorReverse[0];//设备类型
			
			ThingType thingTypeEnum = CurrencyThingDataStreamMaker.getInstance().standardThingType(thingType);
			
			if(null == thingTypeEnum) {//处理空指针问题
				logger.info("不识别的设备协议！------" + thingType);
				return reslutArray;
			}
			
			std.setThingType(thingTypeEnum.getThingType());
			
			switch (thingTypeEnum) {
			
			case BLUETOOTH_WELL:
				logger.info("解析协议>>>协议代码" + thingType + "协议类型>>>" + thingTypeEnum.getThingType());
				break;
			case INTEGRATED_WELL:
				logger.info("解析协议>>>协议代码" + thingType + "协议类型>>>" + thingTypeEnum.getThingType());
				break;
			default:
				logger.info("暂不支持改协议>>> " + thingType + "---" + thingTypeEnum.getThingType());
				break;
			}
			reslutArray = CurrencyProtocolParser.getInstance().processProtocal(xorReverse, std , gatewayId);//解析SEFON子设备通用协议
			
		} catch (Exception e) {
			logger.info("上报数据解析异常！");
			e.printStackTrace();
		}
		
		return reslutArray;
	}
	
	
	
	/**
	 * 校验数据上报数据是否符合规范header+payload[header]
	* @Description:校验成功返回true,校验失败返回false
	* @param data
	 */
	public static boolean standardData(String data) {
		//校验上报数据是否正确应该是以三位数字开头+中英文字符组成
		Matcher matcherHeader = Pattern.compile("^[0-9]{3}").matcher(data);// 设备topic
		boolean flag = false, lengthFlag  = false;
		String header = "", payload = "";
		
		if (data.length() > 3) {
			while (matcherHeader.find()) {
				flag = true;
				header = matcherHeader.group(0);
				break;
			}
			
			if (!header.isEmpty()) {//获取payload
				int payloadLength = Integer.parseInt(header);
				payload = data.substring(header.length());
				if(payload.length() == payloadLength) {//长度相等
					lengthFlag = true;
				}
			}
		}
		
		return lengthFlag && flag;
		
	}
	
	public static void main(String[] args) {
		
		byte [] newIdByte = BaseByteUtil.getInstance().strToByte("test001", 7);
		for (byte b : newIdByte) {
			System.out.println(b);
		}
		//[5, 1, 1, 9, 67, 68, 83, 70, 50, 48, 52, 49, 56, 13, 1, 4, 7, 23, 2, 3, 0, 1, 18, 7, 6, 16, 8, 0, 12, 57, 28, 69, 1, 37, 1, 5, 50, 48, 52, 49, 56, -5, -109]
		//[5, 1, 1, 9, 67, 68, 83, 70, 50, 48, 52, 49, 56, 13, 1, 4, 7, 23, 2, 3, 0, 1, 18, 7, 6, 16, 8, 0, 12, 57, 28, 69, 1, 37, 1, 5, 50, 48, 52, 49, 56, -5, -109]
	    //[..............................................................................................................................50, 48, 52, 49, 56..........]
	}

}
