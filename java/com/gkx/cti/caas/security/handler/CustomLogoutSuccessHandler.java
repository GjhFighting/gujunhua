package com.gkx.cti.caas.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;


/**
 * <p>登出成功处理器</p>
 * @author 杨雪令
 * @time 2018年7月9日下午12:00:06
 * @version 1.0
 */
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
	
	private final Log logger = LogFactory.getLog(CustomLogoutSuccessHandler.class);

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		
		String casLogoutPath = "/logout?service=";
		logger.info("logout casLogoutPath=" + casLogoutPath);
		response.sendRedirect(casLogoutPath);
	}
}