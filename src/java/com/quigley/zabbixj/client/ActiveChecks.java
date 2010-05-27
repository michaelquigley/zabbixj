package com.quigley.zabbixj.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

public class ActiveChecks {
	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("192.168.9.33", 10051);
		InputStream input = socket.getInputStream();
		OutputStream output = socket.getOutputStream();
	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		output.write(getRequest());
		output.flush();

		byte[] buffer = new byte[10240];
		int read = 0;
		while((read = input.read(buffer, 0, 10240)) != -1) {
			baos.write(buffer, 0, read);
		}
		
		socket.close();
		
		JSONObject response = getResponse(baos.toByteArray());

		System.out.println("Response: " + response.getString("response"));
		if(response.getString("response").equals("success")) {
			JSONArray keyArray = response.getJSONArray("data");
			for(int i = 0; i < keyArray.length(); i++) {
				JSONObject key = keyArray.getJSONObject(i);
				System.out.println("Key: " + key.getString("key") + ", Delay: " + key.getLong("delay") + ", Lastlogsize: " + key.getLong("lastlogsize") + ", mtime: " + 
						key.getLong("mtime"));
			}
		}
	}
	
	private static byte[] getRequest() throws Exception {
		JSONObject request = new JSONObject();
		request.put("request", "active checks");
		request.put("host", "UbuntuVM");
		
		byte[] requestBytes = request.toString().getBytes();

		String header = "ZBXD\1";
		byte[] headerBytes = header.getBytes();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeLong(request.length());
		dos.flush();
		dos.close();
		bos.close();
		byte[] requestLengthBytes = bos.toByteArray();

		byte[] allBytes = new byte[headerBytes.length + requestLengthBytes.length + requestBytes.length];

		int index = 0;
		for(int i = 0; i < headerBytes.length; i++) {
			allBytes[index++] = headerBytes[i];
		}
		for(int i = 0; i < requestLengthBytes.length; i++) {
			allBytes[index++] = requestLengthBytes[7 - i]; // Reverse the byte order.
		}
		for(int i = 0; i < requestBytes.length; i++) {
			allBytes[index++] = requestBytes[i];
		}
		
		return allBytes;
	}
	
	private static JSONObject getResponse(byte[] responseBytes) throws Exception {
		byte[] sizeBuffer = new byte[8];
		int index = 0;
		for(int i = 12; i > 4; i--) {
			sizeBuffer[index++] = responseBytes[i];
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(sizeBuffer);
		DataInputStream dis = new DataInputStream(bais);
		long size = dis.readLong();
		dis.close();
		bais.close();
	
		byte[] jsonBuffer = new byte[responseBytes.length - 13];
		if(jsonBuffer.length != size) {
			throw new ZabbixException("Reported and actual buffer sizes differ!");
		}
		
		index = 0;
		for(int i = 13; i < responseBytes.length; i++) {
			jsonBuffer[index++] = responseBytes[i];
		}
		
		JSONObject response = new JSONObject(new String(jsonBuffer));
		
		return response;
	}
}