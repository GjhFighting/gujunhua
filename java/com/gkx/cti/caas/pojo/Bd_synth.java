package com.gkx.cti.caas.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Document")
@XmlAccessorType(XmlAccessType.FIELD)
public class Bd_synth {
	private String serverip;
	private String apikey;
	private String secretkey;
	private String apitokenurl;
	private String apiurl;
	private String cuid;
	private String per;
	private String spd;
	private String pit;
	private String vol;
	private String aue;
	
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
	public String getApiurl() {
		return apiurl;
	}
	public void setApiurl(String apiurl) {
		this.apiurl = apiurl;
	}
	public String getCuid() {
		return cuid;
	}
	public void setCuid(String cuid) {
		this.cuid = cuid;
	}
	public String getPer() {
		return per;
	}
	public void setPer(String per) {
		this.per = per;
	}
	public String getSpd() {
		return spd;
	}
	public void setSpd(String spd) {
		this.spd = spd;
	}
	public String getPit() {
		return pit;
	}
	public void setPit(String pit) {
		this.pit = pit;
	}
	public String getVol() {
		return vol;
	}
	public void setVol(String vol) {
		this.vol = vol;
	}
	public String getAue() {
		return aue;
	}
	public void setAue(String aue) {
		this.aue = aue;
	}
	
	
}
