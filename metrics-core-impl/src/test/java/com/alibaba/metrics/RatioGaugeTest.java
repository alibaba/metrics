package com.alibaba.metrics;

import org.junit.Assert;
import org.junit.Test;

public class RatioGaugeTest {

    @Test
    public void testRatioGauge() {

        MetricRegistry registry = new MetricRegistryImpl();
        final Counter total = registry.counter(MetricName.build("test.total"));
        final Counter success = registry.counter(MetricName.build("test.success"));
        total.inc(5);
        success.inc(3);
        RatioGauge successRateGuage = new RatioGauge() {
            @Override
            protected Ratio getRatio() {
                return Ratio.of(success.getCount(), total.getCount());
            }
        };

        registry.register(MetricName.build("test.success_rate"), successRateGuage);

        Assert.assertEquals(0.6d, successRateGuage.getValue().doubleValue(), 0.00001d);
    }

    public void testRatioGauge2() {
        final Counter total = MetricManager.getCounter("test", MetricName.build("test.total"));
        final Counter success = MetricManager.getCounter("test", MetricName.build("test.success"));
        total.inc(5);
        success.inc(3);
        RatioGauge successRateGuage = new RatioGauge() {
            @Override
            protected Ratio getRatio() {
                return Ratio.of(success.getCount(), total.getCount());
            }
        };

        MetricManager.register("test", MetricName.build("test.success_rate"), successRateGuage);

        Assert.assertEquals(0.6d, successRateGuage.getValue().doubleValue(), 0.00001d);
    }
}
