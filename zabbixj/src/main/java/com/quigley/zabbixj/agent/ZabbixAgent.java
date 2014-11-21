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

package com.quigley.zabbixj.agent;

import com.quigley.zabbixj.agent.active.ActiveThread;
import com.quigley.zabbixj.agent.passive.ListenerThread;
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
        
        enablePassive = true;
        listenPort = 10050;
        listenAddress = null;
        
        enableActive = false;
        serverAddress = null;
        serverPort = 10051;
        refreshInterval = 120;
    }
    
    /**
     * Start processing requests. Starts the passive listener (if enabled) and the
     * active agent (if enabled).
     */
    public void start() throws Exception {
        if(log.isInfoEnabled()) {
            log.info("Starting Zabbix agent.");
        }

        if(enablePassive) {
        	if(log.isInfoEnabled()) {
        		log.info("Starting passive listener.");
        	}
        	
	        if(listenAddress == null) {
	            listenerThread = new ListenerThread(metricsContainer, listenPort);
	        } else {
	            listenerThread = new ListenerThread(metricsContainer, listenAddress, listenPort);
	        }
	        listenerThread.start();
	        
	        if(log.isInfoEnabled()) {
	        	log.info("Passive listener started.");
	        }
        }

        if(enableActive) {
        	if(log.isInfoEnabled()) {
        		log.info("Starting active agent.");
        	}
        	
        	activeThread = new ActiveThread(metricsContainer, hostName, serverAddress, serverPort, refreshInterval);
        	activeThread.start();
        	
        	if(log.isInfoEnabled()) {
        		log.info("Active agent started.");
        	}
        }
        
        if(log.isInfoEnabled()) {
            log.info("Zabbix agent started.");
        }
    }

    /**
     * Stop processing requests. Stops any passive or active components started in the <code>start</code>
     * method.
     */
    public void stop() {
        if(log.isInfoEnabled()) {
            log.info("Stopping Zabbix agent.");
        }

        if(enablePassive) {
        	if(log.isInfoEnabled()) {
        		log.info("Stopping passive listener.");
        	}
        	
	        listenerThread.shutdown();
	        try {
	            listenerThread.join();
	
	        } catch(InterruptedException ie) {
	            //
	        }
	        
	        if(log.isInfoEnabled()) {
	        	log.info("Passive listener stopped.");
	        }
        }
        
        if(enableActive) {
        	if(log.isInfoEnabled()) {
        		log.info("Stopping active agent.");
        	}
        	
        	activeThread.shutdown();
        	try {
        		activeThread.join();
        		
        	} catch(InterruptedException ie) {
        		//
        	}
        	
        	if(log.isInfoEnabled()) {
        		log.info("Active agent stopped.");
        	}
        }

        if(log.isInfoEnabled()) {
            log.info("Zabbix agent stopped.");
        }
    }

    /**
     * Return the value of property <code>enablePassive</code>.
     * @return the current value of property enablePassive.
     */
    public boolean isEnablePassive() {
		return enablePassive;
	}

    /**
     * Set the value of property <code>enablePassive</code>.
     * @param enablePassive Set to <code>true</code> if the agent should start a listener
     * 		  for passive checks. Set to <code>false</code> if the agent should omit the
     * 		  passive listener. 
     */
	public void setEnablePassive(boolean enablePassive) {
		this.enablePassive = enablePassive;
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
     * Return the value of property <code>enableActive</code>.
     * @return the current value of <code>enableActive</code>. Defaults to <code>false</code>.
     */
    public boolean isEnableActive() {
		return enableActive;
	}
    
    /**
     * Set the value of property <code>enableActive</code>.
     * @param enableActive Set to <code>true</code> when an active check configuration is
     * 		  desired, <code>false</code> otherwise.
     */
	public void setEnableActive(boolean enableActive) {
		this.enableActive = enableActive;
	}

	/**
	 * Return the value of property <code>hostName</code>.
	 * @return the current value of <code>hostName</code>.
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Set the value of property <code>hostName</code>.
	 * @param hostName the name of this host, as configured in Zabbix.
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * Return the value of property <code>serverAddress</code>.
	 * @return the current value of <code>serverAddress</code>.
	 */
	public InetAddress getServerAddress() {
		return serverAddress;
	}
	
	/**
	 * Set the value of property <code>serverAddress</code>.
	 * @param serverAddress the IP address for the Zabbix server listening for active checks.
	 */
	public void setServerAddress(InetAddress serverAddress) {
		this.serverAddress = serverAddress;
	}

	/**
	 * Return the value of property <code>serverPort</code>.
	 * @return the current value of the <code>serverPort</code> property.
	 */
	public int getServerPort() {
		return serverPort;
	}
	
	/**
	 * Set the value of property <code>serverPort</code>.
	 * @param serverPort the TCP port for the Zabbix server listening for active checks. Defaults to
	 * 		  <code>10051</code>.
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	/**
	 * Return the value of property <code>refreshInterval</code>.
	 * @return the current value of the <code>refreshInterval</code> property. Defaults to <code>120</code>.
	 */
	public int getRefreshInterval() {
		return refreshInterval;
	}
	public void setRefreshInterval(int refreshInterval) {
		this.refreshInterval = refreshInterval;
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
    
    private boolean enablePassive;
    private InetAddress listenAddress;
    private int listenPort;
    
    private boolean enableActive;
    private String hostName;
    private InetAddress serverAddress;
    private int serverPort;
    private int refreshInterval;

    private MetricsContainer metricsContainer;
    private ListenerThread listenerThread;
    private ActiveThread activeThread;

    private static Logger log = LoggerFactory.getLogger(ZabbixAgent.class);
}
