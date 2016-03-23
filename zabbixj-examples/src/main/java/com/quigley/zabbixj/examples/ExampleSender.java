package com.quigley.zabbixj.examples;

import com.quigley.zabbixj.sender.ZabbixSender;
import com.quigley.zabbixj.sender.ZabbixSenderRequest;
import com.quigley.zabbixj.sender.ZabbixSenderDataItem;
import com.quigley.zabbixj.sender.ZabbixSenderResponse;

public class ExampleSender {
	public static void main(String[] args) throws Exception {
		if(args.length < 5) {
			System.out.println("Usage: ExampleSender <serverName> <serverPort> <hostName> <key> <value>");
			return;
		}

		String serverName = args[0];
		int serverPort = Integer.parseInt(args[1]);
		String hostName = args[2];
		String key = args[3];
		String value = args[4];

		ZabbixSender zabbixSender = new ZabbixSender(serverName, serverPort);

		ZabbixSenderRequest request = new ZabbixSenderRequest();
		request.addData(new ZabbixSenderDataItem(hostName, key, value));
		ZabbixSenderResponse senderResult = zabbixSender.send(request);

		if(senderResult != null) {
			System.out.println(senderResult.toString());

		} else {
			System.out.println("Null response from zabbix server.");
		}
	}
}