package com.jackromer.gateway.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Hex;

import com.jackromer.gateway.enums.SignMethodType;

/**
 * Created by RichieMay on 2018/2/6 0006. 验证用户名和密码util类
 */
public class AuthUtil {


	/**
	 *校验不同的加密许可证
	* @Description:
	 * @param key
	* @param data
	* @param signType
	* @return
	* @throws Exception
	 */
	public static String cipherMac(SignMethodType signType, String key, String data) throws Exception {
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), signType.toString().toLowerCase());
		Mac mac = Mac.getInstance(signType.toString());
		mac.init(secretKeySpec);
		byte[] result = mac.doFinal(data.getBytes());
		return Hex.toHexString(result).toLowerCase();
	}

	public static boolean authenticate(SignMethodType signMethod, String key, String content, String signature) throws Exception {
		return signature.toLowerCase().equals(cipherMac(signMethod, key, content));
	}
}
