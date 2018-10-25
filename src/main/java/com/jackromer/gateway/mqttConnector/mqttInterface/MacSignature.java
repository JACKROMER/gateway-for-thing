/*
 * File name: MacSignature.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Xu Apr 16, 2018 ... ... ...
 *
 ***************************************************/

package com.jackromer.gateway.mqttConnector.mqttInterface;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.paho.client.mqttv3.internal.websocket.Base64;
/**
 * @Description:
 * @author: jackromer MD5签名
 * @version: 1.0, Apr 16, 2018
 */

public class MacSignature {
	/**
	 * @param text
	 *            要签名的文本
	 * @param secretKey
	 *            阿里云 MQ SecretKey
	 * @return 加密后的字符串
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 */
	// 用户名不需要加密 ，密码做小写的MD5加密
	public static String macSignature(String text, String secretKey)
			throws InvalidKeyException, NoSuchAlgorithmException {
		Charset charset = Charset.forName("UTF-8");
		String algorithm = "HmacSHA1";
		Mac mac = Mac.getInstance(algorithm);
		mac.init(new SecretKeySpec(secretKey.getBytes(charset), algorithm));
		byte[] bytes = mac.doFinal(text.getBytes(charset));
		// return new String(Base64.encodeBytes(bytes), charset);
		return new String(Base64.encodeBytes(bytes));
	}



	/**
	 * 发送方签名方法
	 *
	 * @param clientId
	 *            MQTT ClientID
	 * @param secretKey
	 *            阿里云 MQ SecretKey
	 * @return 加密后的字符串
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static String publishSignature(String clientId, String secretKey)
			throws NoSuchAlgorithmException, InvalidKeyException {
		return macSignature(clientId, secretKey);
	}



	/**
	 * 订阅方签名方法
	 *
	 * @param topics
	 *            要订阅的 Topic 集合
	 * @param clientId
	 *            MQTT ClientID
	 * @param secretKey
	 *            阿里云 MQ SecretKey
	 * @return 加密后的字符串
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static String subSignature(List<String> topics, String clientId, String secretKey)
			throws NoSuchAlgorithmException, InvalidKeyException {
		Collections.sort(topics); // 以字典顺序排序
		String topicText = "";
		for (String topic : topics) {
			topicText += topic + "\n";
		}
		String text = topicText + clientId;
		return macSignature(text, secretKey);
	}



	/**
	 * 订阅方签名方法
	 *
	 * @param topic
	 *            要订阅的 Topic
	 * @param clientId
	 *            MQTT ClientID
	 * @param secretKey
	 *            阿里云 MQ SecretKey
	 * @return 加密后的字符串
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static String subSignature(String topic, String clientId, String secretKey)
			throws NoSuchAlgorithmException, InvalidKeyException {
		List<String> topics = new ArrayList<String>();
		topics.add(topic);
		return subSignature(topics, clientId, secretKey);
	}



	/**
	 * md5 小写加密
	 * 
	 * @Description:
	 * @param password
	 * @return
	 */
	public static String md5Password(String password) {

		try {
			// 得到一个信息摘要器
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] result = digest.digest(password.getBytes());
			StringBuffer buffer = new StringBuffer();
			// 把每一个byte 做一个与运算 0xff;
			for (byte b : result) {
				// 与运算
				int number = b & 0xff;// 加盐
				String str = Integer.toHexString(number);
				if (str.length() == 1) {
					buffer.append("0");
				}
				buffer.append(str);
			}

			// 标准的md5加密后的结果
			return buffer.toString().toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}

	}



	/**
	 * 	
	* @Description:
	* @param key
	* @param data
	* @return
	 * @throws InvalidKeyException 
	 * @throws NoSuchAlgorithmException 
	 */
	
	/*public static String macMd5Password(String key, String data) throws InvalidKeyException, NoSuchAlgorithmException {
			String signType = "HMACMD5";
			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), signType.toLowerCase());
			Mac mac = Mac.getInstance("HMACMD5");
			mac.init(secretKeySpec);
			byte[] result = mac.doFinal(data.getBytes());
			return null;
			
			return Hex.toHexString(result).toLowerCase();
	}*/
}
