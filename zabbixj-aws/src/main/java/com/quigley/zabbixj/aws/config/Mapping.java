package com.quigley.zabbixj.aws.config;

public class Mapping {
	public int getPeriodMinutes() {
		return periodMinutes;
	}
	public void setPeriodMinutes(int periodMinutes) {
		this.periodMinutes = periodMinutes;
	}

	public Source getSource() {
		return source;
	}
	public void setSource(Source source) {
		this.source = source;
	}

	public Destination getDestination() {
		return destination;
	}
	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	private int periodMinutes;
	private Source source;
	private Destination destination;
}
