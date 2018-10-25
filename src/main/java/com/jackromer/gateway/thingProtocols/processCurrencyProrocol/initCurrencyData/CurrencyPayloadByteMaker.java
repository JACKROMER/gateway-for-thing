/*
* File name: byteMake.java								
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

package com.jackromer.gateway.thingProtocols.processCurrencyProrocol.initCurrencyData;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackromer.gateway.utils.BaseByteUtil;

import io.netty.util.CharsetUtil;

/**
* @Description:	初始化蓝牙设备协议数据流 【上报、下发】
* @author: jackromer
* @version: 1.0, Jun 20, 2018
*/

public class CurrencyPayloadByteMaker {

	
	private static Logger logger = LoggerFactory.getLogger(CurrencyPayloadByteMaker.class);
	
	volatile private static CurrencyPayloadByteMaker instance = null;
	
	private CurrencyPayloadByteMaker(){}
	 
	public static CurrencyPayloadByteMaker getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (CurrencyPayloadByteMaker.class) {
				if(instance == null){//二次检查
					instance = new CurrencyPayloadByteMaker();
				}
			}
		} 
		
		return instance;
	}
	
	/**
	 * 初始化上报数据payload的二进制数据
	* @Description:
	* @return
	 */
	public static byte[] initPayloadByte() {
		
		byte thingType = 1;//设备类型	1
		byte controlType = 1;//控制类型 1
		byte protocal = 1;//协议版本 1
		byte numLeng = 6;//编号长度 6
		byte[] result1 = {thingType,controlType,protocal,numLeng};
		
		String thingCode = "cdsfrj";//设备编码  cdsfrj
		byte[]  thingCodeByte = BaseByteUtil.getInstance().strToByte(thingCode, 6);
		
		byte wireVersion = 1;//固件版本 1
		byte hardVersion = 1;//硬件版本 1 
		byte command = 1;//command 1 
		byte key = 1;//key 1 
		
		byte[] keyValue = getKeyValue();//key value 0-57字节
		
		byte keyLength = (byte) keyValue.length;//key_length 7
		
		byte[] result3 = {wireVersion,hardVersion,command,key,keyLength};
		
		byte[] checkSumByte = BaseByteUtil.getInstance().concatBytes(result1,thingCodeByte,result3,keyValue);
		
		int  checkSum = checkPayload(checkSumByte);//checksum 2字节 只校验payload
		
		byte[] checkSumByteValue = BaseByteUtil.getInstance().intToByte(checkSum);
		
		byte[] result = BaseByteUtil.getInstance().concatBytes(result1, thingCodeByte, result3, keyValue, checkSumByteValue);
		
		//System.out.println("payload   length >>> " + result.length);
		
		return result;
	}
	
	/**
	 * 根据传入的子设备Id初始化上报数据payload的二进制数据
	* @Description:
	* @return
	 */
	public static byte[] initRadomPayloadByte(String subThingId) {
		//[5, 1, 1, 9, 67, 68, 83, 70, 50, 48, 52, 49, 56, 13, 1, 4, 7, 23, 2, 3, 0, 1, 18, 7, 6, 16, 8, 0, 12, 57, 28, 69, 1, 37, 1, 5, 50, 48, 52, 49, 56, -5, -109]
		byte thingType = 5;//设备类型	1
		byte controlType = 1;//控制类型 1
		byte protocal = 1;//协议版本 1
		byte numLeng = 6;//编号长度 6
		byte[] result1 = {thingType,controlType,protocal,numLeng};
		
		String thingCode = "cdsfrj";//设备编码  cdsfrj
		byte[]  thingCodeByte = BaseByteUtil.getInstance().strToByte(thingCode, 6);
		
		
		byte wireVersion = 13;//固件版本 1
		byte hardVersion = 1;//硬件版本 1 
		byte command = 4;//command 1 
		byte key = 7;//key 1 
		
		byte[] keyValue = getRadomKeyValue(subThingId);//key value 0-57字节
		
		byte keyLength = (byte) keyValue.length;//key_length 7
		
		byte[] result3 = {wireVersion,hardVersion,command,key,keyLength};
		
		byte[] checkSumByte = BaseByteUtil.getInstance().concatBytes(result1,thingCodeByte,result3,keyValue);
		
		int  checkSum = checkPayload(checkSumByte);//checksum 2字节 只校验payload
		
		byte[] checkSumByteValue = BaseByteUtil.getInstance().intToByte(checkSum);
		
		byte[] realCheckSumByte = {checkSumByteValue[1], checkSumByteValue[0]};//采用大端模式【-3, -65】
		
		byte[] result = BaseByteUtil.getInstance().concatBytes(result1, thingCodeByte, result3, keyValue, realCheckSumByte);
		
		//System.out.println("payload   length >>> " + result.length);
		
		return result;
	}
	
	/**
	 * 通过基础数据初始化下发命令payload的二进制数据
	* @Description:【thingType, controlType, protocal, thingCode, wireVersion, hardVersion, command, key】
	*              【            5          1            1     CDSF20418      13            1          3      1   】
	* 			         
	* @return
	 */
	public byte[] initCommandPayloadByteByBaseData(String [] baseDataArray) {
		
		byte thingType = (byte) Integer.parseInt(baseDataArray[0]);//设备类型	1
		byte controlType = (byte) Integer.parseInt(baseDataArray[1]);//控制类型 1
		byte protocal = (byte) Integer.parseInt(baseDataArray[2]);//协议版本 1
		
		String thingCode = baseDataArray[3];//设备编码  cdsfrj
		
		byte[]  thingCodeByte = thingCode.getBytes(CharsetUtil.UTF_8);
		
		byte numLeng = (byte) thingCodeByte.length;//编号长度 
		
		byte[] result1 = {thingType,controlType,protocal,numLeng};
		
		byte wireVersion =(byte) Integer.parseInt(baseDataArray[4]);//固件版本 13
		
		byte hardVersion = (byte) Integer.parseInt(baseDataArray[5]);//硬件版本 1 
		
		byte command = (byte) Integer.parseInt(baseDataArray[6]);//command 
		
		byte key = (byte) Integer.parseInt(baseDataArray[7]);//key 1
		
		
		byte[] result3 = {wireVersion, hardVersion, command, key};
		
		byte[] checkSumByte = BaseByteUtil.getInstance().concatBytes(result1,thingCodeByte,result3);//payloadByte
		
		int  checkSum = checkPayload(checkSumByte);//checksum 2字节 只校验payload  无符号short 64959
		
		byte[] checkSumByteValue = BaseByteUtil.getInstance().intToByte(checkSum);//int 转byte【】取后两个字节【-65, -3, 0 , 0】
		
		byte[] realCheckSumByte = {checkSumByteValue[1], checkSumByteValue[0]};//采用大端模式【-3, -65】
		
		byte[] result = BaseByteUtil.getInstance().concatBytes(result1,thingCodeByte,result3,realCheckSumByte);
		
		String byteStr = "";
		for (byte b : result) {
			byteStr += " " + b;
		}
		
		logger.info("下发的字节数据为：【" + byteStr + "】");
		
		return result;
	}
	
	/**
	 * 初始化下发命令payload的二进制数据
	* @Description:
	* @return
	 */
	public static byte[] initCommandPayloadByte() {
		
		byte thingType = 5;//设备类型	1
		byte controlType = 1;//控制类型 1
		byte protocal = 1;//协议版本 1
		
		String thingCode = "CDSF20418";//设备编码  cdsfrj
		
		byte[]  thingCodeByte = thingCode.getBytes(CharsetUtil.UTF_8);
		
		byte numLeng = (byte) thingCodeByte.length;//编号长度 
		
		byte[] result1 = {thingType,controlType,protocal,numLeng};
		
		byte wireVersion = 13;//固件版本 13
		
		byte hardVersion = 1;//硬件版本 1 
		
		byte command = 3;//command 
		
		byte key = 1;//key 1
		
		//byte[] keyValue = getCommandKeyValue();//key value 0-57字节
		
		//byte keyLength = (byte) keyValue.length;//key_length 7
		
		byte[] result3 = {wireVersion, hardVersion, command, key};
		
		byte[] checkSumByte = BaseByteUtil.getInstance().concatBytes(result1,thingCodeByte,result3);//payloadByte
		
		int  checkSum = checkPayload(checkSumByte);//checksum 2字节 只校验payload  无符号short 64959
		
		byte[] checkSumByteValue = BaseByteUtil.getInstance().intToByte(checkSum);//int 转byte【】取后两个字节【-65, -3, 0 , 0】
		
		byte[] realCheckSumByte = {checkSumByteValue[1], checkSumByteValue[0]};//采用大端模式【-3, -65】
		
		byte[] result = BaseByteUtil.getInstance().concatBytes(result1,thingCodeByte,result3,realCheckSumByte);
		
		String byteStr = "";
		for (byte b : result) {
			byteStr += " " + b;
		}
		
		logger.info("下发的字节数据为：【" + byteStr + "】");
		
		return result;
	}
	
	/**
	 * 校验payload内容
	* @Description:
	* @param checkStr
	* @return
	 */
	public static int checkPayload(byte[] checkData) { //无符号int相加  byte 0xFF ---short 0x00FF
		
		  int result = 0;//无符号short
		  
		  for (short b : checkData) {//0x00FF & 0xFF   0000 0000 0111 1111 & 1111 1111 = 0000 0000 1111 1111
			  
			result += b & 0xFF ;//转为0-255 int
			
			result  = result & 0xFFFF;//
			
		  }
		  
		  int reverse =   (~ result) & 0xFFFF;//用大的装晓得
		  
		  int finalResult = reverse + 1;//转为无符号的short类型
		  
		  finalResult = finalResult & 0xFFFF;
		  
		  //logger.info("取反前" + ~ result + " >>> 取反后" + finalResult );
		  
		  return finalResult ;//保留低位数据
		  
	}
	
	public static int getCheckSumShort(int unsighedShort) {
		
		int result = unsighedShort & 0xFFFF;//转为无符号short
		
		return result;
	}
	
	
	public static void main(String[] args) {
		
		System.out.println("short" + 64351);
		//check sum 装的是无符号的short
		byte[] arr = {95,-5};
		
		int check2 = BaseByteUtil.getInstance().byteToUnSinghedShort(arr);//上报的转为无符号的
		
		System.out.println("上报的无符号checkSum" + check2);
		
		System.out.println("校验的无符号checkSum" + 64351);
		
	}
	
	/***
	 * 拼装keyvalue 字节数组
	* @Description:
	* @return
	 */
	public static byte[] getKeyValue() {
		byte in = 1;//IN	
		byte out  = 1;//OUT	
		byte hx  = 01;//HX	
		byte re  = 00;//RE	
		
		String time  = "170313135501" ;//TIME	>>> 6 字节 1703131355
		byte[] timeByte = getTimeByte(time);
		
		byte sig  = 99;//SIG	
		byte bt  = 99;//BT	
		byte t  = 127;//T	
		byte p  = 99;//P	
		
		short sn  = 8888;//SN	>>>2 字节
		byte[] snByte = BaseByteUtil.getInstance().shortToByte(sn);
		byte end  = 00;//END	
		String id  = "aiTMOQDix2qymFjL716g";//ID >>>idlen 字节
		byte[] idByte = id.getBytes();
		int idLen = idByte.length;
		byte idlen  = (byte) idLen;//ID_LEN
		byte[] result1 = {in,out,hx,re,
				timeByte[0],
				timeByte[1],
				timeByte[2],
				timeByte[3],
				timeByte[4],
				timeByte[5],
						  sig,bt,t,p,
				snByte[0],
				snByte[1],
				end,idlen};
		
		byte[] result = BaseByteUtil.getInstance().concatBytes(result1, idByte);
		//System.out.println("key-value length >>> " + result.length);
		return result;
	}
	//[5, 1, 1, 9, 67, 68, 83, 70, 50, 48, 52, 49, 56, 13, 1, 4, 7, 23, 2, 3, 0, 1, 18, 7, 6, 16, 8, 0, 12, 57, 28, 69, 1, 37, 1, 5, 50, 48, 52, 49, 56, -5, -109]
	
	/***
	 * 拼装keyvalue 字节数组
	* @Description:
	* @return
	 */
	public static byte[] getRadomKeyValue(String subThingId) {
		byte in = 2;//IN	
		byte out  = 3;//OUT	
		byte hx  = 0;//HX	
		byte re  = 1;//RE	
		
		String time  = "170313135501" ;//TIME	>>> 6 字节 1703131355
		byte[] timeByte = getTimeByte(time);
		
		byte sig  = 99;//SIG	
		byte bt  = 99;//BT	
		byte t  = 127;//T	
		byte p  = 99;//P	
		
		short sn  = 8888;//SN	>>>2 字节
		byte[] snByte = BaseByteUtil.getInstance().shortToByte(sn);
		byte end  = 00;//END
		
		String id  = subThingId;//ID >>>idlen 字节
		byte[] idByte = id.getBytes();
		int idLen = idByte.length;
		byte idlen  = (byte) idLen;//ID_LEN
		byte[] result1 = {in,out,hx,re,
				timeByte[0],
				timeByte[1],
				timeByte[2],
				timeByte[3],
				timeByte[4],
				timeByte[5],
						  sig,bt,t,p,
				snByte[0],
				snByte[1],
				end,idlen};
		
		byte[] result = BaseByteUtil.getInstance().concatBytes(result1, idByte);
		//System.out.println("key-value length >>> " + result.length);
		return result;
	}
	
	
	/***
	 * 拼装keyvalue 字节数组
	* @Description:
	* @return
	 */
	public static byte[] getCommandKeyValue() {
		byte [] b = {0};
		//System.out.println("key-value length >>> " + b.length);
		return b;
	}
	
	
	/**
	 * 拼装时间戳byte
	* @Description:
	* @param timeStr
	* @return
	 */
	public static byte[] getTimeByte(String timeStr) {
		//System.out.println(timeStr);
		String t1 = timeStr.substring(0, 2);
		String t2 = timeStr.substring(2, 4);
		String t3 = timeStr.substring(4, 6);
		String t4 = timeStr.substring(6, 8);
		String t5 = timeStr.substring(8,10);
		String t6 = timeStr.substring(10);
		
		byte s1 = (byte) Integer.parseInt(t1);
		byte s2 = (byte) Integer.parseInt(t2);
		byte s3 = (byte) Integer.parseInt(t3);
		byte s4 = (byte) Integer.parseInt(t4);
		byte s5 = (byte) Integer.parseInt(t5);
		byte s6 = (byte) Integer.parseInt(t6);
		
		byte[] result = {s1,s2,s3,s4,s5,s6};
		
		return result;
	}
}
