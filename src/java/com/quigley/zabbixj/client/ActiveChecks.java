package com.quigley.zabbixj.client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ActiveChecks {
	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("eleven", 10051);
		InputStream input = socket.getInputStream();
		OutputStream output = socket.getOutputStream();
	
		FileOutputStream fos = new FileOutputStream("request.bin");
		fos.write(getRequest());
		fos.close();
		
		output.write(getRequest());
		output.flush();

		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String line;
		while((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		
		socket.close();
	}
	
	private static byte[] getRequest() throws Exception {
		String request = "{\r\n" +
		 "	\"request\":\"active checks\",\r\n" +
		 "	\"host\":\"Eleven\"\r\n" +
		 "}";
		byte[] requestBytes = request.getBytes();

		String header = "ZBXD ";
		byte[] headerBytes = header.getBytes();
		headerBytes[4] = 1;
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeLong(request.length());
		dos.flush();
		dos.close();
		bos.close();
		byte[] requestLengthBytes = bos.toByteArray();

		byte[] allBytes = new byte[headerBytes.length + requestLengthBytes.length + request.getBytes().length];

		int index = 0;
		for(int i = 0; i < headerBytes.length; i++) {
			allBytes[index++] = headerBytes[i];
		}
		for(int i = 0; i < requestLengthBytes.length; i++) {
			allBytes[index++] = requestLengthBytes[i];
		}
		for(int i = 0; i < requestBytes.length; i++) {
			allBytes[index++] = requestBytes[i];
		}
		
		return allBytes;
	}
}