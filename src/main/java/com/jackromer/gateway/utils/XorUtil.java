/*
* File name: 是是是.java								
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

import java.nio.charset.Charset;

/**
* @Description:	
* @author: jackromer
* @version: 1.0, Jun 19, 2018
*/

public class XorUtil {  
	
	volatile private static XorUtil instance = null;
	
	private XorUtil(){}
	 
	public static XorUtil getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (XorUtil.class) {
				if(instance == null){//二次检查
					instance = new XorUtil();
				}
			}
		} 
		
		return instance;
	}
	
	private static final String key0 = "1234567890";
	private static final Charset charset = Charset.forName("UTF-8");
    private static byte[] keyBytes = key0.getBytes(charset);
    public static void main(String[] args) {  
        // TODO Auto-generated method stub  
       /* String data="hello world";  
          
        System.out.println("------------原文--------------");  
        System.out.println(data);  
          
        String after = new String(xorEncode(data.getBytes()));  
        System.out.println("------------加密--------------");  
        System.out.println(after);  
          
        String before=new String(xorEncode(after.getBytes()));  
        System.out.println("------------解密---------------");  
        System.out.println(before);  
          
        String key=new String(xorEncode(new byte[50]));  
        System.out.println("------------秘钥---------------");  
        System.out.println(key);  */
    }  
    
    /**
     * xor 加密 key=1234567890
    * @Description:
    * @param data
    * @return
     */
    public  byte[] xorEncode(byte[] data){  
    	 
         for(int i = 0; i < data.length; ++i){  
             data[i] ^= keyBytes[i%keyBytes.length];  
         }  
         
         return data;  
         
    }  

    
    /**
     * xor 解密 key=1234567890
    * @Description:
    * @param data
    * @return
     */
    public  byte[] xorDecode(byte[] data){
    	
    	for(int i = 0; i < data.length; ++i){  
            data[i] ^= keyBytes[i%keyBytes.length];  
        }  
        return data;
    }
    
   
}  
