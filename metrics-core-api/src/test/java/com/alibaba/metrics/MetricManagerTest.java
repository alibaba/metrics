package com.alibaba.metrics;

import org.junit.Assert;
import org.junit.Test;

public class MetricManagerTest {

    @Test
    public void testNOPMetricManager() {

        Assert.assertTrue(MetricManager.getIMetricManager() instanceof NOPMetricManager);
    }

    @Test
    public void testNOPCompass() {

        Compass compass = MetricManager.getCompass("test", MetricName.build("com.taobao.test"));
        Compass.Context context = compass.time();
        context.stop();
        context.success();
        context.error("error1");

        Assert.assertEquals(0, compass.getCount());
        Assert.assertEquals(0, compass.getSuccessCount());
        Assert.assertEquals(0, compass.getErrorCodeCounts().size());
    }
}
