package com.quigley.zabbixj.api;

import java.util.ArrayList;
import java.util.List;

public class ParameterArrayRequest extends Request {
	public ParameterArrayRequest(String method) {
		super(method);
		params = new ArrayList<Object>();
	}

	public List<Object> getParams() {
		return params;
	}
	public void setParams(List<Object> params) {
		this.params = params;
	}
	public void addParam(Object param) {
		params.add(param);
	}

	private List<Object> params;
}