package com.alibaba.metrics;


import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DynamicMetricSetTest {

    MetricRegistry registry = new MetricRegistryImpl();

    private class TestDynamicMetricSet implements DynamicMetricSet {

        private Map<MetricName, Metric> metrics = new HashMap<MetricName, Metric>();

        public TestDynamicMetricSet() {
            MetricName name = MetricName.build("test.counter");
            metrics.put(name, new CounterImpl());
        }

        @Override
        public Map<MetricName, Metric> getDynamicMetrics() {
            return metrics;
        }

        @Override
        public long lastUpdateTime() {
            return 0;
        }
    }

    @Test
    public void testDynamicMetricSet() {
        registry.counter("test.counter2");
        registry.register("dynamic", new TestDynamicMetricSet());
        Assert.assertEquals(2, registry.getCounters().size());
    }
}
