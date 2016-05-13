package com.quigley.zabbixj.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

public class ZabbixApi {
	public ZabbixApi(String url) throws Exception {
		uri = new URI(url.trim());
		httpClient = HttpClients.createDefault();
	}

	public boolean login(String user, String password) throws IOException {
		ParameterMapRequest request = new ParameterMapRequest("user.login");
		request.addParam("user", user);
		request.addParam("password", password);
		JSONObject response = call(request);
		String newAuth = response.getString("result");
		if(newAuth != null && !newAuth.isEmpty()) {
			auth = newAuth;
			return true;
		} else {
			return false;
		}
	}

	public JSONObject call(Request request) throws IOException {
		if(request.getAuth() == null) {
			request.setAuth(auth);
		}
		String outJson = JSON.toJSONString(request);
		log.debug("outJson = [ " + outJson + " ]");
		HttpUriRequest httpUriRequest = RequestBuilder.post().setUri(uri).addHeader("Content-Type", "application/json")
				.setEntity(new StringEntity(outJson))
				.build();
		CloseableHttpResponse response = httpClient.execute(httpUriRequest);
		HttpEntity entity = response.getEntity();
		String inJson = EntityUtils.toString(entity);
		log.debug("inJson = [ " + inJson + " ]");
		return (JSONObject) JSON.parse(inJson);
	}

	private CloseableHttpClient httpClient;
	private URI uri;
	private String auth;

	private static final Logger log = LoggerFactory.getLogger(ZabbixApi.class);
}