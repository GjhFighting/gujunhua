package com.gkx.cti.caas.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gkx.cti.caas.feign.OrganClient;
import com.gkx.cti.caas.feign.UserClient;

import feign.Feign;
import feign.Logger;
import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;


/**
 * <p>Portal Feign 配置</p>
 * @author 杨雪令
 * @time 2018年12月10日下午4:59:17
 * @version 1.0
 */
@Configuration
public class FeignPortalConfig {

	private final Log logger = LogFactory.getLog(FeignPortalConfig.class);
	
	
	//portal api 认证用户名
	@Value("${portal-api.path:none}")
	String path;
	
	//portal api 认证用户名
	@Value("${portal-api.user.name:none}")
	String user;
	
	//portal api 认证密码
	@Value("${portal-api.user.password:none}")
	String password;
	
	
	@Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
	

	@Bean
	public RequestInterceptor basicAuthRequestInterceptor() {
		logger.info("Using FeignConfig user "+ user +" ...");
        return new BasicAuthRequestInterceptor(user, password);
    }
	

	@Bean
	public OrganClient organClient() {
		OrganClient organClient = Feign.builder()
			.encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .requestInterceptor(basicAuthRequestInterceptor())
            .target(OrganClient.class, path);
		return organClient;
	}
	
	
	
	@Bean
	public UserClient userClient() {
		UserClient userClient = Feign.builder()
			.encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .requestInterceptor(basicAuthRequestInterceptor())
            .target(UserClient.class, path);
		return userClient;
	}
}