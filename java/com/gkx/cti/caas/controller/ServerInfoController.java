package com.gkx.cti.caas.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gkx.cti.caas.pojo.Server;
import com.gkx.cti.caas.service.ServerInfoServer;
import com.gkx.cti.caas.util.SSHUtil;
import com.ql.util.api.ResponesData;

@RestController
@RequestMapping("/serverinfo")
public class ServerInfoController {
	@Resource
	private ServerInfoServer serverInfoServer;
	
	@SuppressWarnings("static-access")
	@RequestMapping("/ping")
	public ResponesData ping(@RequestParam("serverip") String serverip) {
		SSHUtil ssh = new SSHUtil();
		String pingString = "ping "+serverip;
		String result = ssh.run("125.91.33.50", "root", "124!@$qweQWE", pingString);
		if(result.indexOf("ttl")>0) {
			return ResponesData.success("success");		
		}
		return ResponesData.success("false");
	}
	
	@RequestMapping("/putinfodb")
	public ResponesData putInfoDB(HttpServletRequest req, @RequestBody(required = false) Server server) {		
		serverInfoServer.putinfoDB(server);
		return ResponesData.success("success!!");

	}
	
	
	@RequestMapping("/getserverbyname")
	public Server getserverbyname(@RequestParam("name") String name) {
		Server server = serverInfoServer.getserverbyname(name);
		return server;
	}
	
	@RequestMapping("/getserverinfo")
	public List<Server> getServerInfo() {
		System.out.println("server");
		List<Server> list = serverInfoServer.getServerInfo(); 
		return list;
	}
	
	
	@RequestMapping("/restart_plugin")
	public String replace_plugin(@RequestParam("serverip") String serverip) {
		SSHUtil ssh = new SSHUtil();
		String replacestring = "cd /root/sh_wwz/ && ./restart_unimrcp.sh '"+serverip+"'";
		System.out.println(replacestring);
		String result = ssh.run("125.91.33.50", "root", "124!@$qweQWE", replacestring);
		return null;
	}
}
