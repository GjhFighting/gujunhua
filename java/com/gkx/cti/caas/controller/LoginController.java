package com.gkx.cti.caas.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ql.util.api.ResponesData;
import com.ql.util.web.HttpUtil;
import com.ql.util.web.RequestUtil;
import com.gkx.cti.caas.config.Configs;



/**
 * <p>登录登出控制器</p>
 * @author 杨雪令
 * @time 2019年1月9日上午11:39:00
 * @version 1.0
 */
@RestController
public class LoginController {
	
	@Autowired
	private Configs configs;
	
	
	/**
	 * <p>入口，重定向到前端</p>
	 * @author 杨雪令
	 * @time 2018年11月6日下午7:06:17
	 * @version 1.0
	 */
	@RequestMapping(value="/index")
	public void index(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		boolean isMobile = RequestUtil.isMobile(request);
		
		//手机端
		if(isMobile) {
			HttpUtil.outputJson(ResponesData.success("login"), response);
			return;
		}
		
		//电脑端
		response.sendRedirect(configs.pathHtml);
	}
	
	
	
	/**
	 * <p>登出系统(不登出cas)</p>
	 * @param session 登出系统
	 * @author 杨雪令
	 * @time 2019年1月9日上午11:38:25
	 * @version 1.0
	 */
	@RequestMapping(value="/slogout")
	public ResponesData slogout(HttpSession session) {
		session.invalidate();
		return ResponesData.success("success");
	}
}