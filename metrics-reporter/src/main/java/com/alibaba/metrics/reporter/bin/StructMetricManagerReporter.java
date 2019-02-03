package com.alibaba.metrics.reporter.bin;

import com.alibaba.metrics.Clock;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Histogram;
import com.alibaba.metrics.IMetricManager;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.Timer;
import com.alibaba.metrics.common.ClassifiedMetricsCollector;
import com.alibaba.metrics.common.CollectLevel;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.common.MetricsCollector;
import com.alibaba.metrics.common.MetricsCollectorFactory;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.common.filter.BucketMetricLevelFilter;
import com.alibaba.metrics.reporter.MetricManagerReporter;
import com.alibaba.metrics.reporter.file.FileAppender;
import com.alibaba.metrics.reporter.file.JsonMetricFormat;
import com.alibaba.metrics.reporter.file.MetricFormat;
import com.alibaba.metrics.status.LogDescriptionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class StructMetricManagerReporter extends MetricManagerReporter {

    private final static Logger logger = LoggerFactory.getLogger(StructMetricManagerReporter.class);
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final byte[] DEFAULT_DELIMITER_BYTES = "\n".getBytes(UTF_8);
    private static final String appName = System.getProperty("project.name", "DEFAULT_APP");

    private final Clock clock;
    private final Map<String, String> globalTags;
    private final MetricFilter filter;
    private final BinAppender binAppender;
    /**
     * If fileAppender is null, no file will be outputted
     */
    private final FileAppender fileAppender;
    private final MetricsCollectPeriodConfig metricsReportPeriodConfig;
    private final MetricFormat metricFormat;
    private LogDescriptionManager logDescriptionManager;

    /** 是否输出 */
    private volatile boolean advancedMetricsReport = false;

    /** whether to write metric object to file(metric.log) as well */
    private volatile boolean writeMetricToFile = false;

    /** 上一次收集的时间戳 */
    private Map<MetricLevel, Long> lastTimestamp = new HashMap<MetricLevel, Long>();

    /**
     * 控制metrics落盘时需要收集的指标内容
     */
    private CollectLevel collectLevel;

    public StructMetricManagerReporter(IMetricManager metricManager, BinAppender binAppender, FileAppender fileAppender,
                                       LogDescriptionManager logDescriptionManager) {
        this(metricManager, binAppender, fileAppender, Clock.defaultClock(), TimeUnit.SECONDS, TimeUnit.SECONDS,
                TimeUnit.SECONDS, MetricFilter.ALL, new MetricsCollectPeriodConfig(),
                new HashMap<String, String>(), new JsonMetricFormat(), CollectLevel.CLASSIFIER);
        this.logDescriptionManager = logDescriptionManager;
    }

    public StructMetricManagerReporter(IMetricManager metricManager, BinAppender binAppender, FileAppender fileAppender,
            Clock clock, TimeUnit rateUnit, TimeUnit timestampPrecision, TimeUnit durationUnit, MetricFilter filter,
            MetricsCollectPeriodConfig metricsReportPeriodConfig, Map<String, String> globalTags,
            MetricFormat metricFormat, CollectLevel collectLevel) {

        super(metricManager, "bin-reporter", filter, new BucketMetricLevelFilter(metricsReportPeriodConfig), rateUnit,
                durationUnit);
        this.binAppender = binAppender;
        this.fileAppender = fileAppender;
        this.metricFormat = metricFormat;
        this.clock = clock;
        this.globalTags = globalTags;
        this.collectLevel = collectLevel;
        this.metricsReportPeriodConfig = metricsReportPeriodConfig;
        this.filter = filter;
        logDescriptionManager = new LogDescriptionManager(this.binAppender.getPath());
        fillLastUpdateTime();
    }

    @Override
    public void report(Map<MetricName, Gauge> gauges, Map<MetricName, Counter> counters,
            Map<MetricName, Histogram> histograms, Map<MetricName, Meter> meters, Map<MetricName, Timer> timers,
            Map<MetricName, Compass> compasses, Map<MetricName, FastCompass> fastCompasses) {

        long timestamp = clock.getTime();

        MetricsCollector collector = MetricsCollectorFactory.createNew(collectLevel, globalTags, rateFactor,
                durationFactor);
        ((ClassifiedMetricsCollector) collector).setLastTimestamp(lastTimestamp);
        ((ClassifiedMetricsCollector) collector).setAdvancedMetricsReport(advancedMetricsReport);

        logger.debug("last report timestamp is : CRITICAL : {}; MAJOR : {}; NORMAL : {}; MINOR : {}; TRIVIAL : {}",
                lastTimestamp.get(MetricLevel.CRITICAL), lastTimestamp.get(MetricLevel.MAJOR),
                lastTimestamp.get(MetricLevel.NORMAL), lastTimestamp.get(MetricLevel.MINOR),
                lastTimestamp.get(MetricLevel.TRIVIAL));

        for (Entry<MetricName, Gauge> g : gauges.entrySet()) {
            if (g.getValue().getValue() instanceof Collection && ((Collection) g.getValue().getValue()).isEmpty()) {
                continue;
            }
            collector.collect(g.getKey(), g.getValue(), timestamp);
        }

        for (Entry<MetricName, Counter> entry : counters.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), timestamp);
        }

        for (Entry<MetricName, Histogram> entry : histograms.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), timestamp);
        }

        for (Entry<MetricName, Meter> entry : meters.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), timestamp);
        }

        for (Entry<MetricName, Timer> entry : timers.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), timestamp);
        }

        for (Entry<MetricName, Compass> entry : compasses.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), timestamp);
        }

        for (Entry<MetricName, FastCompass> entry : fastCompasses.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), timestamp);
        }

        Map<MetricLevel, Map<Long, List<MetricObject>>> metrics = ((ClassifiedMetricsCollector) collector).getMetrics();

        if (metrics.size() <= 0) {
            return;
        }

        // Use iterator so that we can remove the entry while iteration.
        for (Iterator<Entry<MetricLevel, Map<Long, List<MetricObject>>>> it = metrics.entrySet().iterator(); it.hasNext(); ) {

            Map.Entry<MetricLevel, Map<Long, List<MetricObject>>> entry = it.next();
            final MetricLevel level = entry.getKey();
            int interval = metricsReportPeriodConfig.period(level) * 1000;
            long startTime = lastTimestamp.get(level) + interval;
            long endTime = (timestamp / interval - 1) * interval;

            startTime = ((ClassifiedMetricsCollector) collector).adjustStartTime(startTime, endTime, interval, level);

            Map<Long, List<MetricObject>> timeSequenced = metrics.get(level);

            logger.debug("MetricLevel : {}, startTime : {}, endTime : {}, metricsLength : {}", level, startTime,
                    endTime, timeSequenced == null ? "null" : timeSequenced.size());

            try {
                for (long time = startTime; time <= endTime; time = time + interval) {
                    if (timeSequenced != null && timeSequenced.size() > 0) {

                        final List<MetricObject> objects = timeSequenced.get(time);
                        if (objects != null && objects.size() > 0) {
                            binAppender.append(time, new HashMap<MetricLevel, List<MetricObject>>() {
                                {
                                    put(level, objects);
                                }
                            }, lastTimestamp);
                            if (writeMetricToFile) {
                                // write object to json
                                writeMetricObjectToFile(objects);
                            }
                            // clear the reference to improve gc efficiency.
                            timeSequenced.remove(time);
                        }

                    }
                }
            } catch (Throwable e) {
                logger.error("write metrics data error!", e);
            } finally {
                logDescriptionManager.setLastCollectionTime(level, endTime);
                lastTimestamp.put(level, endTime);
            }
            // clear the reference to improve gc efficiency.
            it.remove();
        }
    }

    public void writeMetricObjectToFile(List<MetricObject> metricObjects) {
        if (null == fileAppender) {
            return;
        }
        try {
            for (MetricObject metric : metricObjects) {
                MetricObject copied = MetricObject.named(metric.getMetric())
                        .withInterval(metricsReportPeriodConfig.period(metric.getMetricLevel()))
                        .withLevel(metric.getMetricLevel())
                        .withTags(merge(metric.getTags(), "appName", appName))
                        .withMeterName(metric.getMeterName())
                        .withTimestamp(metric.getTimestamp())
                        .withType(metric.getMetricType())
                        .withValue(metric.getValue()).build();
                fileAppender.append(metricFormat.formatToBytes(copied));
                fileAppender.append(DEFAULT_DELIMITER_BYTES);
            }
            fileAppender.flush();
        } catch (Throwable e) {
            logger.error("write metrics data error!", e);
        }
    }

    private void fillLastUpdateTime() {
        long timestamp = System.currentTimeMillis();
        for (MetricLevel level : MetricLevel.values()) {
            int interval = metricsReportPeriodConfig.period(level) * 1000;
            lastTimestamp.put(level, (timestamp / interval - 1) * interval);
        }
    }

    private Map<String, String> merge(Map<String, String> tags, String extraKey, String extraValue) {
        Map<String, String> result = new HashMap<String, String>(tags);
        result.put(extraKey, extraValue);
        return result;
    }

    public void setAdvancedMetricsReport(boolean advancedMetricsReport) {
        this.advancedMetricsReport = advancedMetricsReport;
    }

    public void setWriteMetricToFile(boolean writeMetricToFile) {
        this.writeMetricToFile = writeMetricToFile;
    }
}
