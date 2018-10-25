/*
 * File name: protocolParseUtil.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 "zhouqiang" Jun 27, 2018
 * ... ... ...
 *
 ***************************************************/

package com.jackromer.gateway.thingProtocols.processCurrencyProrocol.protocolParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.jackromer.gateway.thingProtocols.processCurrencyProrocol.CurrencySubThingData;
import com.jackromer.gateway.thingProtocols.processCurrencyProrocol.initCurrencyData.CurrencyPayloadByteMaker;
import com.jackromer.gateway.thingProtocols.processCurrencyProrocol.initCurrencyData.CurrencyThingDataStreamMaker;
import com.jackromer.gateway.thingProtocols.processCurrencyProrocol.initCurrencyData.InitCurrencyByteData;
import com.jackromer.gateway.utils.BaseByteUtil;

/**
 * @Description: 协议解析类
 * @author: jackromer
 * @version: 1.0, Jun 27, 2018
 */

public class CurrencyProtocolParser {

	private static Logger		logger			= LoggerFactory.getLogger(CurrencyProtocolParser.class);

	public static final String	SUBTHING_REPLY	= "subThingReply";//返回此值代表是设备返回的数据


	//使用volatile关键字保其可见性
	volatile private static CurrencyProtocolParser instance = null;
	
	private CurrencyProtocolParser(){}
	 
	public static CurrencyProtocolParser getInstance() {
	 
		if(instance != null){//懒汉式 
			
		} else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (CurrencyProtocolParser.class) {
				if(instance == null){//二次检查
					instance = new CurrencyProtocolParser();
				}
			}
		} 
		
