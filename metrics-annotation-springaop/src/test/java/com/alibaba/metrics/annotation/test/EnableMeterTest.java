package com.alibaba.metrics.annotation.test;

import com.alibaba.metrics.Meter;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.annotation.MetricsAnnotationInterceptor;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MetricsAnnotationInterceptor.class, TestConfig.class})
public class EnableMeterTest {

    @Autowired
    private MetricsAnnotationTestService dubboMetricsTestService;

    @Test
    public void test1() {
        this.dubboMetricsTestService.testMeter1();

        Meter meter = MetricManager.getMeter("test",
            MetricName.build("ascp.upcp-scitem.metrics-annotation.meter.test1")
                .tagged("purpose", "test"));
        TestCase.assertEquals(1, meter.getCount());

        this.dubboMetricsTestService.testMeter1();
        this.dubboMetricsTestService.testMeter1();
        TestCase.assertEquals(3, meter.getCount());
    }

    @Test
    public void test2() {
        this.dubboMetricsTestService.testMeter2();

        Meter meter = MetricManager.getMeter("test",
            MetricName.build("ascp.upcp-scitem.metrics-annotation.meter.test2"));
        TestCase.assertEquals(meter.getCount(), 4);

        this.dubboMetricsTestService.testMeter2();
        this.dubboMetricsTestService.testMeter2();
        TestCase.assertEquals(meter.getCount(), 12);
    }
}
