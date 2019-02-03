package com.alibaba.metrics.reporter;

import com.alibaba.metrics.Compass;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Histogram;
import com.alibaba.metrics.IMetricManager;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.Timer;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.common.filter.CompositeMetricFilter;
import com.alibaba.metrics.common.filter.TimeMetricLevelFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * report MetricManager 里所有的metrics
 *
 * @see ScheduledReporter
 *
 */
public abstract class MetricManagerReporter implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(MetricManagerReporter.class);
    private static final AtomicInteger FACTORY_ID = new AtomicInteger();

    protected final double durationFactor;
    protected final double rateFactor;

    private final IMetricManager metricManager;
    private final ScheduledExecutorService executor;
    private final String durationUnit;
    private final String rateUnit;

    private long schedulePeriod = 1;
    private TimeUnit scheduleUnit = TimeUnit.SECONDS;

    private TimeMetricLevelFilter timeMetricLevelFilter;
    private CompositeMetricFilter compositeMetricFilter;
    private ScheduledFuture futureTask;


    private Runnable task = new Runnable() {
        @Override
        public void run() {
            if (!runFlag) {
                return;
            }

            try {
                timeMetricLevelFilter.beforeReport();
                report();
            } catch (Throwable ex) {
                LOG.error("Throwable RuntimeException thrown from {}#report. Exception was suppressed.",
                        MetricManagerReporter.this.getClass().getSimpleName(), ex);
            } finally {
                timeMetricLevelFilter.afterReport();
            }
        }
    };
    /**
     * 控制Report的启动和停止
     */
    private volatile boolean runFlag = true;

    /**
     * A simple named thread factory.
     */
    private static class NamedThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        private NamedThreadFactory(String name) {
            final SecurityManager s = System.getSecurityManager();
            this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = "metrics-" + name + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            final Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    /**
     * Creates a new {@link MetricManagerReporter} instance.
     *
     * @param metricManager the {@link com.alibaba.metrics.IMetricManager} containing the metrics this
     *                 reporter will report
     * @param name     the reporter's name
     * @param filter   the filter for which metrics to report
     * @param rateUnit a unit of time
     * @param durationUnit a unit of time
     */
    protected MetricManagerReporter(IMetricManager metricManager,
                                String name,
                                MetricFilter filter,
                                MetricsCollectPeriodConfig metricsReportPeriodConfig,
                                TimeUnit rateUnit,
                                TimeUnit durationUnit) {
        this(metricManager, filter, new TimeMetricLevelFilter(metricsReportPeriodConfig), rateUnit, durationUnit,
                Executors.newSingleThreadScheduledExecutor(
                        new NamedThreadFactory(name + '-' + FACTORY_ID.incrementAndGet())));
    }

    /**
     * Creates a new {@link MetricManagerReporter} instance.
     *
     * @param metricManager the {@link com.alibaba.metrics.IMetricManager} containing the metrics this
     *                 reporter will report
     * @param name     the reporter's name
     * @param filter   the filter for which metrics to report
     * @param rateUnit a unit of time
     * @param durationUnit a unit of time
     */
    protected MetricManagerReporter(IMetricManager metricManager,
                                    String name,
                                    MetricFilter filter,
                                    TimeMetricLevelFilter timeMetricLevelFilter,
                                    TimeUnit rateUnit,
                                    TimeUnit durationUnit) {
        this(metricManager, filter, timeMetricLevelFilter, rateUnit, durationUnit,
                Executors.newSingleThreadScheduledExecutor(
                        new NamedThreadFactory(name + '-' + FACTORY_ID.incrementAndGet())));
    }

    /**
     * Creates a new {@link MetricManagerReporter} instance.
     *
     * @param metricManager the {@link com.alibaba.metrics.IMetricManager} containing the metrics this
     *                 reporter will report
     * @param filter   the filter for which metrics to report
     * @param executor the executor to use while scheduling reporting of metrics.
     */
    protected MetricManagerReporter(IMetricManager metricManager,
                                MetricFilter filter,
                                TimeMetricLevelFilter timeMetricLevelFilter,
                                TimeUnit rateUnit,
                                TimeUnit durationUnit,
                                ScheduledExecutorService executor) {
        this.metricManager = metricManager;
        this.executor = executor;
        this.rateFactor = rateUnit.toSeconds(1);
        this.rateUnit = calculateRateUnit(rateUnit);
        this.durationFactor = 1.0 / durationUnit.toNanos(1);
        this.durationUnit = durationUnit.toString().toLowerCase(Locale.US);
        this.timeMetricLevelFilter = timeMetricLevelFilter;
        this.compositeMetricFilter = new CompositeMetricFilter(timeMetricLevelFilter, filter);
    }

    /**
     * 暂停report任务
     */
    public void suspension() {
        runFlag = false;
    }

    /**
     * 恢复report任务
     */
    public void resumption() {
        runFlag = true;
    }

    /**
     * Starts the reporter polling at the given period.
     *
     * @param period the amount of time between polls
     * @param unit   the unit for {@code period}
     */
    public void start(long period, TimeUnit unit) {
        this.schedulePeriod =period;
        this.scheduleUnit =unit;
        futureTask = executor.scheduleWithFixedDelay(task, schedulePeriod, schedulePeriod, scheduleUnit);
    }

    /**
     * Stops the reporter and shuts down its thread of execution.
     *
     * Uses the shutdown pattern from:
     * http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html
     */
    public void stop() {
        executor.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(schedulePeriod*2, scheduleUnit)) {
                executor.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(schedulePeriod*2, scheduleUnit)) {
                    LOG.warn(getClass().getSimpleName() + ": ScheduledExecutorService did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public void reschedule(long period, TimeUnit unit) {
        // the parameter false mean no interrupt to the running task.
        if (futureTask.cancel(false)) {
            this.schedulePeriod = period;
            this.scheduleUnit = unit;
            futureTask = executor.scheduleWithFixedDelay(task, period, period, unit);
        }
    }

    /**
     * Stops the reporter and shuts down its thread of execution.
     */
    @Override
    public void close() {
        stop();
    }

    /**
     * Report the current values of all metrics in the metricManager.
     */
    public void report() {
        synchronized (this) {

            Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> categoryMetrics = metricManager
                    .getAllCategoryMetrics(compositeMetricFilter);

            report((Map<MetricName, Gauge>) categoryMetrics.get(Gauge.class),
                    (Map<MetricName, Counter>) categoryMetrics.get(Counter.class),
                    (Map<MetricName, Histogram>) categoryMetrics.get(Histogram.class),
                    (Map<MetricName, Meter>) categoryMetrics.get(Meter.class),
                    (Map<MetricName, Timer>) categoryMetrics.get(Timer.class),
                    (Map<MetricName, Compass>) categoryMetrics.get(Compass.class),
                    (Map<MetricName, FastCompass>) categoryMetrics.get(FastCompass.class));
        }
    }

    /**
     * Called periodically by the polling thread. Subclasses should report all the given metrics.
     *
     * @param gauges     all of the gauges in the metricManager
     * @param counters   all of the counters in the metricManager
     * @param histograms all of the histograms in the metricManager
     * @param meters     all of the meters in the metricManager
     * @param timers     all of the timers in the metricManager
     * @param compasses  all of the compasses in the metricManager
     */
    public abstract void report(Map<MetricName, Gauge> gauges,
                                Map<MetricName, Counter> counters,
                                Map<MetricName, Histogram> histograms,
                                Map<MetricName, Meter> meters,
                                Map<MetricName, Timer> timers,
                                Map<MetricName, Compass> compasses,
                                Map<MetricName, FastCompass> fastCompasses);

    protected String getRateUnit() {
        return rateUnit;
    }

    protected String getDurationUnit() {
        return durationUnit;
    }

    protected double convertDuration(double duration) {
        return duration * durationFactor;
    }

    protected double convertRate(double rate) {
        return rate * rateFactor;
    }

    private String calculateRateUnit(TimeUnit unit) {
        final String s = unit.toString().toLowerCase(Locale.US);
        return s.substring(0, s.length() - 1);
    }
}
