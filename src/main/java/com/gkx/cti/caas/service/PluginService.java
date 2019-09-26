package com.gkx.cti.caas.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gkx.cti.caas.mapper.PluginMapper;

@Service
public class PluginService {
	@Resource
	PluginMapper pluginmapper;
	

	public Boolean SavePluginService(String serverip,String pluginname,String username, String json) {
		// TODO Auto-generated method stub
		//if result == 1 success \ result == 2 false		
		 int result = pluginmapper.savePluginMap(serverip,pluginname,username,json);
		 System.out.println(result);
		 if (result == 2) {
			 return true;
		 }else {
			 return false;
		 }
	}

	public String getFormValue(String pluginname, String serverip) {
		// TODO Auto-generated method stub
		return pluginmapper.getFormValue(pluginname,serverip);
	}
}
