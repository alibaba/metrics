package com.alibaba.metrics.common;

import com.alibaba.metrics.BucketCounter;
import com.alibaba.metrics.ClusterHistogram;
import com.alibaba.metrics.Collector;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.alibaba.metrics.Constants.NOT_AVAILABLE;

public abstract class MetricsCollector implements Collector {

    /**
     * 控制收集的MetricsObject数量上限
     */
    protected static final int MAX_COLLECT_NUM =
            Integer.getInteger("com.alibaba.metrics.collector.maxCollectNumber", 10000);

    protected final List<MetricObject> metrics;
    protected Map<String, String> globalTags;
    /**
     * 每一个统计级别对应的统计周期
     */
    protected MetricsCollectPeriodConfig metricsCollectPeriodConfig;
    protected double rateFactor;
    protected double durationFactor;
    private boolean collectNAValue;
    private double notAvailable;

    /**
     * Use this filer to filter out any metric object that is not needed.
     */
    protected MetricFilter filter;

    MetricsCollector(Map<String, String> globalTags, double rateFactor,
                     double durationFactor, MetricFilter filter) {
        this(globalTags, rateFactor, durationFactor, filter, false);
    }

    /**
     * No public access to this constructor,
     * use {@link MetricsCollectorFactory} to create the {@link NormalMetricsCollector} instance
     * @param globalTags
     * @param rateFactor
     * @param durationFactor
     * @param filter
     */
    MetricsCollector(Map<String, String> globalTags, double rateFactor,
                     double durationFactor, MetricFilter filter, boolean collectNAValue) {
        this.metrics = new ArrayList<MetricObject>();
        this.globalTags = globalTags;
        this.rateFactor = rateFactor;
        this.durationFactor = durationFactor;
        this.filter = filter;
        this.collectNAValue = collectNAValue;
        this.notAvailable = NOT_AVAILABLE * durationFactor;
        this.metricsCollectPeriodConfig = new MetricsCollectPeriodConfig();
    }

    public MetricsCollector addMetric(MetricName name, String suffix, Object value, long timestamp) {
        return addMetric(name, suffix, value, timestamp, MetricObject.MetricType.GAUGE);
    }

    public MetricsCollector addMetric(MetricName name, String suffix, Object value, long timestamp,
                                      MetricObject.MetricType type) {
        MetricName fullName = name.resolve(suffix);
        return addMetric(fullName, value, timestamp, type, metricsCollectPeriodConfig.period(fullName.getMetricLevel()));
    }

    public MetricsCollector addMetric(MetricName name, String suffix, Object value, long timestamp,
                                      MetricObject.MetricType type, int interval) {
        MetricName fullName = name.resolve(suffix);
        return addMetric(fullName, value, timestamp, type, interval);
    }

    public MetricsCollector addMetric(MetricObject object) {
        if (this.metrics.size() >= MAX_COLLECT_NUM) {
            // do not collect if exceed MAX_COLLECT_NUM
            return this;
        }

        if (!collectNAValue && object.getValue() instanceof Double
                && (Double) object.getValue() == notAvailable) {
            // do not collect NaN values
            return this;
        }

        if ((filter == null || filter.matches(MetricName.build(object.getMetric()), null))
                && object.getValue() != null) {
            this.metrics.add(object);
        }
        return this;
    }

    public MetricsCollector addMetric(MetricName name, MetricName suffix, Object value, long timestamp,
                                      MetricObject.MetricType type, int interval) {
        MetricName fullName = MetricName.join(name, suffix);
        return addMetric(fullName, value, timestamp, type, interval);
    }

    public MetricsCollector addMetric(MetricName fullName, Object value, long timestamp,
                                      MetricObject.MetricType type, int interval) {
        MetricObject obj = MetricObject.named(fullName.getKey())
                .withType(type)
                .withTimestamp(timestamp)
                .withValue(value)
                .withTags(merge(globalTags, fullName.getTags()))
                .withLevel(fullName.getMetricLevel())
                .withInterval(interval)
                .build();
        return addMetric(obj);
    }

    public List<MetricObject> build() {
        return metrics;
    }

    /**
     * clear all the metrics
     */
    public void clear() {
        this.metrics.clear();
    }


