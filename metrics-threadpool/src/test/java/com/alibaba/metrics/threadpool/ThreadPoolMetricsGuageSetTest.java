package com.alibaba.metrics.threadpool;

import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author arebya
 * @version 1.0
 * @date 2019/3/18
 **/
public class ThreadPoolMetricsGuageSetTest {


    @Test
    public void testCollectThreadPoolMetrics() {

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

        MetricName name = MetricName.build("threadpool");

        MetricManager.register("threadpool", name, new ThreadPoolMetricsGaugeSet(10, TimeUnit.MILLISECONDS, executor, name));

        MetricRegistry registry = MetricManager.getIMetricManager().getMetricRegistryByGroup("threadpool");

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
                        (Long) registry.getGauges().get(MetricName.build("threadpool.active")).getValue() > 0L);
                Assert.assertTrue(
                        (Long) registry.getGauges().get(MetricName.build("threadpool.queued")).getValue() >= 0L);
            }
            executor.submit(t);
//            System.out.println("round " + i + " thread pool stat[active:" +
//                    registry.getGauges().get(MetricName.build("threadpool.active")).getValue() +
//                    ",queued:" + registry.getGauges().get(MetricName.build("threadpool.queued")).getValue() + "]");
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.assertNotNull(registry.getGauges().get(MetricName.build("threadpool.active")));
        Assert.assertNotNull(registry.getGauges().get(MetricName.build("threadpool.queued")));
        Assert.assertEquals(100L, registry.getGauges().get(MetricName.build("threadpool.completed")).getValue());
        Assert.assertEquals(3L, registry.getGauges().get(MetricName.build("threadpool.pool")).getValue());


    }
}
