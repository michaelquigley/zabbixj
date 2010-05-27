/*
 * Zabbix/J - A Java agent for the Zabbix monitoring system.
 * Copyright (C) 2006 Michael F. Quigley Jr.
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

package com.quigley.zabbixj.agent;

import com.quigley.zabbixj.metrics.MetricsContainer;
import com.quigley.zabbixj.metrics.MetricsException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The WorkerThread is responsible for operating on the client socket, accepting
 * a metric key and returning the corresponding value.
 * 
 * @author Michael Quigley
 */
public class WorkerThread extends Thread {

    /**
     * Construct a new WorkerThread instance.
     * @param container a reference to the MetricsContainer to process against.
     * @param socket an established TCP socket to process requests from.
     */
    public WorkerThread(MetricsContainer container, Socket socket) {
        this.container = container;
        this.socket = socket;
    }

    /**
     * WorkerThread execution begins.
     */
    public void run() {
        // For log messages.
        String client = socket.getInetAddress().getHostAddress();

        if(log.isDebugEnabled()) {
            log.info("Accepted Connection From: " + client);
        }

        PrintWriter out = null;
        BufferedReader in = null;

        try {
            // Set timeout to 1 second.
            socket.setSoTimeout(1000);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputLine = in.readLine();
            
            // Fix for zabbix_get.
            if(inputLine.substring(0, 4).equals("ZBXD")) {
            	inputLine = inputLine.substring(13, inputLine.length());
            }
            
            if(inputLine != null) {
                try {
                    Object value = container.getMetric(inputLine);

                    out.print(value.toString());
                    out.flush();

                } catch(MetricsException me) {
                    if(log.isErrorEnabled()) {
                        log.error("Client: " + client + " Sent Unknown Key: " + inputLine);
                    }

                    out.print("ZBX_NOTSUPPORTED");
                    out.flush();
                }
            }

        } catch(SocketTimeoutException ste) {
            log.debug(client + ": Timeout Detected.");

        } catch(Exception e) {
            log.error(client + ": Error: " + e.toString());

        } finally {
            if(log.isDebugEnabled()) {
                log.debug(client + ": Disconnected.");
            }

            try { if(in != null) { in.close(); } } catch(Exception e) { }
            try { if(out != null) { out.close(); } } catch(Exception e) { }
            try { socket.close(); } catch(Exception e) { }
        }
    }

    private MetricsContainer container;
    private Socket socket;

    private static Logger log = LoggerFactory.getLogger(WorkerThread.class);
}
