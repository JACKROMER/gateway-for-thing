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

/**
* @Description:	
* @author: jackromer
* @version: 1.0, Jun 19, 2018
*/

import org.apache.commons.codec.binary.Base64;

/**
 * 将String进行base64编码解码，使用utf-8
 */
public class Base64Util {

	//private static final Logger logger = LoggerFactory.getLogger(Base64Util.class);

    //private static final String UTF_8 = "UTF-8";
	volatile private static Base64Util instance = null;
	
	private Base64Util(){}
	 
	public static Base64Util getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (Base64Util.class) {
				if(instance == null){//二次检查
					instance = new Base64Util();
				}
			}
		} 
		
		return instance;
	}

    /**
     * 对给定的字符串进行base64解码操作
     */
    public byte[] decodeData(byte[] inputData) {
    	
    	byte [] reslut = new byte[0];
        if (null == inputData || inputData.length == 0) {
		    return reslut;
		}
		return Base64.decodeBase64(inputData);
    }

    /**
     * 对给定的字符串进行base64加密操作
     */
    public  byte[] encodeData(byte[] inputData) {
    	byte [] reslut = new byte[0];
        if (null == inputData || inputData.length == 0) {
		    return reslut;
		}
		return Base64.encodeBase64(inputData);
    }

    public static void main(String[] args) {
        //System.out.println(Base64Util.encodeData("sss"));
    }

}