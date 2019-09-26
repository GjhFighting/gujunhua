package com.gkx.cti.caas.pojo;

public class Logger {
	private String serverip;
	private String output;
	private String headers;
	private String priority;
	private String masking;
	
	public String getServerip() {
		return serverip;
	}
	public void setServerip(String serverip) {
		this.serverip = serverip;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getHeaders() {
		return headers;
	}
	public void setHeaders(String headers) {
		this.headers = headers;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getMasking() {
		return masking;
	}
	public void setMasking(String masking) {
		this.masking = masking;
	}
	
}
