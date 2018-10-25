package com.jackromer.gateway.enums;

/**
* @Description:	编解码加密类型
* @author: jackromer
* @version: 1.0, Jul 24, 2018
*/
public enum SignMethodType {
    HMACMD5,
    HMACSHA1;

    public static SignMethodType getMethodFromString (String name) {
        return SignMethodType.valueOf(name.toUpperCase());
    }
}