    @Override
    public void collect(MetricName name, FastCompass fastCompass, long timestamp) {

        int bucketInterval = fastCompass.getBucketInterval();

        long start = getNormalizedStartTime(timestamp, bucketInterval);
        long totalCount = 0;
        long totalRt = 0;
        long successCount = 0;
        long hitCount = -1;

        Map<String, Map<Long, Long>> countPerCategory = fastCompass.getMethodCountPerCategory(start);
        for (Map.Entry<String, Map<Long, Long>> entry: countPerCategory.entrySet()) {
            if (entry.getValue().containsKey(start)) {
                this.addMetric(name, entry.getKey() + "_bucket_count", entry.getValue().get(start), start,
                        MetricObject.MetricType.DELTA, bucketInterval);
                totalCount += entry.getValue().get(start);
                if ("success".equals(entry.getKey())) {
                    successCount += entry.getValue().get(start);
                }
                if ("hit".equals(entry.getKey())) {
                    hitCount = entry.getValue().get(start);
                    successCount += entry.getValue().get(start);
                }
            } else {
                this.addMetric(name, entry.getKey() + "_bucket_count", 0L, start,
                        MetricObject.MetricType.DELTA, bucketInterval);
            }
        }
        for (Map.Entry<String, Map<Long, Long>> entry: fastCompass.getMethodRtPerCategory(start).entrySet()) {
            if (entry.getValue().containsKey(start)) {
                totalRt += entry.getValue().get(start);
            }
        }
        this.addMetric(name, "bucket_count", totalCount, start,
                MetricObject.MetricType.DELTA, bucketInterval);
        this.addMetric(name, "bucket_sum", totalRt, start,
                MetricObject.MetricType.DELTA, bucketInterval);
        this.addMetric(name, "qps", rate(totalCount, bucketInterval), start,
                MetricObject.MetricType.GAUGE, bucketInterval);
        this.addMetric(name, "rt", rate(totalRt, totalCount), start,
                MetricObject.MetricType.GAUGE, bucketInterval);
        this.addMetric(name, "success_rate", ratio(successCount, totalCount), start,
                MetricObject.MetricType.GAUGE, bucketInterval);
        if (hitCount >= 0) {
            // TODO special case for tair
            this.addMetric(name, "hit_rate", ratio(hitCount, successCount), start,
                    MetricObject.MetricType.GAUGE, bucketInterval);
        }
    }

    @Override
    public void collect(MetricName name, ClusterHistogram clusterHistogram, long timestamp) {
        long start = getNormalizedStartTime(timestamp, metricsCollectPeriodConfig.period(name.getMetricLevel()));
        Map<Long, Map<Long, Long>> values= clusterHistogram.getBucketValues(start);
        long[] buckets = clusterHistogram.getBuckets();
        if (values.containsKey(start)) {
            Map<Long, Long> bucketAndValues = values.get(start);
            for (long bucket: buckets) {
                this.addMetric(name.tagged("bucket", bucket == Long.MAX_VALUE ? "+Inf" : Long.toString(bucket)),
                        "cluster_percentile", bucketAndValues.containsKey(bucket) ? bucketAndValues.get(bucket) : 0L,
                        start, MetricObject.MetricType.PERCENTILE);
            }
        } else {
            this.addMetric(name, "cluster_percentile", 0L, start, MetricObject.MetricType.PERCENTILE);
        }
    }

    protected Map<String, String> merge(Map<String, String> map1, Map<String, String> map2) {
        Map<String, String> result = new HashMap<String, String>();
        result.putAll(map1);
        result.putAll(map2);
        return result;
    }

    protected double convertRate(double rate) {
        return rate * rateFactor;
    }

    protected double convertDuration(double duration) {
        return duration * durationFactor;
    }

    protected void addInstantCountMetric(Map<Long, Long> instantCount, MetricName name, int countInterval, long timestamp) {
        long start = getNormalizedStartTime(timestamp, countInterval);
        // only the latest instant rate, for compatibility
        if (instantCount.containsKey(start)) {
            this.addMetric(name, "bucket_count", instantCount.get(start), start,
                    MetricObject.MetricType.DELTA, countInterval);
            this.addMetric(name, "qps", rate(instantCount.get(start), countInterval),
                    start, MetricObject.MetricType.GAUGE, countInterval);
        } else {
            this.addMetric(name, "bucket_count", 0L, start,
                    MetricObject.MetricType.DELTA, countInterval);
            this.addMetric(name, "qps", 0.0d,
                    start, MetricObject.MetricType.GAUGE, countInterval);
        }
    }

