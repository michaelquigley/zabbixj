/*
 * Copyright 2015 Michael Quigley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.quigley.zabbixj.examples;

import java.net.InetAddress;

import com.quigley.zabbixj.agent.ZabbixAgent;
import com.quigley.zabbixj.providers.JVMMetricsProvider;

/**
 * A simple active Zabbix agent implementation.
 * 
 * @author Michael Quigley
 */
public class ExampleActiveAgent {
	public static void main(String[] args) throws Exception {
		if(args.length < 3) {
			System.out.println("Usage: ExampleActiveAgent <hostName> <serverAddress> <serverPort>");	
			return;
		}
		
		String hostName = args[0];
		String serverAddress = args[1];
		int serverPort = Integer.parseInt(args[2]);
		
		ZabbixAgent agent = new ZabbixAgent();
		agent.setEnableActive(true);
		agent.setEnablePassive(false);
		agent.setHostName(hostName);
		agent.setServerAddress(InetAddress.getByName(serverAddress));
		agent.setServerPort(serverPort);
		
        agent.addProvider("example", new ExampleMetricsProvider());
		agent.addProvider("java", new JVMMetricsProvider());

		agent.start();
		
		while(true) {
			Thread.sleep(10000);
		}
	}
}