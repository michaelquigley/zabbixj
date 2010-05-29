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
				String inputLine = input.readLine();
				socket.close();

				if(inputLine.substring(0, 4).equals("ZBXD")) {
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
			}
			
			return values;
			
		} catch(IOException ioe) {
			throw new ZabbixException(ioe);
		}
	}
	
	private InetAddress agentAddress;
	private int port;
}