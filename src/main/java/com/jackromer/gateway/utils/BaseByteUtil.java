/*
* File name: BaseToByte.java								
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

package com.jackromer.gateway.utils;

/**
* @Description:	
* @author: jackromer
* @version: 1.0, Jun 20, 2018
*/

import java.io.*;  

public class BaseByteUtil {  
	
	volatile private static BaseByteUtil instance = null;
	
	private BaseByteUtil(){}
	 
	public static BaseByteUtil getInstance() {
	 
		if(instance != null){//懒汉式 
			
		}else{
			//创建实例之前可能会有一些准备性的耗时工作 
			synchronized (BaseByteUtil.class) {
				if(instance == null){//二次检查
					instance = new BaseByteUtil();
				}
			}
		} 
		
		return instance;
	}
	
    /** 
     * @方法功能 InputStream 转为 byte 
     * @param InputStream 
     * @return 字节数组 
     * @throws Exception 
     */  
    public  byte[] inputStream2Byte(InputStream inStream)  
            throws Exception {  
        int count = 0;  
        while (count == 0) {  
            count = inStream.available();  
        }  
        byte[] b = new byte[count];  
        inStream.read(b);  
        return b;  
    }  
  
    /** 
     * @方法功能 byte 转为 InputStream 
     * @param 字节数组 
     * @return InputStream 
     * @throws Exception 
     */  
    public  InputStream byte2InputStream(byte[] b) throws Exception {  
        InputStream is = new ByteArrayInputStream(b);  
        return is;  
    }  
  
    
    /** 
     * @功能 短整型与字节的转换 
     * @param 短整型 
     * @return 两位的字节数组 
     */  
    public  byte[] shortToByte(short number) {  
        int temp = number;  
        byte[] b = new byte[2];  
        for (int i = 0; i < b.length; i++) {  
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位  
            temp = temp >> 8; // 向右移8位  
        }  
        return b;  
    }  
    
    
    
	/**
	 * 
	* @Description: string to byte
	* @param str
	* @param byteLength
	* @return
	 */
    public  byte[] strToByte(String str,int byteLength) {
    	byte[] b = new byte[byteLength];
    	b = str.getBytes();
    	return b;
    }
    
    /**
     * 
    * @Description: byte to string
    * @param b
    * @return
     */
    public  String byteToStr(byte[] b) {
    	String result = new String(b);
    	return result;
    }
    
    /** 
     * @功能 字节的转换与短整型 
     * @param 两位的字节数组 
     * @return 短整型 
     */  
    public  short byteToShort(byte[] b) {  
        short s = 0;  
        short s0 = (short) (b[0] & 0xff);// 最低位  
        short s1 = (short) (b[1] & 0xff);  
        s1 <<= 8;  
        s = (short) (s0 | s1);  
        return s;  
    }  
  
    /** 
     * @功能        字节转换为短整型无符号short
     * @param 两位的字节数组 
     * @return 短整型 
     */  
    public  int byteToUnSinghedShort(byte[] b) {  
        short s = 0;  
        short s0 = (short) (b[0] & 0xff);// 最低位  
        short s1 = (short) (b[1] & 0xff);  
        s1 <<= 8;  
        s = (short) (s0 | s1);  
        return s & 0xFFFF;  
    }  
    
    
    /** 
     * @方法功能 整型与字节数组的转换 
     * @param 整型 
     * @return 四位的字节数组 
     */  
    public  byte[] intToByte(int i) {  
        byte[] bt = new byte[4];  
        bt[0] = (byte) (0xff & i);  
        bt[1] = (byte) ((0xff00 & i) >> 8);  
        bt[2] = (byte) ((0xff0000 & i) >> 16);  
        bt[3] = (byte) ((0xff000000 & i) >> 24);  
        return bt;  
    }  
    
    
    public  byte[] int2byte(int res) {  
    	byte[] targets = new byte[4];  
    	  
    	targets[0] = (byte) (res & 0xff);// 最低位   
    	targets[1] = (byte) ((res >> 8) & 0xff);// 次低位   
    	targets[2] = (byte) ((res >> 16) & 0xff);// 次高位   
    	targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。   
    	return targets;   
    	}   
  
    /** 
     * @方法功能 字节数组和整型的转换 
     * @param 字节数组 
     * @return 整型 
     */  
    public  int bytesToInt(byte[] bytes) {  
        int num = bytes[0] & 0xFF;  
        num |= ((bytes[1] << 8) & 0xFF00);  
        num |= ((bytes[2] << 16) & 0xFF0000);  
        num |= ((bytes[3] << 24) & 0xFF000000);  
        return num;  
    }  
  
