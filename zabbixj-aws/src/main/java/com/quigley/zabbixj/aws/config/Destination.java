package com.quigley.zabbixj.aws.config;

public class Destination {
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	private String host;
	private String key;
}