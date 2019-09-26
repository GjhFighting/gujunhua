package com.gkx.cti.caas.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gkx.cti.caas.mapper.ServerInfoMapper;
import com.gkx.cti.caas.pojo.Server;

@Service
public class ServerInfoServer {

	@Resource
	ServerInfoMapper serverInfoMapper;
	
	public void putinfoDB(Server server) {
		serverInfoMapper.putinfoDB(server);
	}
	
	public Server getserverbyname(String name) {
		return serverInfoMapper.getserverbyname(name);
	}
	
	public Server getServerBySeverIp(String serverip) {
		return serverInfoMapper.getServerBySeverIp(serverip);
	}
	
	public List<Server> getServerInfo() {
		return serverInfoMapper.getServerInfo();
	}

	public String get_plugin_which(String serverip) {
		// TODO Auto-generated method stub
		return serverInfoMapper.get_plugin_which(serverip);
	}

}
