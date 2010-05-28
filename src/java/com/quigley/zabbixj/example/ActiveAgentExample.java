package com.quigley.zabbixj.example;

import java.net.InetAddress;

import com.quigley.zabbixj.agent.ZabbixAgent;
import com.quigley.zabbixj.providers.JVMMetricsProvider;

public class ActiveAgentExample {
	public static void main(String[] args) throws Exception {
		ZabbixAgent agent = new ZabbixAgent();
		agent.setEnableActive(true);
		agent.setEnablePassive(false);
		agent.setHostName("ZabbixJ");
		agent.setServerAddress(InetAddress.getByName("192.168.9.33"));
		agent.setServerPort(10051);
		
        agent.addProvider("example", new ExampleMetricsProvider());
		agent.addProvider("java", new JVMMetricsProvider());

		agent.start();
		
		while(true) {
			Thread.sleep(10000);
		}
	}
}