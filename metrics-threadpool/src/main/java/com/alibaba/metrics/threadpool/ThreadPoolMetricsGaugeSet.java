/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.metrics.threadpool;

import com.alibaba.metrics.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This gauge set is used to collect @see ThreadPoolExecutor metrics
 **/
public class ThreadPoolMetricsGaugeSet extends CachedMetricSet {

    // metrics name
    private static final String[] THREAD_POOL_METRICS_GUAGES = new String[]{"active", "queued", "completed", "pool"};
    // use short ttl
    private static long DEFAULT_DATA_TTL = 10;

    private long[] threadPoolMetrics;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final Map<MetricName, Metric> metrics;

    public ThreadPoolMetricsGaugeSet(ThreadPoolExecutor threadPoolExecutor) {
        this(DEFAULT_DATA_TTL, TimeUnit.MILLISECONDS, Clock.defaultClock(), threadPoolExecutor);
    }

    public ThreadPoolMetricsGaugeSet(long dataTTL, TimeUnit unit, ThreadPoolExecutor threadPoolExecutor) {
        this(dataTTL, unit, Clock.defaultClock(), threadPoolExecutor);
    }

    public ThreadPoolMetricsGaugeSet(long dataTTL, TimeUnit unit, Clock clock, ThreadPoolExecutor threadPoolExecutor) {
        super(dataTTL, unit, clock);
        if (threadPoolExecutor == null) {
            throw new RuntimeException("illegal thread pool executor,must not be null");
        }
        threadPoolMetrics = new long[THREAD_POOL_METRICS_GUAGES.length];
        this.threadPoolExecutor = threadPoolExecutor;
        metrics = new HashMap<MetricName, Metric>(4);
        populateGauges();
    }

    @Override
    protected void getValueInternal() {

        if (threadPoolExecutor == null) {
            return;
        }
        // active count
        threadPoolMetrics[0] = threadPoolExecutor.getActiveCount();
        // queued size
        threadPoolMetrics[1] = threadPoolExecutor.getQueue().size();
        // completed count
        threadPoolMetrics[2] = threadPoolExecutor.getCompletedTaskCount();
        // pool size
        threadPoolMetrics[3] = threadPoolExecutor.getPoolSize();
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        return metrics;
    }

    private void populateGauges() {
        // populate guages
        for (int i = 0; i < THREAD_POOL_METRICS_GUAGES.length; i++) {
            metrics.put(
                    MetricName.build(THREAD_POOL_METRICS_GUAGES[i])
                    , new ThreadPoolGauge(i));
        }
    }


    private class ThreadPoolGauge extends PersistentGauge<Long> {

        private int index;

        public ThreadPoolGauge(int index) {
            this.index = index;
        }

        @Override
        public Long getValue() {
            refreshIfNecessary();
            return threadPoolMetrics[index];
        }
    }
}
