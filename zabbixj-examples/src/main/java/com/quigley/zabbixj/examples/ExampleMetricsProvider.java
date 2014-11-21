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

package com.quigley.zabbixj.examples;

import com.quigley.zabbixj.metrics.MetricsException;
import com.quigley.zabbixj.metrics.MetricsKey;
import com.quigley.zabbixj.metrics.MetricsProvider;

import java.util.Random;

/**
 * An example MetricsProvider implementation that returns random numbers. This
 * example also illustrates the use of MetricsKey concepts, including
 * parameters.
 * 
 * @author Michael Quigley
 */
public class ExampleMetricsProvider implements MetricsProvider {
	public ExampleMetricsProvider() {
		random = new Random();
	}

	public Object getValue(MetricsKey mKey) throws MetricsException {
		if(mKey.getKey().equals("random")) {
            int maxRandom = 100;
            if(mKey.isParameters()) {
                maxRandom = Integer.parseInt(mKey.getParameters()[0]);
            }

            return new Float(random.nextFloat() * maxRandom);

        } else {
		    throw new MetricsException("I do not support the key: " + mKey.getKey());
        }
    }
	
	private Random random;
}
