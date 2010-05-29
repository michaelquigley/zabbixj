/*
 * Zabbix/J - A Java agent for the Zabbix monitoring system.
 * Copyright (C) 2006-2010 Michael F. Quigley Jr.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 */

package com.quigley.zabbixj.example;

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
			System.out.println("Usage: exampleActiveAgent <hostName> <serverAddress> <serverPort>");	
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