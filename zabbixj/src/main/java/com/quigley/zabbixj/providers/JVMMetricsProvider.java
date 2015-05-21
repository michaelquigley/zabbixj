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
package com.quigley.zabbixj.providers;

import com.quigley.zabbixj.metrics.MetricsException;
import com.quigley.zabbixj.metrics.MetricsKey;
import com.quigley.zabbixj.metrics.MetricsProvider;

/**
 * Provides standard metrics for the Java virtual machine.
 *
 * @author Michael Quigley
 */
public class JVMMetricsProvider implements MetricsProvider {
    public Object getValue(MetricsKey mKey) throws MetricsException {
        if (mKey.getKey().equals("memory.free")) {
            Runtime rt = Runtime.getRuntime();
            return new Long(rt.freeMemory());

        } else if (mKey.getKey().equals("memory.max")) {
            Runtime rt = Runtime.getRuntime();
            return new Long(rt.maxMemory());

        } else if (mKey.getKey().equals("memory.total")) {
            Runtime rt = Runtime.getRuntime();
            return new Long(rt.totalMemory());
            
        } else if (mKey.getKey().equals("os.name")) {
        	return System.getProperty("os.name");
        	
        } else if (mKey.getKey().equals("os.arch")) {
        	return System.getProperty("os.arch");
        }

        throw new MetricsException("Unknown Key: " + mKey.getKey());
    }
}