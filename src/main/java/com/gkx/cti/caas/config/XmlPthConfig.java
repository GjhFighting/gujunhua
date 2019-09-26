package com.gkx.cti.caas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cass.xml")
public class XmlPthConfig {
	
	private String path;

}