    /** 
     * @方法功能 字节数组和长整型的转换 
     * @param 字节数组 
     * @return 长整型 
     */  
    public  byte[] longToByte(long number) {  
        long temp = number;  
        byte[] b = new byte[8];  
        for (int i = 0; i < b.length; i++) {  
            b[i] = new Long(temp & 0xff).byteValue();  
            // 将最低位保存在最低位  
            temp = temp >> 8;  
            // 向右移8位  
        }  
        return b;  
    }  
  
    /** 
     * @方法功能 字节数组和长整型的转换 
     * @param 字节数组 
     * @return 长整型 
     */  
    public  long byteToLong(byte[] b) {  
        long s = 0;  
        long s0 = b[0] & 0xff;// 最低位  
        long s1 = b[1] & 0xff;  
        long s2 = b[2] & 0xff;  
        long s3 = b[3] & 0xff;  
        long s4 = b[4] & 0xff;// 最低位  
        long s5 = b[5] & 0xff;  
        long s6 = b[6] & 0xff;  
        long s7 = b[7] & 0xff; // s0不变  
        s1 <<= 8;  
        s2 <<= 16;  
        s3 <<= 24;  
        s4 <<= 8 * 4;  
        s5 <<= 8 * 5;  
        s6 <<= 8 * 6;  
        s7 <<= 8 * 7;  
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;  
        return s;  
    }  
    
    /**
     * 拼接字节数组
    * @Description:
    * @param data
    * @return
     */
    public  byte[] concatBytes(byte[] ...data) { 
    	int length = 0;
    	//计算byte数组长度
    	for (int i = 0; i < data.length; i ++) {
    		length += data[i].length;
		}
    	
        byte[] result = new byte[length];  
        //拼接字节数组
        int startDes = 0;
        for (int i = 0; i < data.length; i ++) {
        	 byte[] itor = data[i];
        	 System.arraycopy(itor, 0, result, startDes, itor.length);
        	 startDes = startDes + itor.length ;
		}
        
        return result;  
    }  
    
  /**
   * 截取数组
  * @Description:
  * @param data
  * @param start
  * @param length copy length
  * @return
   */
    public  byte[] subStrByteArray(byte[] data,int start,int length) {
    	
    	byte[] result  = new byte[length];
    	
    	System.arraycopy(data, start, result, 0, length);
    	
    	return result;
    }
    
    
    /**
     * 解析时间  必须为6个字节-年月日时分秒
    * @Description:
    * @param data
    * @return 2018-06-12 16:13:54
     */
    public  String parseTime(byte[] data) {
    	
    	int year = data[0];
    	int month = data[1];
    	int day = data[2];
    	int hour = data[3];
    	int minute = data[4];
    	int second = data[5];
    	
    	String yearStr = "20" + String.valueOf(year);
    	
    	String monthStr = month > 10 ? String.valueOf(month) : "0" + String.valueOf(month);
    	
    	String dayStr = day > 10 ? String.valueOf(day) : "0" + String.valueOf(day);
    	
    	String hourStr = hour > 10 ? String.valueOf(hour) : "0" + String.valueOf(hour);
    	
    	String minuteStr = minute > 10 ? String.valueOf(minute) : "0" + String.valueOf(minute);
    	
    	String secondStr = second > 10 ? String.valueOf(second) : "0" + String.valueOf(second);
    	
    	String timeStr = yearStr + "-" + monthStr + "-" + dayStr + " " + hourStr + ":" + minuteStr + ":" + secondStr;
    	
    	//System.out.println("上报时间为"+timeStr);
    	
    	return timeStr;
    }
    
    
    public static void main(String[] args) {
		/*byte [] a = {1,2,3};
		byte [] b = {11,22,33};
		byte [] c = {110,120,119};
		byte [] result = concatBytes(a, b,c);
		System.out.println(result.length);
		for (byte re : result) {
			System.out.println(re);
		}*/
    	/*byte[] s = int2byte(64958);
    	for (byte b : s) {
			System.out.println(b);
		}*/
    	/*byte [] b = {-65,-3};//64959
    	byte [] b2 = {-3,-65};//64959 应该传这个
    	short s = byteToShort(b2);
    	int v = s & 0xFFFF;
    	System.out.println(v);*/
	}
}  