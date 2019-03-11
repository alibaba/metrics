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
    private MetricsAnnotationTestService metricsTestService;

    @Test
    public void test1() {
        this.metricsTestService.testMeter1();

        Meter meter = MetricManager.getMeter("test",
            MetricName.build("ascp.upcp-scitem.metrics-annotation.meter.test1")
                .tagged("purpose", "test"));
        TestCase.assertEquals(1, meter.getCount());

        this.metricsTestService.testMeter1();
        this.metricsTestService.testMeter1();
        TestCase.assertEquals(3, meter.getCount());
    }

    @Test
    public void test2() {
        this.metricsTestService.testMeter2();

        Meter meter = MetricManager.getMeter("test",
            MetricName.build("ascp.upcp-scitem.metrics-annotation.meter.test2"));
        TestCase.assertEquals(meter.getCount(), 4);

        this.metricsTestService.testMeter2();
        this.metricsTestService.testMeter2();
        TestCase.assertEquals(meter.getCount(), 12);
    }
}
