/*
* File name: ThingType.java								
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
* 1.0			"zhouqiang"		Jul 3, 2018
* ...			...			...
*
***************************************************/

package com.jackromer.gateway.enums;

/**
* @Description:	设备类型
* @author: jackromer
* @version: 1.0, Jul 3, 2018
*/

public enum ThingType {
	
	BLUETOOTH_WELL("blueTooth-well"),
	
	BLUETOOTH_HAND_WELL("blueTooth-hand-well"),
	
	UNCONTROLLABLE_WIRELESS_WELL("uncontrollable-wireless-well"),
	
	UNCONTROLLABLE_THING_BREAKOFF_SENSOR("uncontrollable-thing-breakoff-sensor"),
	
	INTEGRATED_WELL("integrated-well"), 
	
	DOUBLE_BLUETOOTH_WELL_3020("double-bluetooth-well"),

	SZ_SECRET_WELL("sz-secret-well");
	
	private final String thingType;
	
	ThingType(String thingType) {
        this.thingType = thingType;
    }

    public String getThingType() {
        return thingType;
    }
}
