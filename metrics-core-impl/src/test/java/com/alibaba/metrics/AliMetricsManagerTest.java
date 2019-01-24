package com.alibaba.metrics;

import org.junit.Assert;
import org.junit.Test;

public class AliMetricsManagerTest {

    @Test
    public void testGetMetricRegistryByGroup() {
        AliMetricManager metricManager = new AliMetricManager();
        MetricRegistry registry = metricManager.getMetricRegistryByGroup("test");
        Assert.assertEquals(registry, metricManager.getMetricRegistryByGroup("test"));
        Assert.assertNotEquals(registry, metricManager.getMetricRegistryByGroup("test2"));
    }
}
