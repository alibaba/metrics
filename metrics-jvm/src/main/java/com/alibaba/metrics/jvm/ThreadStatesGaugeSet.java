package com.alibaba.metrics.jvm;

import com.alibaba.metrics.CachedMetricSet;
import com.alibaba.metrics.Clock;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.PersistentGauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A set of gauges for the number of threads in their various states and deadlock detection.
 */
public class ThreadStatesGaugeSet extends CachedMetricSet {

    private static final Logger logger = LoggerFactory.getLogger(ThreadStatesGaugeSet.class);

    // do not compute stack traces.
    private final static int STACK_TRACE_DEPTH = 0;

    private final ThreadMXBean threads;

    private final Map<MetricName, Metric> gauges;

    private final int[] threadCounts;

    private int liveThreadCount;

    private int daemonThreadCount;

    private int deadlockThreadCount;

    /**
     * Creates a new set of gauges using the default MXBeans.
     */
    public ThreadStatesGaugeSet() {
        this(ManagementFactory.getThreadMXBean(), DEFAULT_DATA_TTL, TimeUnit.MILLISECONDS, Clock.defaultClock());
    }

    public ThreadStatesGaugeSet(long dataTTL, TimeUnit unit) {
        this(ManagementFactory.getThreadMXBean(), dataTTL, unit, Clock.defaultClock());
    }

    /**
     * Creates a new set of gauges using the given MXBean and detector.
     *
     * @param threads          a thread MXBean
     */
    public ThreadStatesGaugeSet(ThreadMXBean threads, long dataTTL, TimeUnit unit, Clock clock) {
        super(dataTTL, unit, clock);
        this.threads = threads;
        this.gauges = new HashMap<MetricName, Metric>();
        this.threadCounts = new int[Thread.State.values().length];
        populateGauges();
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        return Collections.unmodifiableMap(gauges);
    }

    @Override
    protected void getValueInternal() {
        final ThreadInfo[] allThreads = threads.getThreadInfo(threads.getAllThreadIds(), STACK_TRACE_DEPTH);
        if (allThreads == null)  {
            logger.warn("java.lang.management.ThreadMXBean.getThreadInfo(long[], int) returns null, might be a JDK bug.");
        } else {
            // clear thread count first
            for (int i = 0; i < threadCounts.length; i++) {
                threadCounts[i] = 0;
            }
            for (ThreadInfo info : allThreads) {
                if (info != null) {
                    threadCounts[info.getThreadState().ordinal()]++;
                }
            }
        }
        liveThreadCount = threads.getThreadCount();
        daemonThreadCount = threads.getDaemonThreadCount();
        deadlockThreadCount = threads.findDeadlockedThreads() == null ? 0 : threads.findDeadlockedThreads().length;
    }

    private void populateGauges() {
        for (final Thread.State state : Thread.State.values()) {
            gauges.put(MetricRegistry.name(state.toString().toLowerCase(), "count"),
                    new PersistentGauge<Integer>() {
                        @Override
                        public Integer getValue() {
                            refreshIfNecessary();
                            return threadCounts[state.ordinal()];
                        }
                    });
        }

        gauges.put(MetricName.build("count"), new PersistentGauge<Integer>() {
            @Override
            public Integer getValue() {
                refreshIfNecessary();
                return liveThreadCount;
            }
        });

        gauges.put(MetricName.build("daemon.count"), new PersistentGauge<Integer>() {
            @Override
            public Integer getValue() {
                refreshIfNecessary();
                return daemonThreadCount;
            }
        });

        gauges.put(MetricName.build("deadlock.count"), new PersistentGauge<Integer>() {
            @Override
            public Integer getValue() {
                refreshIfNecessary();
                return deadlockThreadCount;
            }
        });
    }
}
