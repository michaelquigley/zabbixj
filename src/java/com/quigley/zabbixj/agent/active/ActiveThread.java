package com.quigley.zabbixj.agent.active;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quigley.zabbixj.client.ZabbixException;
import com.quigley.zabbixj.metrics.MetricsContainer;

public class ActiveThread extends Thread {
	public ActiveThread(MetricsContainer metricsContainer, String hostName, InetAddress serverAddress, int serverPort, int refreshInterval) {
		running = true;
		checks = new HashMap<Integer, List<String>>();
		lastChecked = new HashMap<Integer, Long>();
		
		this.metricsContainer = metricsContainer;
		this.hostName = hostName;
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.refreshInterval = refreshInterval;
	}
	
	public void run() {
        if(log.isDebugEnabled()) {
            log.debug("ActiveThread Starting.");
        }

        try {
        	refresh();
        	
        } catch(Exception e) {
        	log.error("Unable to do get initial list of checks.", e);
        }
        
        while(running) {
        	try {
        		Thread.sleep(1000);
        	} catch(InterruptedException ie) {
        		//
        	}
        	
        	long clock = System.currentTimeMillis() / 1000;
        	if((clock - lastRefresh) >= refreshInterval) {
        		try {
        			refresh();
        			
        		} catch(Exception e) {
        			log.error("Unable to refresh.", e);
        		}
        	}
        	
        	for(int delay : checks.keySet()) {
        		long delayLastChecked = lastChecked.get(delay);
        		if(clock - delayLastChecked >= delay) {
        			try {
        				sendMetrics(delay, checks.get(delay));
        				
        			} catch(Exception e) {
        				log.error("Unable to send metrics.", e);
        			}
        		}
        	}
        }
	}
	
	private void sendMetrics(int delay, List<String> keyList) throws Exception {
		long clock = System.currentTimeMillis() / 1000;
		
		JSONObject metrics = new JSONObject();
		metrics.put("request", "agent data");

		JSONArray data = new JSONArray();
		for(String keyName : keyList) {
			JSONObject key = new JSONObject();
			key.put("host", hostName);
			key.put("key", keyName);
			try {
				Object value = metricsContainer.getMetric(keyName);
				key.put("value", value.toString());
				
			} catch(Exception e) {
				key.put("value", "ZBX_NOTSUPPORTED");
			}
			key.put("clock", "" + clock);
			
			data.put(key);
		}
		metrics.put("data", data);
		metrics.put("clock", "" + clock);
		
		Socket socket = new Socket(serverAddress, serverPort);
		InputStream input = socket.getInputStream();
		OutputStream output = socket.getOutputStream();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		output.write(getRequest(metrics));
		output.flush();

		byte[] buffer = new byte[10240];
		int read = 0;
		while((read = input.read(buffer, 0, 10240)) != -1) {
			baos.write(buffer, 0, read);
		}
		
		socket.close();
		
		JSONObject response = getResponse(baos.toByteArray());
		if(response.getString("response").equals("success")) {
			log.info("Success. Server reports: " + response.getString("info"));
		} else {
			log.error("Failure!");
		}
		
		lastChecked.put(delay, clock);
	}
	
	private void refresh() throws Exception {
		Socket socket = new Socket(serverAddress, serverPort);
		InputStream input = socket.getInputStream();
		OutputStream output = socket.getOutputStream();
	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		JSONObject request = new JSONObject();
		request.put("request", "active checks");
		request.put("host", hostName);

		byte[] buffer = getRequest(request);
		
		output.write(buffer);
		output.flush();

		buffer = new byte[10240];
		int read = 0;
		while((read = input.read(buffer, 0, 10240)) != -1) {
			baos.write(buffer, 0, read);
		}
		
		socket.close();
		
		JSONObject response = getResponse(baos.toByteArray());
		if(response.getString("response").equals("success")) {
			refreshFromResponse(response);
			
		} else {
			log.warn("Server reported a failure when requesting active checks:" + response.getString("info"));
		}
		
		lastRefresh = System.currentTimeMillis() / 1000;
	}
	
	private void refreshFromResponse(JSONObject response) throws JSONException {
		long clock = System.currentTimeMillis() / 1000;
		
		Map<Integer, List<String>> newChecks = new HashMap<Integer, List<String>>();
		
		JSONArray data = response.getJSONArray("data");
		for(int i = 0; i < data.length(); i++) {
			JSONObject check = data.getJSONObject(i);
			String key = check.getString("key");
			int delay = check.getInt("delay");
			
			if(!newChecks.containsKey(delay)) {
				newChecks.put(delay, new ArrayList<String>());
			}
			if(!lastChecked.containsKey(delay)) {
				lastChecked.put(delay, clock);
			}
			List<String> checksForDelay = newChecks.get(delay);
			if(!checksForDelay.contains(key)) {
				checksForDelay.add(key);
				newChecks.put(delay, checksForDelay);
				
				if(log.isDebugEnabled()) {
					log.debug("Added '" + key + "' on " + delay + " second delay cycle.");
				}
			}
		}
		
		List<Integer> lastCheckedToRemove = new ArrayList<Integer>();
		for(int delay : lastChecked.keySet()) {
			if(!newChecks.containsKey(delay)) {
				lastCheckedToRemove.add(delay);
			}
		}
		for(int delay : lastCheckedToRemove) {
			log.debug("Pruning lastChecked delay '" + delay + "'");
			lastChecked.remove(delay);
		}
		
		checks = newChecks;
	}
	
	private byte[] getRequest(JSONObject jsonObject) throws Exception {
		byte[] requestBytes = jsonObject.toString().getBytes();

		String header = "ZBXD\1";
		byte[] headerBytes = header.getBytes();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeLong(requestBytes.length);
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

	private JSONObject getResponse(byte[] responseBytes) throws Exception {
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
	
	public void shutdown() {
		running = false;
	}
	
	private boolean running;
	private Map<Integer, List<String>> checks;
	private Map<Integer, Long> lastChecked;
	private long lastRefresh;
	
	private MetricsContainer metricsContainer;
	private String hostName;
	private InetAddress serverAddress;
	private int serverPort;
	private int refreshInterval;
	
	private static Logger log = LoggerFactory.getLogger(ActiveThread.class);
}