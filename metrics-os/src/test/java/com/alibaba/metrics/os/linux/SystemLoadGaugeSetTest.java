package com.alibaba.metrics.os.linux;

import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SystemLoadGaugeSetTest {

    @Test
    public void testSystemLoadMetrics() {

        SystemLoadGaugeSet systemLoadGaugeSet = new SystemLoadGaugeSet(
                10, TimeUnit.MILLISECONDS, "src/test/resources/proc_loadavg");

        Map<MetricName, Metric> metrics = systemLoadGaugeSet.getMetrics();

        Assert.assertEquals(3, metrics.keySet().size());

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Gauge _1min = (Gauge)metrics.get(MetricName.build("load.1min"));
        Gauge _5min = (Gauge)metrics.get(MetricName.build("load.5min"));
        Gauge _15min = (Gauge)metrics.get(MetricName.build("load.15min"));

        Assert.assertEquals(0.08f, _1min.getValue());
        Assert.assertEquals(0.06f, _5min.getValue());
        Assert.assertEquals(0.05f, _15min.getValue());

    }
}
