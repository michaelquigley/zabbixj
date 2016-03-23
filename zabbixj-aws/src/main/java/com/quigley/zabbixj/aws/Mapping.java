package com.quigley.zabbixj.aws;

public class Mapping {
	public int getPeriodMinutes() {
		return periodMinutes;
	}
	public void setPeriodMinutes(int periodMinutes) {
		this.periodMinutes = periodMinutes;
	}

	public SourceItem getSource() {
		return source;
	}
	public void setSource(SourceItem source) {
		this.source = source;
	}

	public DestinationItem getDestination() {
		return destination;
	}
	public void setDestination(DestinationItem destination) {
		this.destination = destination;
	}

	private int periodMinutes;
	private SourceItem source;
	private DestinationItem destination;
}
