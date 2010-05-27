package com.quigley.zabbixj.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ActiveChecks {
	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("192.168.9.33", 10051);
		InputStream input = socket.getInputStream();
		OutputStream output = socket.getOutputStream();
	
		FileOutputStream fos = new FileOutputStream("request.bin");
		fos.write(getRequest());
		fos.close();
		
		output.write(getRequest());

		byte[] buffer = new byte[1024];
		int read = 0;
		while((read = input.read(buffer, 0, 1024)) != -1) {
			System.out.write(buffer, 0, read);
		}
		
		socket.close();
	}
	
	private static byte[] getRequest() throws Exception {
		String request = "{\n" +
		 "	\"request\":\"active checks\",\n" +
		 "	\"host\":\"Eleven\"\n" +
		 "}\n";
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
		System.out.println("requestLengthBytes: " + requestLengthBytes.length);

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