package com.gkx.cti.caas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;



/**
 * <p>Redis Session配置</p>
 * @author 杨雪令
 * @time 2019年4月2日
 * @version 1.0
 */
@Configuration  
@EnableRedisHttpSession(redisNamespace="ctiSession", maxInactiveIntervalInSeconds=21600)
public class RedisSessionConfig {
}