package com.gkx.cti.caas.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.gkx.cti.caas.feign.UserClient;

/**
 * <p>用户服务类</p>
 * @author 杨雪令
 * @time 2018年10月22日上午11:13:09
 * @version 1.0
 */
@Component
public class CustomUserDetailsService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	
	@Autowired
	private UserClient userClient;
	
 
	@Override
	public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
		
		logger.info("用户授权：" + token.getName());
		
		//查询用户URL
		Set<String> urlList = userClient.findAuthUrl(token.getName(), "jz");
		logger.info("用户urlList：" + urlList.size());
        
        //构造权限集合
		List<GrantedAuthority> authorities = new ArrayList<>();
		for(String url : urlList) {
			GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(url);
			authorities.add(grantedAuthority);
		}
		
		
		//公共权限
		authorities.add(new SimpleGrantedAuthority("/"));
		authorities.add(new SimpleGrantedAuthority("/favicon.ico"));
		authorities.add(new SimpleGrantedAuthority("/slogout"));
		authorities.add(new SimpleGrantedAuthority("/index"));
		authorities.add(new SimpleGrantedAuthority("/getLoginUserInfo"));
		
		
		//返回用户信息
		UserDetails userDetails = new User(token.getName(), "", true, true, true, true, authorities);
		return userDetails;
	}
 
}