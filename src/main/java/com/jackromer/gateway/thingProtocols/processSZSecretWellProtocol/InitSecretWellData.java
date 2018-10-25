package com.jackromer.gateway.thingProtocols.processSZSecretWellProtocol;

import com.jackromer.gateway.utils.Base64Util;
import com.jackromer.gateway.utils.XorUtil;

/**
 * @Description: 密码设备上报数据模拟
 * @author: jackromer
 * @version: 1.0, 2018年07月16日
 */
public class InitSecretWellData {


    public static String originalData(){
        //数据帧头
        String header  = "*460029961128089*";

        //数据区数据
        String data  = "cdsfrj0204,10,20,21,0023,";

        String state ="state[t,5.6,14,iv,c,20180716103004,f]";

        //数据帧尾
        String end = "#";

        // payload XOR加密
        byte[] xorPayload = XorUtil.getInstance().xorEncode((data+state).getBytes());

        // payload base64加密
        byte[] base64Payload = Base64Util.getInstance().encodeData(xorPayload);

        return header + new String(base64Payload) + end;
    }

    //Ul1dUlxRF1xNawMCAgIFBwUPCAIECgMEaA==
    public static String decryptData(){
        String data = "028NDMyPXZyZH4LAAUDCzk0NTbFhg==";
        byte[] base64Bate = Base64Util.getInstance().decodeData(data.getBytes());

        byte[] bytes = XorUtil.getInstance().xorDecode(base64Bate);

        return new String(bytes);
    }

    public static void main(String[] args) {
       /* String ss1 = "*111111111111111*ASDSAFA=#*4515";
    //  String ss = "cdsfrj0204,10,20,21,0023,state[t,5.6,14,iv,c,20160225183004,f]";
      //  boolean matches = Pattern.matches("^\\*.*", ss1);
        boolean matches = Pattern.matches("^\\*[0-9]{15}\\*[a-zA-Z\\d=]+#.*$", ss1);
        System.out.println(matches);
        Matcher matcher = Pattern.compile("^\\*[0-9]{15}\\*[a-zA-Z\\d=]+#.*$").matcher(ss1);
        while (matcher.find()) {
            System.err.println(matcher.group(0));
        }
*/
        System.err.println(originalData());
        /*String[] split = ss.split(",");
        for (String s: split) {
            System.err.println(s + "   ");
        }*/
    }




}
