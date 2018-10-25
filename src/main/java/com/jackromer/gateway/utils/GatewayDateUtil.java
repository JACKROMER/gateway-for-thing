/*
* File name: sss.java								
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
* 1.0			"zhouqiang"		Jun 19, 2018
* ...			...			...
*
***************************************************/

package com.jackromer.gateway.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 将String进行base64编码解码，使用utf-8
 */
public class GatewayDateUtil {

	private static final Logger logger = LoggerFactory.getLogger(GatewayDateUtil.class);

    //private static final String UTF_8 = "UTF-8";
	volatile private static GatewayDateUtil instance = null;
	
	private GatewayDateUtil(){}
	 
	public static GatewayDateUtil getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (GatewayDateUtil.class) {
				if(instance == null){//二次检查
					instance = new GatewayDateUtil();
				}
			}
		} 
		
		return instance;
	}

	
	/**
	 * 将时间戳转换为时间 1529906701777
	* @Description:
	* @param s
	* @return
	 */
    public  Date stampToDate(String s){
    	
        String res;
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        long lt = new Long(s);
        
        Date date = new Date(lt);
        
        res = simpleDateFormat.format(date);
        
        try {
			date = simpleDateFormat.parse(res);
		} catch (ParseException e) {
			logger.info("时间戳转换失败！");
			e.printStackTrace();
		}
        
        return date;
    }
    
    
	 /**
     * 校验时间戳是否超过期(三分钟)
     * @Description:未超过三分钟返回true,小于三分钟返回 false
     */
    
    public  boolean checkCommandTime(Date now ,String timeStamp) {
    	boolean flag = true;
    	
    	Date shadowDate = stampToDate(timeStamp);
    	
    	//小于三分钟,则下发命令
    	if((now.getTime() - shadowDate.getTime()) < 1000 * 60 * 3) {
    		flag = false;
    	}
    	return flag;
    }

}