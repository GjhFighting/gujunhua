package com.gkx.cti.caas.security;

import java.math.BigInteger;
import java.security.MessageDigest;


/**
 * <p>密码加密类</p>
 * @author 杨雪令
 * @time 2018年10月24日上午11:44:14
 * @version 1.0
 */
public class PasswordEncoder {

	/**
	 * <p>对密码进行加密</p>
	 * @param password 密码
	 * @return String 加密后的密码
	 * @author 杨雪令
	 * @time 2018年10月24日上午11:44:41
	 * @version 1.0
	 */
	public static String encode(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.toString().getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            return null;
        }
    }
}
