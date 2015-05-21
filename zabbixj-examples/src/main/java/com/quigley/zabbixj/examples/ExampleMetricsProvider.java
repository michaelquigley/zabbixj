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
