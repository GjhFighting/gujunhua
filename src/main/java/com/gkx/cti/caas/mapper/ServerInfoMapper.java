package com.gkx.cti.caas.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gkx.cti.caas.pojo.Server;

public interface ServerInfoMapper {
	
	public Server getserverbyname(String name);
	
	public List<Server> getServerInfo();

	public void putinfoDB(Server server);

	public String get_plugin_which(@Param("serverip") String serverip);

	public Server getServerBySeverIp(@Param("serverip") String serverip);
}
