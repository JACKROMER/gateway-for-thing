/*
* File name: SubThingData.java								
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

package com.jackromer.gateway.thingProtocols.processCurrencyProrocol;

/**
* @Description:	设备影子数据类
* @author: jackromer
* @version: 1.0, Jun 21, 2018
*/

public class CurrencySubThingData {
	
			//开始解析数据
			//------------------------thing type to protocal--------------------------------------------------
			/**
			 * 设备类型
			 */
			private String thingType ; 
			/**
			 * 控制类型 1
			 */
			private String controlType ;
			/**
			 * 协议版本 1
			 */
			private byte protocalVersion;
			
			//------------------------thing code --------------------------------------------------
			
			/**
			 * 设备编码  cdsfrj
			 */
			private String thingCode ;
			
			//-------------------------wire to KeyLength -------------------------------------------------
			/**
			 * 固件版本 1
			 */
			private int wireVersion ;
			/**
			 * 硬件版本 1 
			 */
			private int hardVersion ;
			/**
			 * command
			 */
			private int command ;
			/**
			 * key 1 
			 */
			private int key ;
			
			//--------------------------key value------------------------------------------------
			
			/**
			 * IN 
			 */
			private String in ;
			/**
			 * OUT
			 */
			private String out ;	
			/**
			 * HX
			 */
			private String hx ;	
			/**
			 * RE
			 */
			private String re ;	
			
			/**
			 * TIME	>>> 6 字节 1703131355
			 */
			private String time;
			/**
			 * SIG
			 */
			private int sig ;	
			/**
			 * BT
			 */
			private int bt  ;
			/**
			 * T
			 */
			private int t ;
			/**
			 * P
			 */
			private int p ;
			
			/**
			 * SN	>>>2 字节
			 */
			private short sn ;
			/**
			 * END
			 */
			private String end ;
			
			/**
			 * ID >>>idlen 字节
			 */
			private String id;

			/**
			 * @return the thingType
			 */
			public String getThingType() {
				return thingType;
			}

			/**
			 * @param thingType the thingType to set
			 */
			public void setThingType(String thingType) {
				this.thingType = thingType;
			}

			/**
			 * @return the controlType
			 */
			public String getControlType() {
				return controlType;
			}

			/**
			 * @param controlType the controlType to set
			 */
			public void setControlType(String controlType) {
				this.controlType = controlType;
			}

			/**
			 * @return the protocalVersion
			 */
			public byte getProtocalVersion() {
				return protocalVersion;
			}

			/**
			 * @param protocalVersion the protocalVersion to set
			 */
			public void setProtocalVersion(byte protocalVersion) {
				this.protocalVersion = protocalVersion;
			}

			/**
			 * @return the thingCode
			 */
			public String getThingCode() {
				return thingCode;
			}

			/**
			 * @param thingCode the thingCode to set
			 */
			public void setThingCode(String thingCode) {
				this.thingCode = thingCode;
			}

			/**
			 * @return the wireVersion
			 */
			public int getWireVersion() {
				return wireVersion;
			}

			/**
			 * @param wireVersion the wireVersion to set
			 */
			public void setWireVersion(int wireVersion) {
				this.wireVersion = wireVersion;
			}

			/**
			 * @return the hardVersion
			 */
			public int getHardVersion() {
				return hardVersion;
			}

			/**
			 * @param hardVersion the hardVersion to set
			 */
			public void setHardVersion(int hardVersion) {
				this.hardVersion = hardVersion;
			}

			/**
			 * @return the command
			 */
			public int getCommand() {
				return command;
			}

			/**
			 * @param command the command to set
			 */
			public void setCommand(int command) {
				this.command = command;
			}

			/**
			 * @return the key
			 */
			public int getKey() {
				return key;
			}

			/**
			 * @param key the key to set
			 */
			public void setKey(int key) {
				this.key = key;
			}

			/**
			 * @return the in
			 */
			public String getIn() {
				return in;
			}

			/**
			 * @param in the in to set
			 */
			public void setIn(String in) {
				this.in = in;
			}

			/**
			 * @return the out
			 */
			public String getOut() {
				return out;
			}

			/**
			 * @param out the out to set
			 */
			public void setOut(String out) {
				this.out = out;
			}

			/**
			 * @return the hx
			 */
			public String getHx() {
				return hx;
			}

			/**
			 * @param hx the hx to set
			 */
			public void setHx(String hx) {
				this.hx = hx;
			}

			/**
			 * @return the re
			 */
			public String getRe() {
				return re;
			}

			/**
			 * @param re the re to set
			 */
			public void setRe(String re) {
				this.re = re;
			}

			/**
			 * @return the time
			 */
			public String getTime() {
				return time;
			}

			/**
			 * @param time the time to set
			 */
			public void setTime(String time) {
				this.time = time;
			}

			/**
			 * @return the sig
			 */
			public int getSig() {
				return sig;
			}

			/**
			 * @param sig the sig to set
			 */
			public void setSig(int sig) {
				this.sig = sig;
			}

			/**
			 * @return the bt
			 */
			public int getBt() {
				return bt;
			}

			/**
			 * @param bt the bt to set
			 */
			public void setBt(int bt) {
				this.bt = bt;
			}

			/**
			 * @return the t
			 */
			public int getT() {
				return t;
			}

			/**
			 * @param t the t to set
			 */
			public void setT(int t) {
				this.t = t;
			}

			/**
			 * @return the p
			 */
			public int getP() {
				return p;
			}

			/**
			 * @param p the p to set
			 */
			public void setP(int p) {
				this.p = p;
			}

			/**
			 * @return the sn
			 */
			public short getSn() {
				return sn;
			}

			/**
			 * @param sn the sn to set
			 */
			public void setSn(short sn) {
				this.sn = sn;
			}

			/**
			 * @return the end
			 */
			public String getEnd() {
				return end;
			}

			/**
			 * @param end the end to set
			 */
			public void setEnd(String end) {
				this.end = end;
			}

			/**
			 * @return the id
			 */
			public String getId() {
				return id;
			}

			/**
			 * @param id the id to set
			 */
			public void setId(String id) {
				this.id = id;
			}

			

}
