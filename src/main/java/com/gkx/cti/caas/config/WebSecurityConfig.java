package com.gkx.cti.caas.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.ql.util.spring.security.access.AccessSecurityFilter;
import com.ql.util.spring.security.cas.CasAuthenticationEntryPoint;
import com.ql.util.spring.security.csrf.CsrfResponseFilter;
import com.ql.util.spring.security.handler.JsonAccessDeniedHandler;
import com.gkx.cti.caas.security.CustomUserDetailsService;
import com.gkx.cti.caas.security.handler.AuthenticationSuccessHandler;


/**
 * <p>Spring Security 配置</p>
 * @author 杨雪令
 * @time 2018年10月19日下午4:25:44
 * @version 1.0
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final Log logger = LogFactory.getLog(WebSecurityConfigurerAdapter.class);

	//系统配置
	@Autowired
	private Configs configs;
	
	
	//用户服务类
	@Autowired
	private CustomUserDetailsService userDetailsService;
	
	//认证成功处理器
	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	
	
	
	
	/**
	 * <p>认证管理器</p>
	 * @author 杨雪令
	 * @time 2018年10月19日下午3:19:50
	 * @version 1.0
	 */
	@Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

	
	
	
	
	/**
	 * <p>认证用户信息来源</p>
	 * @author 杨雪令
	 * @time 2018年10月19日下午3:19:50
	 * @version 1.0
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		ServiceProperties serviceProperties = new ServiceProperties();
		serviceProperties.setService(configs.pathServer + "/login");
		serviceProperties.setSendRenew(true);
		serviceProperties.setAuthenticateAllArtifacts(true);
		
		CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
		casAuthenticationProvider.setServiceProperties(serviceProperties);
		casAuthenticationProvider.setKey("casAuthenticationProviderKey");
		casAuthenticationProvider.setAuthenticationUserDetailsService(userDetailsService);
		casAuthenticationProvider.setTicketValidator(new Cas30ServiceTicketValidator(configs.pathCas));
		
		auth.authenticationProvider(casAuthenticationProvider);
	}
	
	



	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		logger.info("Using >> WebSecurityConfig...");
		
		//访问规则配置
		http.authorizeRequests()
		  .antMatchers("/public/**").permitAll()
		  //.antMatchers("/cti-caas/index").permitAll()
	      .anyRequest().authenticated()
	      .and() 
	      .logout().permitAll()
	      .and()
	      .formLogin().permitAll();
		
		
		//csrf
		http.csrf().ignoringAntMatchers("/login").csrfTokenRepository(new HttpSessionCsrfTokenRepository());
		http.addFilterAfter(new CsrfResponseFilter(), CsrfFilter.class);
		
		
		
		
		//认证入口
		//游客访问控制
		//重定向到cas认证
		CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint(configs.pathCasLogin, configs.pathServer + "/login");
	    http.exceptionHandling().authenticationEntryPoint(casAuthenticationEntryPoint);
	    
	    
	    
	    //访问拒绝处理器
	    http.exceptionHandling().accessDeniedHandler(new JsonAccessDeniedHandler());
	    
	    
	    
	    //用户认证过滤器
	    CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
		casAuthenticationFilter.setAuthenticationManager(authenticationManager());
		casAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
		casAuthenticationFilter.setFilterProcessesUrl("/login");
		http.addFilter(casAuthenticationFilter);
		
		
		//资源访问控制过滤器
		AccessSecurityFilter accessSecurityFilter = new AccessSecurityFilter(authenticationManager());
		http.addFilterBefore(accessSecurityFilter, FilterSecurityInterceptor.class);
		
		
		//登出过滤器
		LogoutFilter logoutFilter = new LogoutFilter(configs.pathCasLogout, new SecurityContextLogoutHandler());
		logoutFilter.setFilterProcessesUrl("/logout");
		http.addFilterBefore(logoutFilter, LogoutFilter.class);
		
		
		//CAS单点登出过滤器
		SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
		singleSignOutFilter.setCasServerUrlPrefix(configs.pathCas);
		singleSignOutFilter.setIgnoreInitConfiguration(true);
	    http.addFilterBefore(singleSignOutFilter, CasAuthenticationFilter.class);
	}

	
}