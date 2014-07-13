package com.quigley.zabbixj.metrics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MetricsKeyTest {
	@Test
	public void testSimpleMetricsKey() {
		MetricsKey key = new MetricsKey("processed-jobs.COUNTER");
		assertEquals("processed-jobs", key.getProvider());
		assertEquals("COUNTER", key.getKey());
	}

	@Test
	public void testMetricsKeyWithMultipleDotProviderName() {
		MetricsKey key = new MetricsKey("com.quigley.zabbixj.processed-jobs.COUNTER");
		assertEquals("com.quigley.zabbixj.processed-jobs", key.getProvider());
		assertEquals("COUNTER", key.getKey());
	}

	@Test
	public void testMetricsKeyWithMultipleDotProviderNameWithParameters() {
		MetricsKey key = new MetricsKey("com.quigley.zabbixj.processed-jobs.COUNTER[ a, b, c]");
		assertEquals("com.quigley.zabbixj.processed-jobs", key.getProvider());
		assertEquals("COUNTER", key.getKey());
		assertEquals(true, key.isParameters());
		assertEquals(3, key.getParameters().length);
		assertEquals(" a", key.getParameters()[0]);
		assertEquals(" b", key.getParameters()[1]);
		assertEquals(" c", key.getParameters()[2]);
	}
	
	@Test
	public void testMetricsKeyWithMultipleDotProviderNameWithQuotedParameters() {
		MetricsKey key = new MetricsKey("com.quigley.zabbixj.processed-jobs.COUNTER[\" a\",\" \"b\", c]");

		assertEquals("com.quigley.zabbixj.processed-jobs", key.getProvider());
		assertEquals("COUNTER", key.getKey());
		assertEquals(true, key.isParameters());
		assertEquals(3, key.getParameters().length);
		assertEquals(" a", key.getParameters()[0]);
		assertEquals(" \"b", key.getParameters()[1]);
		assertEquals(" c", key.getParameters()[2]);
	}
}
