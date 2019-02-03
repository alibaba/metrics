package com.alibaba.metrics.annotation.test;

import com.alibaba.metrics.Histogram;
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
public class EnableHistogramTest {
    @Autowired
    private MetricsAnnotationTestService dubboMetricsTestService;

    @Test
    public void test1() {
        this.dubboMetricsTestService.testHistogram1();

        Histogram histogram = MetricManager.getHistogram("test",
            MetricName.build("ascp.upcp-scitem.metrics-annotation.histogram.test1")
                .tagged("purpose", "test"));
        TestCase.assertEquals(1, histogram.getCount());

        this.dubboMetricsTestService.testHistogram2();

        this.dubboMetricsTestService.testHistogram3();

        this.dubboMetricsTestService.testHistogram4();

        this.dubboMetricsTestService.testHistogram5();

        TestCase.assertEquals(5, histogram.getCount());
    }
}
