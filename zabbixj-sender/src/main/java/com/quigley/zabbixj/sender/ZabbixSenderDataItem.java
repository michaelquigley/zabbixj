package com.quigley.zabbixj.sender;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZabbixSenderDataItem {
	public ZabbixSenderDataItem() {
	}

	public ZabbixSenderDataItem(String host, String key, String value) {
		this.host = host;
		this.key = key;
		this.value = value;
	}

	public ZabbixSenderDataItem(String host, String key, String value, Long clock) {
		this.host = host;
		this.key = key;
		this.value = value;
		this.clock = clock;
	}

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

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public Long getClock() {
		return clock;
	}
	public void setClock(Long clock) {
		this.clock = clock;
	}

	private String host;
	private String key;
	private String value;
	private Long clock;
}