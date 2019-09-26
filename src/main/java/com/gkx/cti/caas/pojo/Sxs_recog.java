package com.gkx.cti.caas.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

	@XmlRootElement(name="Document")
	@XmlAccessorType(XmlAccessType.FIELD)
public class Sxs_recog {
	private String serverip;
	private String ip;
	private String port;
	private String did;
	private String samplerate;
	
	public String getServerip() {
		return serverip;
	}
	public void setServerip(String serverip) {
		this.serverip = serverip;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDid() {
		return did;
	}
	public void setDid(String did) {
		this.did = did;
	}
	public String getSamplerate() {
		return samplerate;
	}
	public void setSamplerate(String samplerate) {
		this.samplerate = samplerate;
	}

	
}
