package com.alibaba.metrics.integrate;

import com.alibaba.metrics.IMetricManager;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A metrics cleaner class that will execute a clean up task periodically
 */
public class MetricsCleaner {

    private static final Logger log = LoggerFactory.getLogger(MetricsIntegrateUtils.class);

    private ScheduledExecutorService cleanerExecutor;

    private int keepInterval;

    private int delay;

    public MetricsCleaner(int keepInterval, int delay) {
        this.keepInterval = keepInterval;
        this.delay = delay;
        cleanerExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("cleaner"));
    }

    public void start() {
        cleanerExecutor.scheduleWithFixedDelay(new CleanTask(keepInterval), delay, delay, TimeUnit.SECONDS);
    }

    public void stop() {
        cleanerExecutor.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!cleanerExecutor.awaitTermination(delay*2, TimeUnit.SECONDS)) {
                cleanerExecutor.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!cleanerExecutor.awaitTermination(delay*2, TimeUnit.SECONDS)) {
                    log.warn(getClass().getSimpleName() + ": ScheduledExecutorService did not terminate.");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            cleanerExecutor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    private static class CleanTask extends Thread {

        /**
         * The time interval in milli-seconds, that a metric will be kept before it gets cleaned up.
         * Note that this interval should always be greater than the
         * {@link com.alibaba.metrics.MetricLevel} TRIVIAL level.
         */
        private long keepInterval;

        public CleanTask(int keepInterval) {
            this.keepInterval = 1000L * keepInterval;
        }

        @Override
        public void run() {
            int cleanCount = 0;
            IMetricManager manager = MetricManager.getIMetricManager();
            for (String groupName : manager.listMetricGroups()) {
                MetricRegistry registry = manager.getMetricRegistryByGroup(groupName);
                Map<MetricName, Metric> metrics = registry.getMetrics();
                long curTs = System.currentTimeMillis();
                for (Map.Entry<MetricName, Metric> entry : metrics.entrySet()) {
                    try {
                        if (curTs - entry.getValue().lastUpdateTime() > keepInterval) {
                            if (registry.remove(entry.getKey())) {
                                log.info("Removed: {}", entry.getKey());
                                cleanCount++;
                            }
                        }
                    } catch (Throwable t) {
                        // ignore
                    }
                }
            }
            log.info("Clean up metrics count: {}", cleanCount);
        }
    }
}
