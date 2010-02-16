/*
 * $Id: ZabbixAgent.java 848 2009-03-17 15:23:55Z michael $
 */
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
import com.quigley.zabbixj.metrics.MetricsProvider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZabbixAgent is the central class of the Zabbix/J implementation. It provides
 * all of the necessary components for exposing metrics from within a Java
 * virtual machine.
 * 
 * @author Michael Quigley
 */
public class ZabbixAgent {

    /**
     * Construct a new ZabbixAgent instance.
     * @throws Exception when a problem occurs creating the agent.
     */
    public ZabbixAgent() throws Exception {
        metricsContainer = new MetricsContainer();
        listenPort = 10050;
        listenAddress = null;
    }

    /**
     * Return the value of property listenAddress.
     * @return the current value of listenAddress. Returns null if the property is unset.
     */
    public String getListenAddress() {
        return listenAddress.getHostAddress();
    }

    /**
     * Set the value of property listenAddress.
     * @param listenAddress a string representation of the interface address the agent should
     *        bind on. This address must match the address of an active interface on the host
     *        system.
     * @throws UnknownHostException when the provided address does not match one of the active
     *         interfaces on the host system.
     */
    public void setListenAddress(String listenAddress) throws UnknownHostException {
        this.listenAddress = InetAddress.getByName(listenAddress);
    }

    /**
     * Return the value of property listenPort.
     * @return the current value of listenPort. Defaults to 10050.
     */
    public int getListenPort() {
        return listenPort;
    }

    /**
     * Set the value of property listenPort.
     * @param listenPort the new value for property listenPort. Must be a valid TCP port number
     *        or the agent will fail when the <code>start()</code> method is invoked.
     */
    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    /**
     * Add a MetricsProvider to the agent.
     * @param name bind the provider to this name.
     * @param provider the provider instance.
     */
    public void addProvider(String name, MetricsProvider provider) {
        metricsContainer.addProvider(name, provider);
    }
    
    public void setProviders(Map<String, MetricsProvider> providers) {
    	metricsContainer.addProviders(providers);
    }

    /**
     * Start processing requests.
     */
    public void start() throws Exception {
        if(log.isInfoEnabled()) {
            log.info("Zabbix Agent Starting");
        }

        // Create and start the listener.
        if(listenAddress == null) {
            listenerThread = new ListenerThread(metricsContainer, listenPort);
        } else {
            listenerThread = new ListenerThread(metricsContainer, listenAddress, listenPort);
        }
        listenerThread.start();

        if(log.isInfoEnabled()) {
            log.info("Zabbix Agent Started");
        }
    }

    /**
     * Stop processing requests.
     */
    public void stop() {
        if(log.isInfoEnabled()) {
            log.info("Zabbix Agent Stopping");
        }

        // Stop and join with the listener.
        listenerThread.shutdown();
        try {
            listenerThread.join();

        } catch(InterruptedException ie) {
            //
        }

        if(log.isInfoEnabled()) {
            log.info("Zabbix Agent Stopped");
        }
    }

    private InetAddress listenAddress;
    private int listenPort;

    private MetricsContainer metricsContainer;
    private ListenerThread listenerThread;

    private static Logger log = LoggerFactory.getLogger(ZabbixAgent.class);
}
