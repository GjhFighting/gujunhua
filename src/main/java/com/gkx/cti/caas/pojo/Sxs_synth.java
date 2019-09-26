package com.gkx.cti.caas.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Document")
@XmlAccessorType(XmlAccessType.FIELD)
public class Sxs_synth {
	private String serverip;
	private String filename;
	private String host;
	private String port;
	private String username = "dianxin";
	private String localvoice = "\\0";
	private String samplerate = "1";
	
	public String getServerip() {
		return serverip;
	}
	public void setServerip(String serverip) {
		this.serverip = serverip;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getLocalvoice() {
		return localvoice;
	}
	public void setLocalvoice(String localvoice) {
		this.localvoice = localvoice;
	}
	public String getSamplerate() {
		return samplerate;
	}
	public void setSamplerate(String samplerate) {
		this.samplerate = samplerate;
	}
	
}
