/*
 * $Id: MetricsProvider.java 827 2009-02-26 18:39:30Z michael $
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

package com.quigley.zabbixj.metrics;

/**
 * The MetricsProvider interface must be implemented by any classes wishing to
 * expose metrics via ZABBIX/J.
 * 
 * @author Michael Quigley
 */
public interface MetricsProvider {

	/**
	 * @param mKey a MetricsKey instance describing the metric to retrieve.
	 * @return the value of the key.
	 * @throws MetricsException when a problem is encountered retrieving a value
	 *         for the specified key; typically when a key is not found.
	 */
	public Object getValue(MetricsKey mKey) throws MetricsException;

}
