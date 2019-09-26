package com.gkx.cti.caas.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Document")
@XmlAccessorType(XmlAccessType.FIELD)
public class Bd_recog {
	private String serverip;
	private String apikey;
	private String secretkey;
	private String apitokenurl;
	private String apiasrurl;
	private String rate;
	private String format;
	private String dev_pid;
	private String cuid;
	
	public String getServerip() {
		return serverip;
	}
	public void setServerip(String serverip) {
		this.serverip = serverip;
	}
	public String getApikey() {
		return apikey;
	}
	public void setApikey(String apikey) {
		this.apikey = apikey;
	}
	public String getSecretkey() {
		return secretkey;
	}
	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}
	public String getApitokenurl() {
		return apitokenurl;
	}
	public void setApitokenurl(String apitokenurl) {
		this.apitokenurl = apitokenurl;
	}
	public String getApiasrurl() {
		return apiasrurl;
	}
	public void setApiasrurl(String apiasrurl) {
		this.apiasrurl = apiasrurl;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getDev_pid() {
		return dev_pid;
	}
	public void setDev_pid(String dev_pid) {
		this.dev_pid = dev_pid;
	}
	public String getCuid() {
		return cuid;
	}
	public void setCuid(String cuid) {
		this.cuid = cuid;
	}
	public Bd_recog () {}
	
}
