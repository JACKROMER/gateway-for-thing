/*
* File name: ThingDataStreamMaker.java								
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
* 1.0			"zhouqiang"		Jun 21, 2018
* ...			...			...
*
***************************************************/

package com.jackromer.gateway.thingProtocols.processCurrencyProrocol.initCurrencyData;

import com.alibaba.fastjson.JSONObject;
import com.jackromer.gateway.enums.ThingType;
import com.jackromer.gateway.thingProtocols.processCurrencyProrocol.CurrencySubThingData;

/**
* @Description:	拼装设备影子数据流
* @author: jackromer
* @version: 1.0, Jun 21, 2018
*/

public class CurrencyThingDataStreamMaker {
	
	volatile private static CurrencyThingDataStreamMaker instance = null;
	
	private CurrencyThingDataStreamMaker(){}
	 
	public static CurrencyThingDataStreamMaker getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (CurrencyThingDataStreamMaker.class) {
				if(instance == null){//二次检查
					instance = new CurrencyThingDataStreamMaker();
				}
			}
		} 
		
		return instance;
	}
	
	
	/**
	 * 生成网关设备上报设备影子数据流(影子更新)
	* @Description:
	* @return
	 */
	public static JSONObject getThingReportUpdateJsonData(CurrencySubThingData subThingData ,String gateWayId) {
		
		JSONObject jsonReport = new JSONObject();
		
		//开始解析数据
		//------------------------thing type to protocal--------------------------------------------------
		//设备类型
		jsonReport.put("dt", subThingData.getThingType());
		
		//控制类型 1
		jsonReport.put("ct", subThingData.getControlType());
		
		// 协议版本 1
		jsonReport.put("pv", subThingData.getProtocalVersion());
		
		//------------------------thing code --------------------------------------------------
		
		// 设备编码  cdsfrj
		jsonReport.put("dc", subThingData.getThingCode());
		
		//-------------------------wire to KeyLength -------------------------------------------------
		// 固件版本 1
		jsonReport.put("fv", subThingData.getWireVersion());
		
		//硬件版本 1 
		jsonReport.put("hv", subThingData.getHardVersion());
		
		//command
		jsonReport.put("command", subThingData.getCommand());
		
		//key 1 
		jsonReport.put("key", subThingData.getKey());
		
		//--------------------------key value------------------------------------------------
		
		//IN 内设备盖1：open  2：close  3非法开
		jsonReport.put("ics", subThingData.getIn());
		
		// OUT open,close,illegally open
		jsonReport.put("ocs", subThingData.getOut());
		
		//HX 唤醒状态 1:有效  0：无效 
		jsonReport.put("ws", subThingData.getHx());
		
		//RE 状态原因，00设备产生的数据，01查询的数据，02定时数据 
		//jsonReport.put("statusReason", subThingData.getRe());
		
		//TIME	>>> 6 字节 1703131355
		jsonReport.put("sgt", subThingData.getTime());
		
		//SIG 信号
		jsonReport.put("sig", subThingData.getSig());
		
		// BT 电池电量
		jsonReport.put("bv", subThingData.getBt() < 10 ? subThingData.getBt() : ((double)subThingData.getBt())/10);
		
		//T 温度
		jsonReport.put("temp", subThingData.getT());
		
		//P 湿度
		jsonReport.put("humidity", subThingData.getP());
		
	    //SN通讯序列号
		jsonReport.put("sn", subThingData.getSn());
		
		// END  0发送结束1：未发送结束 0
		//jsonReport.put("sendEnd", subThingData.getEnd());
		
		//ID 设备名称
		jsonReport.put("hardwareid", subThingData.getId());
		
		//-------------------------------------------
		JSONObject jsonState = new JSONObject();
		
		jsonState.put("reported", jsonReport);
		
		//-------------------------------------------
		JSONObject jsonBody = new JSONObject();
		
		jsonBody.put("state", jsonState);
		
		//jsonBody.put("version", 1); 不需要version
		
		//-------------------------------------------
		
		JSONObject thingReportDataJson = new JSONObject();
		
		thingReportDataJson.put("requestId", gateWayId);
		
		thingReportDataJson.put("subThingId", subThingData.getId());
		
		thingReportDataJson.put("method", "put");
		
		thingReportDataJson.put("body", jsonBody);
		
		//System.out.println(thingReportDataJson.toJSONString());
		
		return thingReportDataJson;
		
	}
	
	/**
	 * 处理时间
	* @Description:
	* @param subThingData
	* @return subThingData
	 */
	
	public static String standardTime(String time) {
		String timeStr = "20";
		int end = 0;
		for (int i = 0; i < time.length(); i += 2) {
			end += 2;
			timeStr += time.substring(i, end) + "-" ;
		}
		return timeStr;
	}
	
	/**
	 * 处理设备类型
	* @Description: 不同的编号代表不同的设备类型
	* @param thingType
	* @return ThingType
	 */
	public  ThingType standardThingType(int data) {
		
		ThingType dataValue = null;
		switch (data) {
			case 1:
				dataValue = ThingType.BLUETOOTH_WELL;
				break;
			case 2:
				dataValue = ThingType.BLUETOOTH_HAND_WELL;
				break;
			case 3:
				dataValue = ThingType.UNCONTROLLABLE_WIRELESS_WELL;
				break;
			case 4:
				dataValue = ThingType.UNCONTROLLABLE_THING_BREAKOFF_SENSOR;
				break;
			case 5:
				dataValue = ThingType.INTEGRATED_WELL;
				break;
			case 6:
				dataValue = ThingType.DOUBLE_BLUETOOTH_WELL_3020;
				break;
			default:
				break;
		}
		return dataValue;
	}
	
	/**
	 * 处理控制类型 1（可控）0（不可控）
	* @Description:
	* @param data
	* @return
	 */
	public String standardControlType(int data) {
		String[] switchData  = {"不可控","可控"};
		String dataValue ="";
		switch (data) {
			case 0:
				dataValue = switchData[0];
				break;
			case 1:
				dataValue = switchData[1];
				break;
			default:
				break;
		}
		return dataValue;
	}
	
	/**
	 * 处理内外设备盖 open  2：close  3 illegally open
	* @Description:
	* @param data
	* @return
	 */
	public  String standardInsideAndOut(int data) {
		
		String[] switchData  = {"open","close","illegally open"};
		
		String dataValue ="";
		switch (data) {
			case 1:
				dataValue = switchData[0];
				break;
			case 2:
				dataValue = switchData[1];
				break;
			case 3:
				dataValue = switchData[2];
				break;
			default:
				break;
		}
		return dataValue;
	}
	
	/**
	 * 处理唤醒状态 0无效1有效
	* @Description:
	* @param data
	* @return
	 */
	public  String standardAwakenStatus(int data) {
		
		String[] switchData  = {"无效","有效"};
		
		String dataValue ="";
		switch (data) {
			case 0:
				dataValue = switchData[0];
				break;
			case 1:
				dataValue = switchData[1];
				break;
			default:
				break;
		}
		return dataValue;
	}
	
	/**
	 * 状态原因，00设备产生的数据，01查询的数据，02定时数据 
	* @Description:
	* @param data
	* @return
	 */
	public  String standardStatusReason(int data) {
		
		String[] switchData  = {"设备产生的数据","查询的数据","定时的数据"};
		
		String dataValue ="";
		
		switch (data) {
			case 0:
				dataValue = switchData[0];
				break;
			case 1:
				dataValue = switchData[1];
				break;
			case 2:
				dataValue = switchData[2];
				break;
			default:
				break;
		}
		return dataValue;
	}
	
	/**
	 * 处理发送是否结束 0发送结束1：未发送结束 0
	* @Description:
	* @param data
	* @return
	 */
	public  String standardEnd(int data) {
		
		String[] switchData  = {"发送结束","未发送结束"};
		
		String dataValue ="";
		
		switch (data) {
			case 0:
				dataValue = switchData[0];
				break;
			case 1:
				dataValue = switchData[1];
				break;
			default:
				break;
		}
		return dataValue;
	}
	
	
	public static void main(String[] args) {
		//StandardSubThingData(new SubThingData());
	}
}