		return instance;
	}

	/**
	 * 解析子设备通用协议
	 * @Description: 
	 * @param xorReverse 需要解析的字节数组
	 * @param std 数据点类实体
	 * @return reslutArray, 长度3, 【subThingId,设备影子数据流JSON,reportReply】
	 * 
	 */

	public  String[] processProtocal(byte[] xorReverse, CurrencySubThingData std ,String gatewayId) throws Exception {

		
		String[] reslutArray = new String[3];
		String subThingId = null;
		String replyStr = "";

		// --------------------------checkSum-start
		byte[] checkSumByte = BaseByteUtil.getInstance().subStrByteArray(xorReverse, xorReverse.length - 2, 2);// checksumArray-length-2

		byte[] realCheckSumByte = { checkSumByte[1], checkSumByte[0] };

		byte[] payloadByte = BaseByteUtil.getInstance().subStrByteArray(xorReverse, 0, xorReverse.length - 2);// 获取payloadByteArray

		int payloadCheckSum = CurrencyPayloadByteMaker.checkPayload(payloadByte);

		int byteChecksum = BaseByteUtil.getInstance().byteToUnSinghedShort(realCheckSumByte);//

		if (payloadCheckSum == byteChecksum) {//checksum相同
			logger.info("payload-checksum " + payloadCheckSum + " >>> byteArr-checksum " + byteChecksum+ "The two checkSum is same ！OK!");
		} else {
			logger.info("payload-checksum " + payloadCheckSum + " >>> byteArr-checksum " + byteChecksum+ "The two checkSum is different ！");
			return reslutArray;
		}
		
		
		// --------------------------checkSum-end

		byte controlType = xorReverse[1];// 控制类型
		String controlTypeStr = CurrencyThingDataStreamMaker.getInstance().standardControlType(controlType);
		std.setControlType(controlTypeStr);

		byte protocal = xorReverse[2];// 协议版本
		std.setProtocalVersion(protocal);

		// ------------------------parse thing code
		
		byte numLength = xorReverse[3];// 设备编码-cdsfrj长度
		byte[] thingCodeByte = BaseByteUtil.getInstance().subStrByteArray(xorReverse, 4, numLength);
		String thingCode = new String(thingCodeByte);
		std.setThingCode(thingCode);

		// -------------------------parse wire to KeyLength
		
		byte[] wireToKeyLengthByte = BaseByteUtil.getInstance().subStrByteArray(xorReverse, 4 + numLength, 5);

		byte wireVersion = wireToKeyLengthByte[0];// 固件版本
		std.setWireVersion(wireVersion);

		byte hardVersion = wireToKeyLengthByte[1];// 硬件版本
		std.setHardVersion(hardVersion);

		byte command = wireToKeyLengthByte[2];// command控制类型
		std.setCommand(command);

		byte key = wireToKeyLengthByte[3];// key控制key
		std.setKey(key);

		// --------------------------parse keyvalue
		
		byte keyLength = wireToKeyLengthByte[4];// key_length

		int keyValueLength = (int) keyLength;
		
		int keyValueStartIndex = 4 + numLength + 5;
		
		byte[] keyValue = BaseByteUtil.getInstance().subStrByteArray(xorReverse, keyValueStartIndex, keyValueLength);// keyValueArry可能为正常上报数据,也可能为开锁命令等的返回数据
		
		byte in = keyValue[0];// IN内设备盖状态
		String inStr = CurrencyThingDataStreamMaker.getInstance().standardInsideAndOut(in);
		std.setIn(inStr);

		// 判断是否为下发命令后(开锁后)设备返回的数据-这里需要后期处理分类做差异解析
		if (keyValue.length == 1) {
			byte commandReply = keyValue[0];
			if (commandReply == 1) {
				logger.info("设备锁已经驱动！");
			} else {
				logger.info("设备锁驱动失败！");
			}
		} else {
			byte out = keyValue[1];// OUT外设备盖状态
			String outStr = CurrencyThingDataStreamMaker.getInstance().standardInsideAndOut(out);
			std.setOut(outStr);

			byte hx = keyValue[2];// HX唤醒状态
			String hxStr = CurrencyThingDataStreamMaker.getInstance().standardAwakenStatus(hx);
			std.setHx(hxStr);

			byte re = keyValue[3];// RE状态原因
			String reStr = CurrencyThingDataStreamMaker.getInstance().standardStatusReason(re);
			std.setRe(reStr);

			byte[] timeByte = BaseByteUtil.getInstance().subStrByteArray(keyValue, 4, 6);// TIME时间戳1703131355

			String timeStr = BaseByteUtil.getInstance().parseTime(timeByte);

			std.setTime(timeStr);

			byte sig = keyValue[10];// SIG信号强度
			std.setSig(sig);

			byte bt = keyValue[11];// BT电池电量
			std.setBt(bt);

			byte t = keyValue[12];// T温度
			std.setT(t);

			byte p = keyValue[13];// P湿度
			std.setP(p);

			byte[] snByte = BaseByteUtil.getInstance().subStrByteArray(keyValue, 14, 2);// SN通讯序列号
			short sn = BaseByteUtil.getInstance().byteToShort(snByte);
			std.setSn(sn);

			byte end = keyValue[16];// END
			String endStr = CurrencyThingDataStreamMaker.getInstance().standardEnd(end);
			std.setEnd(endStr);

			byte idlen = keyValue[17];// ID_LEN

			byte[] idByte = BaseByteUtil.getInstance().subStrByteArray(keyValue, 18, idlen);// ID

			subThingId = BaseByteUtil.getInstance().byteToStr(idByte);
			/*
			 * PS-解析出有设备id才证明是设备上报的消息,需要回复该设备消息,否则设备会重复上报该条消息并断开连接
			 */
			if (subThingId.length() > 0) {
				
				//获取基础字节流数据用于回复设备上报-不包含keyValueLength数据和keyValueArray数据
				byte[] baseProtocalByte = BaseByteUtil.getInstance().subStrByteArray(xorReverse, 0, 4 + numLength + 4);

				replyStr = InitCurrencyByteData.initReportReplyData(baseProtocalByte, 0x01);// 0x01代表上报成功,0x00代表上报失败
			}

			std.setId(subThingId);
		}
		
		
		JSONObject json = CurrencyThingDataStreamMaker.getThingReportUpdateJsonData(std, gatewayId);// 拼装上报数据流
		
		/*
		 *  用来单独处理给设备下发命令后设备返回的数据,例如开锁成功或者失败
		 */
		
		if (null == subThingId) {//没有解析到子设备ID则视为设备返回的数据-这里需要后期处理分类做差异解析
			subThingId = SUBTHING_REPLY;
		}

		reslutArray[0] = subThingId;
		
		reslutArray[1] = json.toJSONString();
		
		reslutArray[2] = replyStr;// 回复数据
		
		return reslutArray;
	}
}