    protected void addCompassErrorCode(MetricName name, Compass compass, long timestamp) {
        int countInterval = compass.getInstantCountInterval();
        long start = getNormalizedStartTime(timestamp, countInterval);
        for (Map.Entry<String, BucketCounter> entry: compass.getErrorCodeCounts().entrySet()) {
            this.addMetric(name, MetricName.build("error.count").tagged("error", entry.getKey()),
                    entry.getValue().getCount(), timestamp, MetricObject.MetricType.COUNTER,
                    metricsCollectPeriodConfig.period(name.getMetricLevel()));
            MetricName errorName = MetricName.build("error_bucket_count").tagged("error", entry.getKey());
            Map<Long, Long> errorCodeBucket = entry.getValue().getBucketCounts();
            if (errorCodeBucket.containsKey(start)) {
                this.addMetric(name, errorName, errorCodeBucket.get(start), start,
                        MetricObject.MetricType.DELTA, countInterval);
            } else {
                this.addMetric(name, errorName, 0L, start,
                        MetricObject.MetricType.DELTA, countInterval);
            }
        }
    }

    protected void addAddonMetric(MetricName name, Compass compass, long timestamp) {
        int countInterval = compass.getInstantCountInterval();
        long start = getNormalizedStartTime(timestamp, countInterval);
        for (Map.Entry<String, BucketCounter> entry: compass.getAddonCounts().entrySet()) {
            MetricName addonName = MetricName.build(entry.getKey(), "count");
            this.addMetric(name, addonName, entry.getValue().getCount(), timestamp,
                    MetricObject.MetricType.COUNTER, metricsCollectPeriodConfig.period(name.getMetricLevel()));
            MetricName addonBucketName = MetricName.build(entry.getKey() + "_bucket_count");
            Map<Long, Long> addOnBucket = entry.getValue().getBucketCounts();
            if (addOnBucket.containsKey(start)) {
                this.addMetric(name, addonBucketName, addOnBucket.get(start), start,
                        MetricObject.MetricType.DELTA, countInterval);
                Long successTotal = compass.getBucketSuccessCount().getBucketCounts().get(start);
                if (successTotal == null) {
                    // 当addon的统计桶被更新，然而总次数的更新被延迟到了下一个桶时会出现这种情况，
                    // 这种情况下认为addon == successTotal
                    successTotal = addOnBucket.get(start);
                }
                this.addMetric(name, entry.getKey() + "_rate", ratio(addOnBucket.get(start), successTotal), start,
                        MetricObject.MetricType.GAUGE, compass.getInstantCountInterval());
            } else {
                this.addMetric(name, addonBucketName, 0L, start,
                        MetricObject.MetricType.DELTA, countInterval);
                this.addMetric(name, entry.getKey() + "_rate", 0.0d, start,
                        MetricObject.MetricType.GAUGE, compass.getInstantCountInterval());
            }
        }
    }

    protected void addInstantSuccessCount(MetricName name, Compass compass, long timestamp) {
        this.addMetric(name, "success_count", compass.getBucketSuccessCount().getCount(),
                timestamp, MetricObject.MetricType.COUNTER);

        int countInterval = compass.getInstantCountInterval();
        long start = getNormalizedStartTime(timestamp, countInterval);
        Map<Long, Long> successCount = compass.getBucketSuccessCount().getBucketCounts();

        if (successCount.containsKey(start)) {
            this.addMetric(name, "success_bucket_count", successCount.get(start), start,
                    MetricObject.MetricType.DELTA, countInterval);
            Long total = compass.getInstantCount().get(start);
            if (total == null) {
                // 当success的统计桶被更新，然后总次数的更新被延迟到了下一个桶时会出现这种情况，
                // 这种情况下认为success == total
                total = successCount.get(start);
            }
            this.addMetric(name, "success_rate", ratio(successCount.get(start), total), start,
                    MetricObject.MetricType.GAUGE, compass.getInstantCountInterval());
        } else {
            this.addMetric(name, "success_bucket_count", 0L, start,
                    MetricObject.MetricType.DELTA, countInterval);
            this.addMetric(name, "success_rate", 0.0d,
                    start, MetricObject.MetricType.GAUGE, countInterval);
        }
    }

    protected double rate(long data, long interval) {
        if (interval == 0) return 0.0d;
        return 1.0d * data / interval;
    }

    protected double ratio(long data, long total) {
        if (data > total) return 1.0d;
        if (total == 0) return 0.0d;
        return 1.0d * data / total;
    }

    public void setMetricsCollectPeriodConfig(MetricsCollectPeriodConfig metricsCollectPeriodConfig) {
        this.metricsCollectPeriodConfig = metricsCollectPeriodConfig;
    }

    private long getNormalizedStartTime(long current, int interval) {
        return (TimeUnit.MILLISECONDS.toSeconds(current) - interval) / interval * interval * 1000;
    }
}
