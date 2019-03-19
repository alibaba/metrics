/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.metrics.threadpool;

import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolMetricsGuageSetTest {


    @Test
    public void testCollectThreadPoolMetrics() {

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

        MetricName name = MetricName.build("threadpool");

        MetricManager.register("test", name,
                new ThreadPoolMetricsGaugeSet(10, TimeUnit.MILLISECONDS, executor));

        MetricRegistry registry = MetricManager.getIMetricManager().getMetricRegistryByGroup("test");

        for (int i = 0; i < 100; i++) {
            Runnable t = new Runnable() {

                @Override
                public void run() {
                    // do thread work
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }
            };
            if (i == 10) {
                Assert.assertTrue(
                        (Long) registry.getGauges().get(name.resolve("active")).getValue() > 0L);
                Assert.assertTrue(
                        (Long) registry.getGauges().get(name.resolve("queued")).getValue() >= 0L);
            }
            executor.submit(t);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.assertEquals(100L, registry.getGauges().get(name.resolve("completed")).getValue());
        Assert.assertEquals(3L, registry.getGauges().get(name.resolve("pool")).getValue());


    }
}
