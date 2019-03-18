package com.alibaba.metrics.threadpool;

import com.alibaba.metrics.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This gauge set is used to collect @see ThreadPoolExecutor metrics
 *
 * @author arebya
 * @version 1.0
 * @date 2019/3/18
 **/
public class ThreadPoolMetricsGaugeSet extends CachedMetricSet implements DynamicMetricSet {


    private static final String[] THREAD_POOL_METRICS_GUAGES = new String[]{"active", "queued", "completed", "pool"};


    private long[] threadPoolMetrics;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final MetricName metricName;


    public ThreadPoolMetricsGaugeSet(ThreadPoolExecutor threadPoolExecutor, MetricName metricName) {
        this(DEFAULT_DATA_TTL, TimeUnit.MILLISECONDS, Clock.defaultClock(), threadPoolExecutor, metricName);
    }

    public ThreadPoolMetricsGaugeSet(long dataTTL, TimeUnit unit, ThreadPoolExecutor threadPoolExecutor,
                                     MetricName metricName) {
        this(dataTTL, unit, Clock.defaultClock(), threadPoolExecutor, metricName);
    }

    public ThreadPoolMetricsGaugeSet(long dataTTL, TimeUnit unit, Clock clock, ThreadPoolExecutor threadPoolExecutor,
                                     MetricName metricName) {
        super(dataTTL, unit, clock);
        if (threadPoolExecutor == null) {
            throw new RuntimeException("illegal thread pool executor,must not be null");
        }
        threadPoolMetrics = new long[THREAD_POOL_METRICS_GUAGES.length];
        this.threadPoolExecutor = threadPoolExecutor;
        this.metricName = metricName;
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
    public Map<MetricName, Metric> getDynamicMetrics() {
        refreshIfNecessary();
        Map<MetricName, Metric> metrics = new HashMap<MetricName, Metric>();
        for (int i = 0; i < THREAD_POOL_METRICS_GUAGES.length; i++) {
            metrics.put(
                    metricName.resolve(THREAD_POOL_METRICS_GUAGES[i])
                            , new ThreadPoolGauge(i));
        }

        return metrics;
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        return this.getDynamicMetrics();
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
