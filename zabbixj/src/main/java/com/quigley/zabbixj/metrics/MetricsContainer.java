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

package com.quigley.zabbixj.metrics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsContainer {
	public MetricsContainer() {
		container = new HashMap<String, MetricsProvider>();
	}

	/**
	 * List the MetricsProvider keys in this MetricsContainer.
	 * @return a String[] containing the MetricsProvider keys.
	 */
	public String[] listProviders() {
		String[] providers = new String[container.size()];

		int i = 0;
		Iterator<String> ki = container.keySet().iterator();
		while(ki.hasNext()) {
			providers[i++] = (String) ki.next();	
		}
		
		return providers;
	}

	/**
	 * Add a MetricsProvider to this container.
	 * @param name the name of the MetricsProvider.
	 * @param provider the MetricsProvider instance.
	 */
	public void addProvider(String name, MetricsProvider provider) {
        if(log.isInfoEnabled()) {
            log.info("Adding Provider: " + provider.getClass().getName() + "=" + name);
        }
		
        container.put(name, provider);
	}
	
	public void addProviders(Map<String, MetricsProvider> providers) {
		if(log.isInfoEnabled()) {
			for(String name : providers.keySet()) {
				MetricsProvider provider = providers.get(name);
				log.info("Adding Provider: " + provider.getClass().getName() + "=" + name);	
			}
		}
		container.putAll(providers);
	}
	
	/**
	 * Retrieve a MetricsProvider.
	 * @param name the name of the MetricsProvider to retrieve.
	 * @return the named MetricsProvider instance.
	 * @throws MetricsException when the named MetricsProvider does not exist.
	 */
	public MetricsProvider getProvider(String name) throws MetricsException {
		if(container.containsKey(name)) {
			return (MetricsProvider) container.get(name);
		} else {
			throw new MetricsException("No MetricsProvider with name: " + name);
		}
	}	

	/**
	 * Retrieve a value from this MetricsContainer.
	 * @param key the fully-qualified key naming the metric.
	 * @return the requested value.
	 * @throws MetricsException when the fully-qualified key does not exist.
	 */
	public Object getMetric(String key) throws MetricsException {
		MetricsKey mk = new MetricsKey(key);
		MetricsProvider provider = getProvider(mk.getProvider());
		return provider.getValue(mk);
	}

	private Map<String, MetricsProvider> container;

    private static Logger log = LoggerFactory.getLogger(MetricsProvider.class);
}
