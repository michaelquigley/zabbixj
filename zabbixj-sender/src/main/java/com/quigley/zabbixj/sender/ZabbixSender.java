package com.quigley.zabbixj.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ZabbixSender {
	public ZabbixSender(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public ZabbixSenderResponse send(ZabbixSenderRequest request) throws IOException {
		Socket socket = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(host, port));

			is = socket.getInputStream();
			os = socket.getOutputStream();

			os.write(marshall(request));
			os.flush();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(is, baos);

			return unmarshall(baos.toByteArray());

		} finally {
			if(socket != null) {
				socket.close();
			}
			if(is != null) {
				is.close();
			}
			if(os != null) {
				os.close();
			}
		}
	}

	public byte[] marshall(ZabbixSenderRequest request) throws IOException {
		byte[] json = new ObjectMapper().writeValueAsBytes(request);
		byte[] marshalled = new byte[header.length + 4 + 4 + json.length];
		System.arraycopy(header, 0, marshalled, 0, header.length);
		marshalled[header.length] = (byte) (json.length & 0xFF);
		marshalled[header.length + 1] = (byte) ((json.length >> 8) & 0x00FF);
		marshalled[header.length + 2] = (byte) ((json.length >> 16) & 0x0000FF);
		marshalled[header.length + 3] = (byte) ((json.length >> 24) & 0x000000FF);
		System.arraycopy(json, 0, marshalled, header.length + 4 + 4, json.length);
		return marshalled;
	}

	public ZabbixSenderResponse unmarshall(byte[] marshalled) throws IOException {
		if(marshalled.length < 13) {
			return null;
		}

		String json = new String(marshalled, 13, marshalled.length - 13);
		ZabbixSenderResponse response = new ObjectMapper().readValue(json, ZabbixSenderResponse.class);
		return response;
	}

	private String host;
	private int port;

	private static final byte[] header = { 'Z', 'B', 'X', 'D', '\1' };
}