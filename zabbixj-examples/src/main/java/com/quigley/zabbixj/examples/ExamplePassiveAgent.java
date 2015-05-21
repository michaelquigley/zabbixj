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

import com.quigley.zabbixj.agent.ZabbixAgent;
import com.quigley.zabbixj.providers.JVMMetricsProvider;

/**
 * A simple passive Zabbix agent implementation.
 * 
 * @author Michael Quigley
 */
public class ExamplePassiveAgent {

	public static void main(String[] args) throws Exception {
		if(args.length < 1) {
			System.out.println("Usage: ExamplePassiveAgent <listenPort>");	
			return;
		}

		int listenPort = Integer.parseInt(args[0]);
		
		ZabbixAgent agent = new ZabbixAgent();
        agent.setListenPort(listenPort);

        agent.addProvider("example", new ExampleMetricsProvider());
		agent.addProvider("java", new JVMMetricsProvider());
        
        agent.start();
        
        while(true) {
        	Thread.sleep(10000);
        }
    }
}
