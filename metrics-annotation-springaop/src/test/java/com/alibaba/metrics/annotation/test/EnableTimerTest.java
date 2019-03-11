package com.alibaba.metrics.annotation.test;

import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.Timer;
import com.alibaba.metrics.annotation.MetricsAnnotationInterceptor;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MetricsAnnotationInterceptor.class, TestConfig.class})
public class EnableTimerTest {

    @Autowired
    private MetricsAnnotationTestService metricsTestService;

    @Test
    public void test() {
        this.metricsTestService.testTimer1();

        Timer timer = MetricManager.getTimer("test",
            MetricName.build("ascp.upcp-scitem.metrics-annotation.timer.test1")
                .tagged("purpose", "test"));
        TestCase.assertEquals(1, timer.getCount());

        this.metricsTestService.testTimer1();
        this.metricsTestService.testTimer1();

        TestCase.assertEquals(3, timer.getCount());
    }
}
