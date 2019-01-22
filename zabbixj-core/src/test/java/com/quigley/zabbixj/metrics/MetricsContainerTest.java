package com.quigley.zabbixj.metrics;

import org.junit.Test;

public class MetricsContainerTest {

    @Test(expected = MetricsException.class)
    public void testGetProvider_1() {
        MetricsContainer c = new MetricsContainer();
        c.getMetric("provider.some_key");
    }

    @Test
    public void testGetProvider_2() {
        MetricsContainer c = new MetricsContainer(false);
        c.getMetric("provider.some_key");
    }

    @Test
    public void testGetMetric_1() {
        MetricsContainer c = new MetricsContainer(false);
        c.addProvider("provider", new MetricsProvider() {
            @Override
            public Object getValue(MetricsKey mKey) throws MetricsException {
                return null;
            }
        });
        c.getMetric("provider.some_key");
        c.getMetric("other.some_key");
    }

    @Test(expected = MetricsException.class)
    public void testGetMetric_2() {
        MetricsContainer c = new MetricsContainer();
        c.addProvider("provider", new MetricsProvider() {
            @Override
            public Object getValue(MetricsKey mKey) throws MetricsException {
                throw new MetricsException();
            }
        });
        c.getMetric("provider.some_key");
    }
}
