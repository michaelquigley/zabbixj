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
			System.out.println("Usage: ExamplePassiveAgentClient <address> <port> [-d<sec>] <key> ... <keyN>");
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