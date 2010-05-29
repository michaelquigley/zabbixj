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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.quigley.zabbixj.client.PassiveAgentClient;

/**
 * An example using <code>PassiveAgentClient</code> to retrieve values from a passive
 * Zabbix agent. This is very similar to the Zabbix-provided <code>zabbix_get</code>
 * utility. We include the ability to query multiple keys, repeatedly, with a
 * configurable polling delay.
 * 
 * @author Michael Quigley
 */
public class ExamplePassiveAgentClient {
	public static void main(String[] args) throws Exception {
		if(args.length < 3) {
			System.out.println("Usage: examplePassiveAgentClient <address> <port> [-d<sec>] <key> ... <keyN>");
			System.exit(1);
		}
		
		InetAddress agentAddress = InetAddress.getByName(args[0]);
		int port = Integer.parseInt(args[1]);

		int delay = 0;
		int firstKeyIndex = 2;
		if(args[2].startsWith("-d")) {
			firstKeyIndex++;
			String delayString = args[2].substring(2, args[2].length());
			delay = Integer.parseInt(delayString);
			System.out.println("Retrieving information every " + delay + " seconds.");
		}
		
		PassiveAgentClient zabbixClient = new PassiveAgentClient(agentAddress, port);
		
		List<String> keys = new ArrayList<String>();
		for(int i = firstKeyIndex; i < args.length; i++) {
			keys.add(args[i]);
		}
		
		if(delay > 0) {
			Map<String, Object> lastValues = null;
			while(true) {
				Map<String, Object> values = zabbixClient.getValues(keys);
				System.out.println("As of " + new Date() + ":");
				displayValues(lastValues, values);
				System.out.println("\n");
				lastValues = values;
				
				try { Thread.sleep(delay * 1000); } catch(InterruptedException ie) { }
			}
		} else {
			Map<String, Object> values = zabbixClient.getValues(keys);
			displayValues(null, values);
		}
	}
	
	private static void displayValues(Map<String, Object> oldValues, Map<String, Object> values) {
		for(String key : values.keySet()) {
			Object value = values.get(key);
			Object oldValue = null;
			if(oldValues != null) {
				oldValue = oldValues.get(key);
			}
			
			if(value != null) {
				String delta = null;
				if(oldValue != null) {
					if(oldValue instanceof Float && value instanceof Float) {
						Float oldValueF = (Float) oldValue;
						Float valueF = (Float) value;
						delta = "" + (valueF - oldValueF);
					} else
					if(oldValue instanceof Long && value instanceof Long) {
						Long oldValueL = (Long) oldValue;
						Long valueL = (Long) value;
						delta = "" + (valueL - oldValueL);
					}
				}
				
				if(delta == null) {
					System.out.println("{" + key + "}\t>>\t" + value + "\t{" + value.getClass().getName() + "}");
					
				} else {
					System.out.println("{" + key + "}\t>>\t" + value + " <" + delta + ">\t{" + value.getClass().getName() + "}");
				}
				
			} else {
				System.out.println("{" + key + "}\t>>\t{NULL}");
			}
		}
	}
}