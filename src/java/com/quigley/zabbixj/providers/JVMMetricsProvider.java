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