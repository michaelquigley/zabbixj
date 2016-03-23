package com.quigley.zabbixj.aws.config;

public class Source {
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getDimensionName() {
		return dimensionName;
	}
	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}

	public String getDimensionValue() {
		return dimensionValue;
	}
	public void setDimensionValue(String dimensionValue) {
		this.dimensionValue = dimensionValue;
	}

	public String getMetricName() {
		return metricName;
	}
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public String getStatistic() {
		return statistic;
	}
	public void setStatistic(String statistic) {
		this.statistic = statistic;
	}

	private String namespace;
	private String dimensionName;
	private String dimensionValue;
	private String metricName;
	private String statistic;
}
