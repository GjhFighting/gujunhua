package com.gkx.cti.caas;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.ql.util.spring.boot.annotation.EnableCors;
import com.ql.util.spring.boot.annotation.EnableCustomerError;
import com.ql.util.spring.boot.annotation.EnableDuplicateSubmitExclude;
import com.ql.util.spring.boot.annotation.EnableHttpOptionsHandle;


/**
 * <p>caas启动类</p>
 * @author 杨雪令
 * @time 2019年8月2日
 * @version 1.0
 */
@SpringBootApplication
@EnableCors
@EnableDuplicateSubmitExclude
@EnableHttpOptionsHandle
@EnableCustomerError
public class App extends SpringBootServletInitializer {
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(App.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}
}