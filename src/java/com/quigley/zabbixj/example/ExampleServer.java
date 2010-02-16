/*
 * $Id: ExampleServer.java 832 2009-02-26 21:59:55Z michael $
 */
/*
 * Zabbix/J - A Java agent for the Zabbix monitoring system.
 * Copyright (C) 2006 Michael F. Quigley Jr.
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

import com.quigley.zabbixj.agent.ZabbixAgent;
import com.quigley.zabbixj.providers.JVMMetricsProvider;

/**
 * A simple ZabbixAgent example.
 * 
 * @author Michael Quigley
 */
public class ExampleServer {

	public static void main(String[] args) throws Exception {
		if(args.length < 1) {
			System.out.println("Usage: com.quigley.zabbixj.example.ExampleServer <port>");	
			return;
		}
		
		// Start the ZabbixAgent.
		agent = new ZabbixAgent();
        agent.setListenPort(Integer.parseInt(args[0]));

        agent.addProvider("example", new ExampleMetricsProvider());
		agent.addProvider("java", new JVMMetricsProvider());
        
        agent.start();

        // Your meaningful business logic would go here.
        try { while(true) { Thread.sleep(100000); } } catch(Exception e) { }

        // Stop the ZabbixAgent.
        agent.stop();
    }
	
	private static ZabbixAgent agent;
}
