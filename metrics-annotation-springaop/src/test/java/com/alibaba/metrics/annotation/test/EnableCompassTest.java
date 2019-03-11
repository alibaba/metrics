package com.alibaba.metrics.annotation.test;

import com.alibaba.metrics.Compass;
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
public class EnableCompassTest {

    @Autowired
    private MetricsAnnotationTestService metricsTestService;

    @Test
    public void test() {
        this.metricsTestService.testCompass1();

        Compass compass = MetricManager.getCompass("test",
            MetricName.build("ascp.upcp-scitem.metrics-annotation.compass.test1")
                .tagged("purpose", "test"));

        long successCount = compass.getSuccessCount();
        TestCase.assertEquals(1, successCount);
        long count = compass.getCount();
        TestCase.assertEquals(1, count);

        try {
            this.metricsTestService.testCompass1();
            this.metricsTestService.testCompass2();
            TestCase.fail();
        } catch (Exception e) {
            //ignore
        }

        successCount = compass.getSuccessCount();
        TestCase.assertEquals(2, successCount);
        count = compass.getCount();
        TestCase.assertEquals(3, count);
    }
}
