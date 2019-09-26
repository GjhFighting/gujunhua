package com.gkx.cti.caas.security.handler;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import com.gkx.cti.caas.config.Configs;
import com.gkx.cti.caas.feign.OrganClient;
import com.gkx.cti.caas.feign.UserClient;


/**
 * <p>认证成功处理器</p>
 * @author 杨雪令
 * @time 2018年7月9日下午12:00:06
 * @version 1.0
 */
@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	@Autowired
	private Configs configs;
	
	@Autowired
	private UserClient userClient;
	
	@Autowired
	private OrganClient organClient;
	
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private FindByIndexNameSessionRepository findByIndexNameSessionRepository;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException, ServletException {

		UserDetails userDetails = (UserDetails) auth.getPrincipal();
		logger.info("认证成功, username=" + userDetails.getUsername());
		request.getSession().setAttribute("username", userDetails.getUsername());
		
		
		
		//Gateway用户登陆强踢
		Map<String, Session> sessionMap = findByIndexNameSessionRepository.findByIndexNameAndIndexValue(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, userDetails.getUsername());
		for(Map.Entry<String, Session> entry : sessionMap.entrySet()) {
			logger.info("Gateway用户登陆强踢["+ userDetails.getUsername() +"], session=" + entry.getKey());
			findByIndexNameSessionRepository.deleteById(entry.getKey());
		}
		
		
		//用户信息
		Map<String, String> user = userClient.findByUsername(userDetails.getUsername());
		logger.info("用户信息：" + user);
		request.getSession().setAttribute("userId", user.get("id"));
		request.getSession().setAttribute("tenantId", user.get("tenant"));
		request.getSession().setAttribute("tenantCode", user.get("tenantCode"));
		request.getSession().setAttribute("organId", user.get("organId"));
		request.getSession().setAttribute("accountType", user.get("accountType"));
		request.getSession().setAttribute("language", user.get("language"));
		request.getSession().setAttribute("fullName", user.get("name"));
		
		
		//机构信息
		Map<String, String> organ = organClient.get(user.get("organId"));
		request.getSession().setAttribute("organName", organ.get("name"));
		
		
		
		response.sendRedirect(configs.pathServer + "/index");
	}
}