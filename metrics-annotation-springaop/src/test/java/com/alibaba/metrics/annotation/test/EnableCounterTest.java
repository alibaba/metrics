package com.alibaba.metrics.annotation.test;

import com.alibaba.metrics.Counter;
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
public class EnableCounterTest {

    @Autowired
    private MetricsAnnotationTestService metricsTestService;

    @Test
    public void test1() {
        this.metricsTestService.testCounter1();

        Counter counter = MetricManager.getCounter("test",
            MetricName.build("ascp.upcp-scitem.metrics-annotation.counter.test1")
                .tagged("from", "pc", "type", "dingtalk"));
        TestCase.assertEquals(1, counter.getCount());

        this.metricsTestService.testCounter1();
        this.metricsTestService.testCounter1();
        TestCase.assertEquals(3, counter.getCount());
    }

    @Test
    public void test2() {
        this.metricsTestService.testCounter2();

        Counter counter = MetricManager.getCounter("test",
            MetricName.build("ascp.upcp-scitem.metrics-annotation.counter.test2"));
        TestCase.assertEquals(-3, counter.getCount());

        this.metricsTestService.testCounter2();
        this.metricsTestService.testCounter2();
        TestCase.assertEquals(-9, counter.getCount());
    }
}
