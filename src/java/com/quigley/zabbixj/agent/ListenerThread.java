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

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ListenerThread creates a listening socket, accepts connections on that socket
 * and dispatches them to a WorkerThread for processing.
 * 
 * @author Michael Quigley
 */
public class ListenerThread extends Thread {

    /**
     * Construct a new ListenerThread instance.
     * @param container a reference to the MetricsContainer this listener services.
     * @param port the TCP port number to listen on.
     * @throws Exception when a problem occurs creating the ListenerThread.
     */
    public ListenerThread(MetricsContainer container, int port) throws Exception {
        running = true;
        this.container = container;

        serverSocket = new ServerSocket(port, 5);
        serverSocket.setSoTimeout(1000);
    }

    /**
     * Construct a new ListenerThread instance.
     * @param container a reference to the MetricsContainer this listener services.
     * @param address the IP address to bind to.
     * @param port the TCP port number to listen on.
     * @throws Exception when a problem occurs creating the ListenerThread.
     */
    public ListenerThread(MetricsContainer container, InetAddress address, int port) throws Exception {
        running = true;
        this.container = container;

        serverSocket = new ServerSocket(port, 5, address);
        serverSocket.setSoTimeout(1000);
    }

    /**
     * ListenerThread execution begins.
     */
    public void run() {
        if(log.isDebugEnabled()) {
            log.debug("ListenerThread Starting.");
        }

        while(running) {
            try {
                Socket clientSocket = serverSocket.accept();

                WorkerThread worker = new WorkerThread(container, clientSocket);
                worker.start();

            } catch(SocketTimeoutException ste) {
                // Ignore. We receive one of these every second, when the accept()
                // method times out. This is done to give us the opportunity to break
                // out of the blocking accept() call in order to shut down cleanly.

            } catch(Exception e) {
                if(log.isErrorEnabled()) {
                    log.error("Error Accepting: " + e.toString());
                }
            }
        }
    }

    /**
     * Schedule a shutdown of the listener. The shutdown will occur sometime between
     * 0 and 1 seconds after the shutdown method was invoked.
     */
    public void shutdown() {
        running = false;
    }

    private boolean running;

    private MetricsContainer container;
    private ServerSocket serverSocket;

    private static Logger log = LoggerFactory.getLogger(ListenerThread.class);
}