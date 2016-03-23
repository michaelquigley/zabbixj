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

package com.quigley.zabbixj.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.quigley.zabbixj.ZabbixException;

public class PassiveAgentClient {
	public PassiveAgentClient(InetAddress agentAddress, int port) {
		this.agentAddress = agentAddress;
		this.port = port;
	}
	
	public Map<String, Object> getValues(List<String> keys) {
		try {
			Map<String, Object> values = new HashMap<String, Object>();
			for(String key : keys) {
				Socket socket = new Socket(agentAddress, port);
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				OutputStream output = socket.getOutputStream();
				
				byte[] bytes = (key + "\n").getBytes();
				output.write(bytes);
				output.flush();
				String inputLine = input.readLine();
				socket.close();

				if(inputLine != null) {
					if(inputLine.length() >= 4 && inputLine.substring(0, 4).equals("ZBXD")) {
						inputLine = inputLine.substring(13, inputLine.length());
					}
					
					try {
						long inputLong = Long.parseLong(inputLine);
						values.put(key, inputLong);
						
					} catch(Exception e) {
						try {
							float inputFloat = Float.parseFloat(inputLine);
							values.put(key, inputFloat);
							
						} catch(Exception e2) {
							values.put(key, inputLine);
						}
					}
				} else {
					System.out.println("Empty input line.");
				}
			}
			
			return values;
			
		} catch(IOException ioe) {
			throw new ZabbixException(ioe);
		}
	}
	
	private InetAddress agentAddress;
	private int port;
}
