package com.quigley.zabbixj.api;

import java.util.HashMap;
import java.util.Map;

public class ParameterMapRequest extends Request {
	public ParameterMapRequest(String method) {
		super(method);
		params = new HashMap<String, Object>();
	}

	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	public void addParam(String key, Object value) {
		params.put(key, value);
	}

	private Map<String, Object> params;
}