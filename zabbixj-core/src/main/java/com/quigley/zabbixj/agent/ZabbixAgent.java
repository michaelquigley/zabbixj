/*
 * Copyright 2015 Michael Quigley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        failOnNotExistedMetricProvider = true;

        metricsContainer = new MetricsContainer();

        enablePassive = true;
        listenPort = 10050;
        listenAddress = null;

        enableActive = false;
        serverAddress = null;
        serverPort = 10051;
        refreshInterval = 120;
        pskIdentity = null;
        psk = null;
    }

    /**
     * Start processing requests. Starts the passive listener (if enabled) and the
     * active agent (if enabled).
     * @throws Exception when a problem occurs starting the agent.
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
        		log.info("Starting active agent with {}", serverAddress);
        	}

        	InetAddress addr = InetAddress.getByName(serverAddress); // make sure it can be resolved

        	activeThread = new ActiveThread(metricsContainer, hostName, serverAddress, serverPort, refreshInterval, pskIdentity, psk);
        	activeThread.start();

        	if(log.isInfoEnabled()) {
        		log.info("Active agent started with {}", addr);
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
	public String getServerAddress() {
		return serverAddress;
	}

	/**
	 * Set the value of property <code>serverAddress</code>.
	 * @param serverAddress the IP address for the Zabbix server listening for active checks.
	 */
	public void setServerAddress(String serverAddress) {
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
	 * Return the value of property <code>pskIdentity</code>.
	 * @return the current value of the <code>pskIdentity</code> property. Defaults to <code>null</code>.
	 */
	public String getPskIdentity() {
		return pskIdentity;
	}
	public void setPskIdentity(String psk) {
		this.psk = pskIdentity;
	}

	/**
	 * Return the value of property <code>psk</code>.
	 * @return the current value of the <code>psk</code> property. Defaults to <code>null</code>.
	 */
	public String getPsk() {
		return psk;
	}
	public void setPsk(String psk) {
		this.psk = psk;
	}

	/**
	 * Return the value of property <code>failOnNotExistedMetricProvider</code>.
	 * @return the current value of the <code>failOnNotExistedMetricProvider</code> property. Defaults to <code>true</code>.
	 */
	public boolean isFailOnNotExistedMetricProvider() {
		return failOnNotExistedMetricProvider;
	}
	public void setFailOnNotExistedMetricProvider(boolean failOnNotExistedMetricProvider) {
		this.failOnNotExistedMetricProvider = failOnNotExistedMetricProvider;
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
    private boolean failOnNotExistedMetricProvider;
    private String hostName;
    private String serverAddress;
    private int serverPort;
    private int refreshInterval;
    private String pskIdentity;
    private String psk;

    private MetricsContainer metricsContainer;
    private ListenerThread listenerThread;
    private ActiveThread activeThread;

    private static Logger log = LoggerFactory.getLogger(ZabbixAgent.class);
}
