/*
 * File name: PublishUtil.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Xu Apr 17, 2018 ... ... ...
 *
 ***************************************************/

package com.jackromer.gateway.utils;

import java.util.Random;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description: 组装MQTT publish 消息请求
 * @author: jackromer
 * @version: 1.0, Apr 17, 2018
 */

public class PublishUtil {
	
	volatile private static PublishUtil instance = null;
	
	private PublishUtil(){}
	 
	public static PublishUtil getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (PublishUtil.class) {
				if(instance == null){//二次检查
					instance = new PublishUtil();
				}
			}
		} 
		
		return instance;
	}
	
	/**
	 * 获取publish的 payload
	 * 
	 * @Description:
	 * @param paramName
	 *            方法名
	 * @param RequestId
	 *            自动生成16位随机字符 @param{ "requestId":"gK1nFzu8QQt9Qw98",
	 *            "url":"/things/HNfSxdGUybz055O416sU/shadow/update", "body":{
	 *            "state":{ "reported":{ "red":0, "green":0, "blue":0,
	 *            "light":false } } } }
	 * @return
	 */
	public  JSONObject getPayLoadByParamName(String paramName, String clientId,String subThingId) {
		JSONObject getJson = new JSONObject();
		/*
		 * 获取网关数据流
		 */
		if (paramName.equals("get")) {
			getJson.put("requestId", clientId);
			getJson.put("subThingId", subThingId);
			getJson.put("method", "get");
		}
		
		
		/*
		 * 更新设备 弃用
		 */
		if (paramName.equals("update")) {
			String requestId = getRadomStr(16);
			getJson.put("requestId", requestId);
			getJson.put("url", "/things/" + clientId + "/shadow/update");
			JSONObject body = new JSONObject();// body
			JSONObject state = new JSONObject();// state
			JSONObject reported = new JSONObject();// report

			reported.put("red", "0");
			reported.put("green", "0");
			reported.put("blue", "0");
			reported.put("light", "false");

			state.put("reported", reported);

			body.put("state", state);
			getJson.put("body", body);
		}

		/*
		 * 删除设备
		 */
		if (paramName.equals("delete")) {
			getJson.put("requestId", clientId);
			getJson.put("method", "delete");
			getJson.put("subThingId", subThingId);
		}

		return getJson;
	}



	/**
	 * 随机生成指定长度随机字符串
	 * 
	 * @Description:
	 * @param length
	 * @return
	 */
	public  String getRadomStr(int length) {
		// 产生随机数
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		// 循环length次
		for (int i = 0; i < length; i++) {
			// 产生0-2个随机数，既与a-z，A-Z，0-9三种可能
			int number = random.nextInt(3);
			long result = 0;
			switch (number) {
			// 如果number产生的是数字0；
			case 0:
				// 产生A-Z的ASCII码
				result = Math.round(Math.random() * 25 + 65);
				// 将ASCII码转换成字符
				sb.append(String.valueOf((char) result));
				break;
			case 1:
				// 产生a-z的ASCII码
				result = Math.round(Math.random() * 25 + 97);
				sb.append(String.valueOf((char) result));
				break;
			case 2:
				// 产生0-9的数字
				sb.append(String.valueOf(new Random().nextInt(10)));
				break;
			}
		}
		return sb.toString();
	}



	public static void main(String[] args) throws Exception {
		// System.out.println(getRadomStr(16));
		//System.out.println(getPayLoadByParamName("update", "zhouqiang"));
		System.out.println("-------------------------------------");
		//System.out.println(getPayLoadByParamName("delete", "zhouqiang"));
	}

}
