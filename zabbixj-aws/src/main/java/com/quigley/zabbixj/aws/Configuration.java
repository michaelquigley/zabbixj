package com.quigley.zabbixj.aws;

import java.util.List;

public class Configuration {
	public String getAwsAccessKey() {
		return awsAccessKey;
	}
	public void setAwsAccessKey(String awsAccessKey) {
		this.awsAccessKey = awsAccessKey;
	}

	public String getAwsSecretKey() {
		return awsSecretKey;
	}
	public void setAwsSecretKey(String awsSecretKey) {
		this.awsSecretKey = awsSecretKey;
	}

	public String getAwsRegion() {
		return awsRegion;
	}
	public void setAwsRegion(String awsRegion) {
		this.awsRegion = awsRegion;
	}

	public String getZabbixHost() {
		return zabbixHost;
	}
	public void setZabbixHost(String zabbixHost) {
		this.zabbixHost = zabbixHost;
	}

	public int getZabbixPort() {
		return zabbixPort;
	}

	public void setZabbixPort(int zabbixPort) {
		this.zabbixPort = zabbixPort;
	}

	public List<Mapping> getMappings() {
		return mappings;
	}
	public void setMappings(List<Mapping> mappings) {
		this.mappings = mappings;
	}

	private String awsAccessKey;
	private String awsSecretKey;
	private String awsRegion;

	private String zabbixHost;
	private int zabbixPort;

	private List<Mapping> mappings;
}