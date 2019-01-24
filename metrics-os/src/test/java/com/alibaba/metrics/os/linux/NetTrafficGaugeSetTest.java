package com.alibaba.metrics.os.linux;

import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.ManualClock;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NetTrafficGaugeSetTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testNetTrafficMetrics() {

        ManualClock clock = new ManualClock();

        NetTrafficGaugeSet netTrafficGaugeSet = new NetTrafficGaugeSet(
                "src/test/resources/proc_net_dev", 10, TimeUnit.MILLISECONDS, clock);

        Map<MetricName, Metric> metrics = netTrafficGaugeSet.getMetrics();

        Assert.assertEquals(16, metrics.keySet().size());

        clock.addMillis(20);

        netTrafficGaugeSet.setFilePath("src/test/resources/proc_net_dev_2");

        Gauge<Double> gauge1 = (Gauge)metrics.get(MetricName.build("net.in.bytes").tagged("face", "eth0"));
        Gauge<Double> gauge2 = (Gauge)metrics.get(MetricName.build("net.out.bytes").tagged("face", "eth0"));

        Assert.assertEquals(1000000.0d, gauge1.getValue(), 0.0001d);
        Assert.assertEquals(500000.0d, gauge2.getValue(), 0.0001d);

        clock.addMillis(2000);

        netTrafficGaugeSet.setFilePath("src/test/resources/proc_net_dev_3");

        Assert.assertEquals(200000.0d, gauge1.getValue(), 0.0001d);
        Assert.assertEquals(150000.0d, gauge2.getValue(), 0.0001d);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLinuxKernel310() {
        ManualClock clock = new ManualClock();

        NetTrafficGaugeSet netTrafficGaugeSet = new NetTrafficGaugeSet(
                "src/test/resources/proc_net_dev_kernel_3.10", 10, TimeUnit.MILLISECONDS, clock);

        Map<MetricName, Metric> metrics = netTrafficGaugeSet.getMetrics();

        clock.addMillis(200);

        netTrafficGaugeSet.setFilePath("src/test/resources/proc_net_dev_kernel_3.10_2");

        Gauge<Double> gauge1 = (Gauge)metrics.get(MetricName.build("net.in.bytes").tagged("face", "eth0"));
        Gauge<Double> gauge2 = (Gauge)metrics.get(MetricName.build("net.in.packets").tagged("face", "eth0"));

        Assert.assertEquals(10000.0d, gauge1.getValue(), 0.0001d);
        Assert.assertEquals(30000.0d, gauge2.getValue(), 0.0001d);
    }
}
