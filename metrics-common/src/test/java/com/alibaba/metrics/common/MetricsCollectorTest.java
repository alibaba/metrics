package com.alibaba.metrics.common;

import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.MetricName;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class MetricsCollectorTest {

    @Test
    public void testDoNotCollectNullValue() {
        MetricsCollector collector =
                MetricsCollectorFactory.createNew(TimeUnit.SECONDS.toSeconds(1),
                        1.0 / TimeUnit.MILLISECONDS.toNanos(1), null);
        collector.collect(MetricName.build("TEST"), new Gauge<Object>() {
            @Override
            public Object getValue() {
                return null;
            }
            @Override
            public long lastUpdateTime() {
                return 0;
            }
        }, System.currentTimeMillis());
        // null value should not be collected.
        Assert.assertEquals(0, collector.build().size());
    }
}
