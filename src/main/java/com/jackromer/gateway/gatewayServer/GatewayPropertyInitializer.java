/*
* File name: InitGatewayProperty.java								
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
* 1.0			"zhouqiang"		Jul 30, 2018
* ...			...			...
*
***************************************************/

package com.jackromer.gateway.gatewayServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackromer.gateway.utils.PropertyUtil;

/**
* @Description:	
* @author: jackromer
* @version: 1.0, Jul 30, 2018
*/

public class GatewayPropertyInitializer {

	private static final Logger logger = LoggerFactory.getLogger(GatewayPropertyInitializer.class);
	
	private final static String SPLIT = "&";//配置文件分隔符
	
	/**
	 * 通过配置文件地址初始化多个网关的相关信息
	* @Description:
	* @param propertyFileName
	* @return
	 */
	public static List<GatewayBaseData> initGatewayProperty(String propertyFileName) {
		
		List<GatewayBaseData> list = new ArrayList<GatewayBaseData>();
		
		try {
			
			PropertyUtil pu = PropertyUtil.getInstance(propertyFileName).initProp();
			
			Properties p = pu.props;
			
			for (Object o : p.values()) {
				
				String connect = (String) o;
				
				String [] connectArr = connect.split(SPLIT);
				
				Integer   gatewayPort = Integer.parseInt(connectArr[3]);//网关对外提供的端口
				
				String    broker = connectArr[0];//网关连接的MQTT服务器地址
				
				String    gatewayId = connectArr[1];//网关ID
				
				String    gatewayPassword = connectArr[2];//网关连接服务器的密码
				
				GatewayBaseData baseData = new GatewayBaseData(broker, gatewayPort, gatewayId, gatewayPassword);
				
				list.add(baseData);
				
			}
		} catch (NumberFormatException e) {
			
			logger.error("读取配置文件加载启动参数错误！");
			
			e.printStackTrace();
		}
		
		return list; 
		
	}
}
