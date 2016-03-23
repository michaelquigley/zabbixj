package com.quigley.zabbixj.sender;

public class ZabbixSenderResponse {
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}

	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "ZabbixSenderResponse{" +
				"response='" + response + '\'' +
				", info='" + info + '\'' +
				'}';
	}

	private String response;
	private String info;
}
