/*
 * File name: package-info.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 "zhouqiang" Jun 19, 2018
 * ... ... ...
 *
 ***************************************************/

/**
 * @Description:
 * @author: jackromer
 * @version: 1.0, Jun 19, 2018
 */

package com.jackromer.gateway.thingProtocols.processCurrencyProrocol.initCurrencyData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackromer.gateway.utils.Base64Util;
import com.jackromer.gateway.utils.BaseByteUtil;
import com.jackromer.gateway.utils.XorUtil;


/**
* @Description:	初始化上报数据
* @author: jackromer
* @version: 1.0, Jun 27, 2018
*/
public class InitCurrencyByteData {
	
	private static Logger logger = LoggerFactory.getLogger(InitCurrencyByteData.class);
	

	volatile private static InitCurrencyByteData instance = null;
	
	private InitCurrencyByteData(){}
	 
	public static InitCurrencyByteData getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (InitCurrencyByteData.class) {
				if(instance == null){//二次检查
					instance = new InitCurrencyByteData();
				}
			}
		} 
		
		return instance;
	}

	
	public static void main(String[] args) {
		//initData();
		//initCommandData();
		//for (int i = 1; i < 10; i++) {
			//System.out.println(initRadomData("test00" + i));
			System.out.println(initRadomData(args[0]));
		//}
	}
	
	/**
	 * 初始化上报数据
	 * 
	 * @Description:
	 * @return
	 */
	public static String initData() {

		byte[] payloadByte = CurrencyPayloadByteMaker.initPayloadByte();
		
		// payload XOR加密
		byte[] xorPayload = XorUtil.getInstance().xorEncode(payloadByte);

		// payload base64加密
		byte[] base64Payload = Base64Util.getInstance().encodeData(xorPayload);

		String lenAndResult = BaseByteUtil.getInstance().byteToStr(base64Payload);
		
		logger.info("加密后的结果为 >>> " + lenAndResult);
		
		//处理header,如果小于100前面用0填充
		String header = lenAndResult.length() > 100 ? String.valueOf(lenAndResult.length()) : "0" + lenAndResult.length();
		
		String result = header + lenAndResult;
		
		//logger.info("[模拟上报]最后加密字符为>>>" + result);
		
		return result;// 返回两次加密后的数据
	}

	
	/**
	 * 初始化上报数据
	 * 
	 * @Description:
	 * @return
	 */
	
	public static String initRadomData(String subThingId) {

		byte[] payloadByte = CurrencyPayloadByteMaker.initRadomPayloadByte(subThingId);
		
		// payload XOR加密
		byte[] xorPayload = XorUtil.getInstance().xorEncode(payloadByte);

		// payload base64加密
		byte[] base64Payload = Base64Util.getInstance().encodeData(xorPayload);

		String lenAndResult = BaseByteUtil.getInstance().byteToStr(base64Payload);
		
		logger.info("加密后的结果为 >>> " + lenAndResult);
		
		//处理header,如果小于100前面用0填充
		String header = lenAndResult.length() > 100 ? String.valueOf(lenAndResult.length()) : "0" + lenAndResult.length();
		
		String result = header + lenAndResult;
		
		//logger.info("[模拟上报]最后加密字符为>>>" + result);
		
		return result;// 返回两次加密后的数据
	}
	
	
	/**
	 * 初始化下发数据开锁
	 * 
	 * @Description:
	 * @return
	 */
	public static String initCommandData() {

		byte[] payloadByte = CurrencyPayloadByteMaker.initCommandPayloadByte();
		
		byte[] xorPayload = XorUtil.getInstance().xorEncode(payloadByte);// payload XOR加密
		
		byte[] base64Payload = Base64Util.getInstance().encodeData(xorPayload);// payload base64加密

		String lenAndResult = BaseByteUtil.getInstance().byteToStr(base64Payload);
		
		String header = lenAndResult.length() > 100 ? String.valueOf(lenAndResult.length()) : "0" + lenAndResult.length();
		
		String result = header + lenAndResult;
		
		logger.info("[命令下发]最后加密字符为>>>" + result);
		
		return result;// 返回两次加密后的数据
	}
	
	
	/**
	 * 根据基础数据初始化下发开锁数据
	 * 
	 * @Description:【thingType, controlType, protocal, thingCode, wireVersion, hardVersion, command, key】
	 *              【            5          1            1     CDSF20418      13            1          3      1   】
	 * @return
	 */
	
	public String initCommandDataByBaseData(String [] baseDataArray) {

		byte[] payloadByte = CurrencyPayloadByteMaker.getInstance().initCommandPayloadByteByBaseData(baseDataArray);
		
		byte[] xorPayload = XorUtil.getInstance().xorEncode(payloadByte);// payload XOR加密
		
		byte[] base64Payload = Base64Util.getInstance().encodeData(xorPayload);// payload base64加密

		String lenAndResult = BaseByteUtil.getInstance().byteToStr(base64Payload);
		
		String header = lenAndResult.length() > 100 ? String.valueOf(lenAndResult.length()) : "0" + lenAndResult.length();
		
		String result = header + lenAndResult;
		
		logger.info("[命令下发]最后加密字符为>>>" + result);
		
		return result;// 返回两次加密后的数据
	}

	/**
	 * 初始化回复上报的数据
	 * 
	 * @Description:
	 * @return
	 */
	public static String initReportReplyData(byte [] baseProtocalByte ,int replyByte) {
		
		byte[] successReplyByte = {(byte) replyByte};//success
		
		byte[] successPayloadByte = BaseByteUtil.getInstance().concatBytes(baseProtocalByte,successReplyByte);
		
		int  checkSum = CurrencyPayloadByteMaker.checkPayload(successPayloadByte);//checksum 2字节 只校验payload  无符号short 64959
		
		byte[] checkSumByteValue = BaseByteUtil.getInstance().intToByte(checkSum);//int 转byte【】取后两个字节【-65, -3, 0 , 0】
		
		byte[] realReplyCheckSumByte = {checkSumByteValue[1], checkSumByteValue[0]};//采用大端模式【-3, -65】
		
		byte[] finalReplyByte = BaseByteUtil.getInstance().concatBytes(successPayloadByte, realReplyCheckSumByte);

		
		byte[] xorPayload = XorUtil.getInstance().xorEncode(finalReplyByte);// payload XOR加密
		
		byte[] base64Payload = Base64Util.getInstance().encodeData(xorPayload);// payload base64加密

		String lenAndResult = BaseByteUtil.getInstance().byteToStr(base64Payload);
		
		String header = lenAndResult.length() > 100 ? String.valueOf(lenAndResult.length()) : "0" + lenAndResult.length();
		
		String result = header + lenAndResult;
		
		//logger.info("[回复数据设备数据]最后加密字符为>>>" + result);
		
		return result;// 返回两次加密后的数据
	}

	/**
	 * 获取二进制String
	 * 
	 * @Description:
	 * @param count
	 * @return
	 */
	
	public static String getBinaryStr(int count) {
		return Integer.toBinaryString(count);
	}

}