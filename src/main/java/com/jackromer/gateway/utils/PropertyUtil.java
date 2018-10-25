/*
 * File name: PropertyUtil.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Xu Apr 23, 2018 ... ... ...
 *
 ***************************************************/

package com.jackromer.gateway.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:
 * @author: jackromer
 * @version: 1.0, Apr 23, 2018
 */

public class PropertyUtil {

	private static final Logger	logger	= LoggerFactory.getLogger(PropertyUtil.class);
	
	public Properties	props;
	
	private  String fileName;
	
	volatile private static PropertyUtil instance = null;
	
	public PropertyUtil(String fileName) {
		this.fileName = fileName;
	}
	
	public static PropertyUtil getInstance(String fileName) {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (PropertyUtil.class) {
				if(instance == null){//二次检查
					instance = new PropertyUtil(fileName);
				}
			}
		} 
		
		return instance;
	}
	
	/**
	 * 通过配置文件地址初始化配置
	 * @Description:
	 */
	public  PropertyUtil initProp() {
		return loadProps();
	}
	
	
	/**
	 * 初始化配置文件数据
	* @Description:
	 */
	
	synchronized  private PropertyUtil loadProps() {
		
		PropertyUtil pu = PropertyUtil.getInstance(fileName);
		
		logger.info("开始加载properties文件内容.......");
		
		pu.props = new Properties();
		
		InputStream in = null;
		
		try {
			// -第一种，通过类加载器进行获取properties文件流
			//in = PropertyUtil.class.getClassLoader().getResourceAsStream("app.properties");
			// 第二种，通过类进行获取properties文件流--
			if(fileName.contains("config/base")) {
				in = PropertyUtil.class.getClassLoader().getResourceAsStream(fileName);
			}else {
				in = new FileInputStream(fileName);
			}
			props.load(in);
		} catch (FileNotFoundException e) {
			logger.error("mqttInfo.properties文件未找到");
		} catch (IOException e) {
			logger.error("出现IOException");
		} finally {
			try {
				if (null != in) {
					in.close();
				}
			} catch (IOException e) {
				logger.error("gateway.properties文件流关闭出现异常");
			}
		}
		logger.info("加载properties文件内容完成...........");
		logger.info("properties文件内容：" + props);
		return pu;
	}



	public  String getProperty(String key) {
		if (null == props) {
			loadProps();
		}
		return props.getProperty(key);
	}



	public  String getProperty(String key, String defaultValue) {
		if (null == props) {
			loadProps();
		}
		return props.getProperty(key, defaultValue);
	}
}