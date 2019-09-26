package com.gkx.cti.caas.feign;

import java.util.List;
import java.util.Map;
import java.util.Set;

import feign.Headers;
import feign.Param;
import feign.RequestLine;


/**
 * <p>用户接口</p>
 * @author 杨雪令
 * @time 2018年12月11日上午10:13:46
 * @version 1.0
 */
public interface UserClient {


	/**
	 * <p>根据用户名查询用户信息</p>
	 * @param username 用户名
	 * @author 杨雪令
	 * @time 2018年12月11日上午10:14:11
	 * @version 1.0
	 */
	@Headers({"Content-Type: application/json","Accept: application/json"})
	@RequestLine("GET /user/findByUsername?username={username}")
	Map<String, String> findByUsername(@Param("username") String username);
	
	
	/**
	 * <p>根据用户名获取软电话基本信息</p>
	 * @param username 用户名
	 * @author 朱远平
	 * @time 2019年1月31日上午10:14:11
	 * @version 1.0
	 */
	@Headers({"Content-Type: application/json","Accept: application/json"})
	@RequestLine("GET /user/findSoftPhoneMsg?username={username}")
	Map<String, Object> findSoftPhoneMsg(@Param("username") String username);
	
	
	
	
	/**
	 * <p>根据用户名查询用户按钮权限Url</p>
	 * @param username 用户名
	 * @param systemCode 系统代码
	 * @author 朱远平
	 * @time 2018年12月26日上午10:14:11
	 * @version 1.0
	 */
	@Headers({"Content-Type: application/json","Accept: application/json"})
	@RequestLine("GET /menu/findAuthUrl?username={username}&systemCode={systemCode}")
	Set<String> findAuthUrl(@Param("username") String username, @Param("systemCode") String systemCode);
	
	
	
	
	/**
	 * <p>根据用户名查询用户按钮权限信息Id</p>
	 * @param username 用户名
	 * @param systemCode 系统代码
	 * @author 杨雪令
	 * @time 2019年1月9日下午2:53:04
	 * @version 1.0
	 */
	@Headers({"Content-Type: application/json","Accept: application/json"})
	@RequestLine("GET /menu/findAuthId?username={username}&systemCode={systemCode}")
	Set<String> findAuthId(@Param("username") String username, @Param("systemCode") String systemCode);


	/**
	 * <p>根据用户名查询用户菜单</p>
	 * @param username 用户名
	 * @param systemCode 系统代码
	 * @author 杨雪令
	 * @time 2019年4月11日
	 * @version 1.0
	 */
	@Headers({"Content-Type: application/json","Accept: application/json"})
	@RequestLine("GET /menu/findMenus?username={username}&systemCode={systemCode}")
	List<Map<String, String>> findMenus(@Param("username") String username, @Param("systemCode")  String systemCode);
	
	
	
	
}