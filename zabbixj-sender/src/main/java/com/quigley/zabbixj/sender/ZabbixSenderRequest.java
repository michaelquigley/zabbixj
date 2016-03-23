package com.quigley.zabbixj.sender;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZabbixSenderRequest {
	public ZabbixSenderRequest() {
		request = "sender data";
	}

	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}

	public List<ZabbixSenderDataItem> getData() {
		return data;
	}
	public void setData(List<ZabbixSenderDataItem> data) {
		this.data = data;
	}
	public void addData(ZabbixSenderDataItem item) {
		if(data == null) {
			data = new ArrayList<ZabbixSenderDataItem>();
		}
		data.add(item);
	}

	public Long getClock() {
		return clock;
	}
	public void setClock(Long clock) {
		this.clock = clock;
	}

	private String request;
	private List<ZabbixSenderDataItem> data;
	private Long clock;
}