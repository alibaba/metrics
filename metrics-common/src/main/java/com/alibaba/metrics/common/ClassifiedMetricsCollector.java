package com.alibaba.metrics.common;

import com.alibaba.metrics.BucketCounter;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Histogram;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.Snapshot;
import com.alibaba.metrics.Timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ClassifiedMetricsCollector extends MetricsCollector {

    /** fastcompass实现中的数字分隔偏移量，后半段数字所占位数 */
    private static final int FASTCOMPASS_COUNT_OFFSET = 38;
    private static final long FASTCOMPASS_MASK = (1L << FASTCOMPASS_COUNT_OFFSET) - 1;

    private Map<MetricLevel, Map<Long, List<MetricObject>>> metrics;

    /** 上一次收集的时间戳 */
    private Map<MetricLevel, Long> lastTimestamp = new HashMap<MetricLevel, Long>();

    private static final int CRITICAL_MAX_SAMPLE_NUM = 10;
    private static final int GENERAL_MAX_SAMPLE_NUM = 2;

    private int totalSize = 0;
    private int MAX_SIZE = MAX_COLLECT_NUM * 5;

    private boolean advancedMetricsReport = false;

    /** 度量器名称 */
    private static String COUNTER_NAME = "Counter";
    private static String GAUGE_NAME = "Gauge";
    private static String METER_NAME = "Meter";
    private static String HISTOGRAM_NAME = "Histogram";
    private static String TIMER_NAME = "Timer";
    private static String COMPASS_NAME = "Compass";
    private static String FASTCOMPASS_NAME = "FastCompass";

    ClassifiedMetricsCollector(Map<String, String> globalTags, double rateFactor, double durationFactor,
            MetricFilter filter) {
        super(globalTags, rateFactor, durationFactor, filter);
        metrics = new HashMap<MetricLevel, Map<Long, List<MetricObject>>>();
    }

    @Override
    public void collect(MetricName name, Counter counter, long timestamp) {

        int interval = metricsCollectPeriodConfig.period(name.getMetricLevel()) * 1000;

        long startTime = lastTimestamp.get(name.getMetricLevel()) + interval;
        long endTime = (timestamp / interval - 1) * interval;

        startTime = adjustStartTime(startTime, endTime, interval, name.getMetricLevel());

        long guageTimestamp = startTime;

        if (counter instanceof BucketCounter) {

            Map<Long, Long> totalCounts = ((BucketCounter) counter).getBucketCounts(startTime);

            //String suffix = "s" + ((BucketCounter) counter).getBucketInterval() + "_count";

            for (long time = startTime; time <= endTime; time = time + interval) {

                long metricValue = 0;

                if (totalCounts.containsKey(time)) {
                    metricValue = totalCounts.get(time);
                }

                this.addMetric(name, "bucket_count", metricValue, time, MetricObject.MetricType.DELTA, COUNTER_NAME);
            }

        }

        String normalizedName = name.getKey().endsWith("count") ? name.getKey() : name.resolve("count").getKey();

        MetricObject metricObject = MetricObject.named(normalizedName).withTimestamp(guageTimestamp)
                .withType(MetricObject.MetricType.COUNTER).withValue(counter.getCount())
                .withLevel(name.getMetricLevel()).withTags(merge(globalTags, name.getTags()))
                .withMeterName(COUNTER_NAME).build();

        this.addMetric(metricObject);

    }

    @Override
    public void collect(MetricName name, Gauge gauge, long timestamp) {

        int interval = metricsCollectPeriodConfig.period(name.getMetricLevel()) * 1000;

        long startTime = lastTimestamp.get(name.getMetricLevel()) + interval;
        long endTime = (timestamp / interval - 1) * interval;

        startTime = adjustStartTime(startTime, endTime, interval, name.getMetricLevel());

        long guageTimestamp = startTime;

        MetricObject metricObject = MetricObject.named(name.getKey()).withValue(gauge.getValue())
                .withTimestamp(guageTimestamp).withType(MetricObject.MetricType.GAUGE).withLevel(name.getMetricLevel())
                .withTags(merge(globalTags, name.getTags())).withMeterName(GAUGE_NAME).build();

        this.addMetric(metricObject);
    }

    @Override
    public void collect(MetricName name, Meter meter, long timestamp) {

        Map<Long, Long> totalCounts = meter.getInstantCount(lastTimestamp.get(name.getMetricLevel()));

        int interval = metricsCollectPeriodConfig.period(name.getMetricLevel()) * 1000;

        long startTime = lastTimestamp.get(name.getMetricLevel()) + interval;
        long endTime = (timestamp / interval - 1) * interval;

        startTime = adjustStartTime(startTime, endTime, interval, name.getMetricLevel());

        long guageTimestamp = startTime;

        for (long time = startTime; time <= endTime; time = time + interval) {

            long metricValue = 0;

            if (totalCounts.containsKey(time)) {
                metricValue = totalCounts.get(time);
            }

            this.addMetric(name, "bucket_count", metricValue, time, MetricObject.MetricType.DELTA, METER_NAME);
        }

        this.addMetric(name, "count", meter.getCount(), guageTimestamp, MetricObject.MetricType.COUNTER, METER_NAME);
        this.addMetric(name, "m1", convertRate(meter.getOneMinuteRate()), guageTimestamp, MetricObject.MetricType.GAUGE,
                METER_NAME);

        if (advancedMetricsReport) {
            this.addMetric(name, "m5", convertRate(meter.getFiveMinuteRate()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, METER_NAME);
            this.addMetric(name, "m15", convertRate(meter.getFifteenMinuteRate()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, METER_NAME);
        }

    }

    @Override
    public void collect(MetricName name, Histogram histogram, long timestamp) {

        final Snapshot snapshot = histogram.getSnapshot();

        int interval = metricsCollectPeriodConfig.period(name.getMetricLevel()) * 1000;

        long startTime = lastTimestamp.get(name.getMetricLevel()) + interval;
        long endTime = (timestamp / interval - 1) * interval;

        startTime = adjustStartTime(startTime, endTime, interval, name.getMetricLevel());

        long guageTimestamp = startTime;

        this.addMetric(name, "mean", convertDuration(snapshot.getMean()), guageTimestamp, MetricObject.MetricType.GAUGE,
                HISTOGRAM_NAME);

        if (advancedMetricsReport) {
            this.addMetric(name, "max", snapshot.getMax(), guageTimestamp, MetricObject.MetricType.GAUGE,
                    HISTOGRAM_NAME);
            this.addMetric(name, "min", snapshot.getMin(), guageTimestamp, MetricObject.MetricType.GAUGE,
                    HISTOGRAM_NAME);
            this.addMetric(name, "stddev", snapshot.getStdDev(), guageTimestamp, MetricObject.MetricType.GAUGE,
                    HISTOGRAM_NAME);
            this.addMetric(name, "median", snapshot.getMedian(), guageTimestamp, MetricObject.MetricType.GAUGE,
                    HISTOGRAM_NAME);
            this.addMetric(name, "p75", snapshot.get75thPercentile(), guageTimestamp, MetricObject.MetricType.GAUGE,
                    HISTOGRAM_NAME);
            this.addMetric(name, "p95", snapshot.get95thPercentile(), guageTimestamp, MetricObject.MetricType.GAUGE,
                    HISTOGRAM_NAME);
            this.addMetric(name, "p99", snapshot.get99thPercentile(), guageTimestamp, MetricObject.MetricType.GAUGE,
                    HISTOGRAM_NAME);
        }

    }

    @Override
    public void collect(MetricName name, Timer timer, long timestamp) {

        final Snapshot snapshot = timer.getSnapshot();
        Map<Long, Long> totalCounts = timer.getInstantCount(lastTimestamp.get(name.getMetricLevel()));

        int interval = metricsCollectPeriodConfig.period(name.getMetricLevel()) * 1000;

        long startTime = lastTimestamp.get(name.getMetricLevel()) + interval;
        long endTime = (timestamp / interval - 1) * interval;

        startTime = adjustStartTime(startTime, endTime, interval, name.getMetricLevel());
        long guageTimestamp = startTime;

        for (long time = startTime; time <= endTime; time = time + interval) {

            long metricValue = 0;

            if (totalCounts.containsKey(time)) {
                metricValue = totalCounts.get(time);
            }

            this.addMetric(name, "bucket_count", metricValue, time, MetricObject.MetricType.DELTA, TIMER_NAME);
        }

        this.addMetric(name, "count", timer.getCount(), guageTimestamp, MetricObject.MetricType.COUNTER, TIMER_NAME);

        this.addMetric(name, "m1", convertRate(timer.getOneMinuteRate()), guageTimestamp, MetricObject.MetricType.GAUGE,
                TIMER_NAME);
        this.addMetric(name, "mean", convertDuration(snapshot.getMean()), guageTimestamp, MetricObject.MetricType.GAUGE,
                TIMER_NAME);

        if (advancedMetricsReport) {

            this.addMetric(name, "m5", convertRate(timer.getFiveMinuteRate()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, TIMER_NAME);
            this.addMetric(name, "m15", convertRate(timer.getFifteenMinuteRate()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, TIMER_NAME);
            this.addMetric(name, "max", convertDuration(snapshot.getMax()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, TIMER_NAME);
            this.addMetric(name, "min", convertDuration(snapshot.getMin()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, TIMER_NAME);
            this.addMetric(name, "stddev", convertDuration(snapshot.getStdDev()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, TIMER_NAME);
            this.addMetric(name, "median", convertDuration(snapshot.getMedian()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, TIMER_NAME);
            this.addMetric(name, "p75", convertDuration(snapshot.get75thPercentile()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, TIMER_NAME);
            this.addMetric(name, "p95", convertDuration(snapshot.get95thPercentile()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, TIMER_NAME);
            this.addMetric(name, "p99", convertDuration(snapshot.get99thPercentile()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, TIMER_NAME);

        }

    }

    @Override
    public void collect(MetricName name, Compass compass, long timestamp) {

        final Snapshot snapshot = compass.getSnapshot();

        int interval = metricsCollectPeriodConfig.period(name.getMetricLevel()) * 1000;

        long startTime = lastTimestamp.get(name.getMetricLevel()) + interval;
        long endTime = (timestamp / interval - 1) * interval;

        startTime = adjustStartTime(startTime, endTime, interval, name.getMetricLevel());
        long guageTimestamp = startTime;

        Map<Long, Long> totalCounts = compass.getInstantCount(lastTimestamp.get(name.getMetricLevel()));
        Map<Long, Long> successCounts = compass.getBucketSuccessCount()
                .getBucketCounts(lastTimestamp.get(name.getMetricLevel()));
        Map<String, BucketCounter> errorCounts = compass.getErrorCodeCounts();
        Map<String, BucketCounter> extraCounts = compass.getAddonCounts();

        // count的统计
        // 成功数的统计
        // 成功率的统计

        for (long time = startTime; time <= endTime; time = time + interval) {

            long total = 0;
            long success = 0;
            double successRate = 0;

            if (totalCounts.containsKey(time)) {
                total = totalCounts.get(time);
            }

            if (successCounts.containsKey(time)) {
                success = successCounts.get(time);
            }

            if (total == 0) {
                successRate = 0;
            } else {
                successRate = 1.0d * success / total;
                if (successRate > 1) {
                    successRate = 1.0;
                }
            }

            this.addMetric(name, "success_bucket_count", success, time, MetricObject.MetricType.DELTA, COMPASS_NAME);
            this.addMetric(name, "bucket_count", total, time, MetricObject.MetricType.DELTA, COMPASS_NAME);
            this.addMetric(name, "qps", 1.0d * total / metricsCollectPeriodConfig.period(name.getMetricLevel()), time,
                    MetricObject.MetricType.GAUGE, COMPASS_NAME);
            this.addMetric(name, "success_rate", successRate, time, MetricObject.MetricType.GAUGE, COMPASS_NAME);

        }

        // 错误码的统计
        if (errorCounts != null && errorCounts.size() > 0) {

            for (Entry<String, BucketCounter> entry : errorCounts.entrySet()) {

                String errorCode = entry.getKey();
                BucketCounter metricValue = entry.getValue();
                Map<Long, Long> errors = metricValue.getBucketCounts(lastTimestamp.get(name.getMetricLevel()));

                for (long time = startTime; time <= endTime; time = time + interval) {

                    long metricValue1 = 0;

                    if (errors.containsKey(time)) {
                        metricValue1 = errors.get(time);
                    }

                    this.addMetric(name.tagged("error", errorCode), "error_bucket_count", metricValue1, time,
                            MetricObject.MetricType.DELTA, COMPASS_NAME);
                }

                this.addMetric(name.tagged("error", errorCode), "error.count", metricValue.getCount(), guageTimestamp,
                        MetricObject.MetricType.COUNTER, COMPASS_NAME);

            }

        }

        // 额外增加的扩展字段
        if (extraCounts != null && extraCounts.size() > 0) {

            for (Entry<String, BucketCounter> entry : extraCounts.entrySet()) {

                String extraField = entry.getKey();
                BucketCounter metricValue = entry.getValue();

                Map<Long, Long> extra = metricValue.getBucketCounts(lastTimestamp.get(name.getMetricLevel()));

                String suffix = extraField + "_bucket_count";

                for (long time = startTime; time <= endTime; time = time + interval) {

                    long metricValue1 = 0;

                    if (extra.containsKey(time)) {
                        metricValue1 = extra.get(time);
                    }

                    this.addMetric(name, suffix, metricValue1, time, MetricObject.MetricType.DELTA, COMPASS_NAME);
                }

            }
        }

        this.addMetric(name, "m1", convertRate(compass.getOneMinuteRate()), guageTimestamp,
                MetricObject.MetricType.GAUGE, COMPASS_NAME);
        this.addMetric(name, "mean", convertDuration(snapshot.getMean()), guageTimestamp, MetricObject.MetricType.GAUGE,
                COMPASS_NAME);
        this.addMetric(name, "rt", convertDuration(snapshot.getMean()), guageTimestamp, MetricObject.MetricType.GAUGE,
                COMPASS_NAME);

        if (advancedMetricsReport) {

            this.addMetric(name, "m5", convertRate(compass.getFiveMinuteRate()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, COMPASS_NAME);
            this.addMetric(name, "m15", convertRate(compass.getFifteenMinuteRate()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, COMPASS_NAME);
            this.addMetric(name, "max", convertDuration(snapshot.getMax()), guageTimestamp, MetricObject.MetricType.GAUGE,
                    COMPASS_NAME);
            this.addMetric(name, "min", convertDuration(snapshot.getMin()), guageTimestamp, MetricObject.MetricType.GAUGE,
                    COMPASS_NAME);
            this.addMetric(name, "stddev", convertDuration(snapshot.getStdDev()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, COMPASS_NAME);
            this.addMetric(name, "median", convertDuration(snapshot.getMedian()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, COMPASS_NAME);
            this.addMetric(name, "p75", convertDuration(snapshot.get75thPercentile()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, COMPASS_NAME);
            this.addMetric(name, "p95", convertDuration(snapshot.get95thPercentile()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, COMPASS_NAME);
            this.addMetric(name, "p99", convertDuration(snapshot.get99thPercentile()), guageTimestamp,
                    MetricObject.MetricType.GAUGE, COMPASS_NAME);

        }

    }

    @Override
    public void collect(MetricName name, FastCompass fastCompass, long timestamp) {

        int intervalSeconds = metricsCollectPeriodConfig.period(name.getMetricLevel());
        int interval = metricsCollectPeriodConfig.period(name.getMetricLevel()) * 1000;

        long startTime = lastTimestamp.get(name.getMetricLevel()) + interval;
        long endTime = (timestamp / interval - 1) * interval;

        startTime = adjustStartTime(startTime, endTime, interval, name.getMetricLevel());
        long guageTimestamp = startTime;

        Map<String, Map<Long, Long>> countAndRtPerCategory = fastCompass.getCountAndRtPerCategory(startTime);

        for (long time = startTime; time <= endTime; time = time + interval) {

            long totalCount = 0;
            long totalRt = 0;

            long successCount = 0;
            long hitCount = 0;
            long errorCount = 0;

            Map<Long, Long> hits = countAndRtPerCategory.get("hit");
            Map<Long, Long> successes = countAndRtPerCategory.get("success");
            Map<Long, Long> errors = countAndRtPerCategory.get("error");

            if (hits != null) {
                Long hitNum = hits.get(time);
                if (hitNum != null) {
                    hitCount = getFastCompassCount(hitNum);
                    totalCount = totalCount + hitCount;
                    totalRt = totalRt + getFastCompassRt(hitNum);
                }
            }

            if (successes != null) {
                Long successNum = successes.get(time);
                if (successNum != null) {
                    successCount = getFastCompassCount(successNum);
                    totalCount = totalCount + successCount;
                    totalRt = totalRt + getFastCompassRt(successNum);
                }
            }

            if (errors != null) {
                Long errorNum = errors.get(time);
                if (errorNum != null) {
                    errorCount = getFastCompassCount(errorNum);
                    totalCount = totalCount + errorCount;
                    totalRt = totalRt + getFastCompassRt(errorNum);
                }
            }

            // 成功数为命中数加未命中数
            successCount = successCount + hitCount;

            this.addMetric(name, "bucket_count", totalCount, time, MetricObject.MetricType.DELTA, FASTCOMPASS_NAME);
            this.addMetric(name, "bucket_sum", totalRt, time, MetricObject.MetricType.DELTA, FASTCOMPASS_NAME);

            this.addMetric(name, "hit_bucket_count", hitCount, time, MetricObject.MetricType.DELTA,
                    FASTCOMPASS_NAME);
            this.addMetric(name, "success_bucket_count", successCount, time, MetricObject.MetricType.DELTA,
                    FASTCOMPASS_NAME);
            this.addMetric(name, "error_bucket_count", errorCount, time, MetricObject.MetricType.DELTA,
                    FASTCOMPASS_NAME);

            this.addMetric(name, "qps", rate(totalCount, intervalSeconds), time, MetricObject.MetricType.GAUGE, FASTCOMPASS_NAME);
            this.addMetric(name, "rt", rate(totalRt, totalCount), time, MetricObject.MetricType.GAUGE, FASTCOMPASS_NAME);

            this.addMetric(name, "success_rate", ratio(successCount, totalCount), time, MetricObject.MetricType.GAUGE,
                    FASTCOMPASS_NAME);
            if (hitCount >= 0) {
                // TODO special case for tair
                this.addMetric(name, "hit_rate", ratio(hitCount, successCount), time, MetricObject.MetricType.GAUGE,
                        FASTCOMPASS_NAME);
            }

        }

    }

    public MetricsCollector addMetric(MetricName name, String suffix, Object value, long timestamp,
            MetricObject.MetricType type, String meterName) {
        MetricName fullName = name.resolve(suffix);
        return addMetric(fullName, value, timestamp, type, meterName);
    }

    @Override
    public MetricsCollector addMetric(MetricObject object) {

        if (totalSize >= MAX_SIZE) {
            return this;
        }

        if ((filter == null || filter.matches(MetricName.build(object.getMetric()), null))
                && object.getValue() != null) {

            MetricLevel level = object.getMetricLevel();
            int interval = metricsCollectPeriodConfig.period(level) * 1000;
            long timestamp = object.getTimestamp() / interval * interval;

            Map<Long, List<MetricObject>> metricsLevelUnit = metrics.get(level);

            if (metricsLevelUnit == null) {
                metrics.put(level, new HashMap<Long, List<MetricObject>>());
            }

            List<MetricObject> metricObjects = metrics.get(level).get(timestamp);

            if (metricObjects == null) {
                metrics.get(level).put(timestamp, new ArrayList<MetricObject>());
            }

            List<MetricObject> lst = metrics.get(level).get(timestamp);
            lst.add(object);
            totalSize++;
        }

        return this;
    }

    private MetricsCollector addMetric(MetricName fullName, Object value, long timestamp, MetricObject.MetricType type,
            String meterName) {
        MetricObject obj = MetricObject.named(fullName.getKey()).withType(type).withTimestamp(timestamp)
                .withValue(value).withTags(merge(globalTags, fullName.getTags())).withLevel(fullName.getMetricLevel())
                .withMeterName(meterName).build();
        return addMetric(obj);
    }

    public Map<MetricLevel, Map<Long, List<MetricObject>>> getMetrics() {
        return metrics;
    }

    public void setLastTimestamp(Map<MetricLevel, Long> lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    // 避免starttime太长导致的创建大量对象
    public long adjustStartTime(long startTime, long endTime, int interval, MetricLevel level) {

        int sampleNum = (int) ((endTime - startTime) / interval);
        int maxSampleNum = GENERAL_MAX_SAMPLE_NUM;

        if (level == MetricLevel.CRITICAL) {
            maxSampleNum = CRITICAL_MAX_SAMPLE_NUM;
        }

        if (sampleNum > maxSampleNum) {
            startTime = endTime - interval * maxSampleNum;
        }

        return startTime;
    }

    private long getFastCompassCount(long num) {
        return num >> FASTCOMPASS_COUNT_OFFSET;
    }

    private long getFastCompassRt(long num) {
        return num & FASTCOMPASS_MASK;
    }

    public void setAdvancedMetricsReport(boolean enable) {
        this.advancedMetricsReport = enable;
    }

}
