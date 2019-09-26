package com.gkx.cti.caas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>系统配置</p>
 * @author 杨雪令
 * @time 2018年10月19日上午10:02:29
 * @version 1.0
 */
@Component
public class Configs {

	
    //CAS服务器地址
	@Value("${path.cas}")
    public String pathCas = "";
	
	
	//CAS服务器登录地址
	@Value("${path.cas-login}")
    public String pathCasLogin = "";
	
	//CAS服务器登出地址
	@Value("${path.cas-logout}")
    public String pathCasLogout = "";
	
	
	//后端服务地址
	@Value("${path.server}")
	public String pathServer = "";
	
	
	//前端html地址
	@Value("${path.html}")
	public String pathHtml = "";
		
}
