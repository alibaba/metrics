package com.alibaba.metrics.prometheus;

import com.alibaba.metrics.BucketCounter;
import com.alibaba.metrics.Clock;
import com.alibaba.metrics.ClusterHistogram;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Histogram;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.Metered;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.Snapshot;
import com.alibaba.metrics.Timer;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.prometheus.samplebuilder.DefaultSampleBuilder;
import com.alibaba.metrics.prometheus.samplebuilder.SampleBuilder;
import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AlibabaMetricsExports extends Collector {

    private static final List EMPTY_LIST = Collections.EMPTY_LIST;

    private SampleBuilder sampleBuilder;
    private Clock clock;
    private MetricsCollectPeriodConfig metricsCollectPeriodConfig;

    public AlibabaMetricsExports(Clock clock) {
        this.sampleBuilder = new DefaultSampleBuilder();
        this.clock = clock;
        this.metricsCollectPeriodConfig = new MetricsCollectPeriodConfig();
    }

    @Override
    public List<MetricFamilySamples> collect() {
        List<String> groups = MetricManager.getIMetricManager().listMetricGroups();
        List<MetricFamilySamples> samples = new ArrayList<MetricFamilySamples>();
        long curTs = clock.getTime();
        for (String group : groups) {
            MetricRegistry metricRegistry = MetricManager.getIMetricManager().getMetricRegistryByGroup(group);
            for (Map.Entry<MetricName, Counter> entry : metricRegistry.getCounters().entrySet()) {
                fromCounter(samples, entry.getKey(), entry.getValue(), curTs);
            }
            for (Map.Entry<MetricName, Meter> entry : metricRegistry.getMeters().entrySet()) {
                fromMeter(samples, entry.getKey(), entry.getValue(), curTs, true);
            }
            for (Map.Entry<MetricName, Gauge> entry : metricRegistry.getGauges().entrySet()) {
                fromGauge(samples, entry.getKey(), entry.getValue());
            }
            for (Map.Entry<MetricName, Timer> entry : metricRegistry.getTimers().entrySet()) {
                fromTimer(samples, entry.getKey(), entry.getValue(), curTs);
            }
            for (Map.Entry<MetricName, Histogram> entry : metricRegistry.getHistograms().entrySet()) {
                fromHistogram(samples, entry.getKey(), entry.getValue());
            }
            for (Map.Entry<MetricName, ClusterHistogram> entry : metricRegistry.getClusterHistograms().entrySet()) {
                fromClusterHistogram(samples, entry.getKey(), entry.getValue(), curTs);
            }
            for (Map.Entry<MetricName, Compass> entry : metricRegistry.getCompasses().entrySet()) {
                fromCompass(samples, entry.getKey(), entry.getValue(), curTs);
            }
            for (Map.Entry<MetricName, FastCompass> entry : metricRegistry.getFastCompasses().entrySet()) {
                fromFastCompass(samples, entry.getKey(), entry.getValue(), curTs);
            }
        }
        return samples;
    }

    public void fromCounter(List<MetricFamilySamples> samples, MetricName metricName, Counter counter, long timestamp) {
        samples.add(new CounterMetricFamily(normalizeName(metricName.getKey()) + "_count",
                getHelpMessage(metricName.getKey(), counter), counter.getCount()));
        if (counter instanceof BucketCounter) {
            long start = getNormalizedStartTime(timestamp, ((BucketCounter) counter).getBucketInterval());

            if (((BucketCounter) counter).getBucketCounts().containsKey(start)) {
                samples.add(new GaugeMetricFamily(normalizeName(metricName.getKey()) + "_bucket_count",
                        getHelpMessage(metricName.getKey(), counter), ((BucketCounter) counter).getBucketCounts().get(start)));
            } else {
                samples.add(new GaugeMetricFamily(normalizeName(metricName.getKey()) + "_bucket_count",
                        getHelpMessage(metricName.getKey(), counter), 0));
            }
        }
    }

    public void fromGauge(List<MetricFamilySamples> samples, MetricName metricName, Gauge gauge) {
        Object o = gauge.getValue();
        double value;
        if (o instanceof Number) {
            value = ((Number) o).doubleValue();
        } else if (o instanceof Boolean) {
            value = ((Boolean) o) ? 1: 0;
        } else {
            value = 0;
        }
        samples.add(new GaugeMetricFamily(normalizeName(metricName.getKey()), getHelpMessage(metricName.getKey(), gauge), value));
    }

    public void fromTimer(List<MetricFamilySamples> samples, MetricName metricName, Timer timer, long timestamp) {
        String helpMessage = getHelpMessage(metricName.getKey(), timer);
        fromSnapshot(samples, metricName, timer.getSnapshot(), 1.0d / TimeUnit.MILLISECONDS.toNanos(1L),
                helpMessage);
        fromMeter(samples, metricName, timer, timestamp, true);
    }

    public void fromMeter(List<MetricFamilySamples> samples, MetricName metricName, Metered meter, long timestamp,
                          boolean collectBucketCount) {
        samples.add(new CounterMetricFamily(normalizeName(metricName.getKey()) + "_count",
                getHelpMessage(metricName.getKey(), meter), meter.getCount()));
        samples.add(new GaugeMetricFamily(normalizeName(metricName.getKey()) + "_m1",
                getHelpMessage(metricName.getKey(), meter), meter.getOneMinuteRate()));
        samples.add(new GaugeMetricFamily(normalizeName(metricName.getKey()) + "_m5",
                getHelpMessage(metricName.getKey(), meter), meter.getFiveMinuteRate()));
        samples.add(new GaugeMetricFamily(normalizeName(metricName.getKey()) + "_m15",
                getHelpMessage(metricName.getKey(), meter), meter.getFifteenMinuteRate()));

        if (!collectBucketCount) {
            return;
        }

        long start = getNormalizedStartTime(timestamp, meter.getInstantCountInterval());
        if (meter.getInstantCount().containsKey(start)) {
            samples.add(new GaugeMetricFamily(normalizeName(metricName.getKey()) + "_bucket_count",
                    getHelpMessage(metricName.getKey(), meter), meter.getInstantCount().get(start)));
        } else {
            samples.add(new GaugeMetricFamily(normalizeName(metricName.getKey()) + "_bucket_count",
                    getHelpMessage(metricName.getKey(), meter), 0));
        }
    }

    public void fromHistogram(List<MetricFamilySamples> samples, MetricName metricName, Histogram histogram) {
        String helpMessage = getHelpMessage(metricName.getKey(), histogram);
        fromSnapshot(samples, metricName, histogram.getSnapshot(), 1.0d,
                helpMessage);
        samples.add(new CounterMetricFamily(normalizeName(metricName.getKey()) + "_count", helpMessage, histogram.getCount()));
    }

    public void fromClusterHistogram(List<MetricFamilySamples> samples, MetricName metricName,
                                     ClusterHistogram clusterHistogram, long timestamp) {
        List<MetricFamilySamples.Sample> bucketSamples = new ArrayList<MetricFamilySamples.Sample>();
        long start = getNormalizedStartTime(timestamp, metricsCollectPeriodConfig.period(metricName.getMetricLevel()));
        Map<Long, Map<Long, Long>> bucketValues = clusterHistogram.getBucketValues(start);
        long[] buckets = clusterHistogram.getBuckets();

        if (bucketValues.containsKey(start)) {
            Map<Long, Long> bucketAndValues = bucketValues.get(start);
            for (long bucket: buckets) {
                bucketSamples.add(sampleBuilder.createSample(metricName, "_cluster_percentile",
                        Arrays.asList("bucket"), Arrays.asList(bucket == Long.MAX_VALUE ? "+Inf" : Long.toString(bucket)),
                        bucketAndValues.containsKey(bucket) ? bucketAndValues.get(bucket) : 0L));
            }
        } else {
            bucketSamples.add(sampleBuilder.createSample(metricName, "_cluster_percentile",
                    EMPTY_LIST, EMPTY_LIST, 0L));
        }

        samples.add(new MetricFamilySamples(bucketSamples.get(0).name, Type.HISTOGRAM,
                    getHelpMessage(metricName.getKey(), clusterHistogram), bucketSamples));
    }

    public void fromCompass(List<MetricFamilySamples> samples, MetricName metricName, Compass compass, long timestamp) {
        String helpMessage = getHelpMessage(metricName.getKey(), compass);
        fromSnapshot(samples, metricName, compass.getSnapshot(), 1.0d / TimeUnit.MILLISECONDS.toNanos(1L),
                helpMessage);
        fromMeter(samples, metricName, compass, timestamp, false);

        List<MetricFamilySamples.Sample> bucketSamples = new ArrayList<MetricFamilySamples.Sample>();
        int bucketInterval = compass.getInstantCountInterval();
        long start = getNormalizedStartTime(timestamp, bucketInterval);

        // success count
        BucketCounter successCounter = compass.getBucketSuccessCount();

        if (successCounter.getBucketCounts().containsKey(start)) {
            bucketSamples.add(sampleBuilder.createSample(metricName, "_bucket_count",
                    Arrays.asList("category"), Arrays.asList("success"),
                    successCounter.getBucketCounts().get(start)));
        } else {
            bucketSamples.add(sampleBuilder.createSample(metricName, "_bucket_count",
                    Arrays.asList("category"), Arrays.asList("success"), 0));
        }

        // error count
        for (Map.Entry<String, BucketCounter> entry : compass.getErrorCodeCounts().entrySet()) {
            String tag = entry.getKey();
            // error bucket count
            if (entry.getValue().getBucketCounts().containsKey(start)) {
                bucketSamples.add(sampleBuilder.createSample(metricName, "_bucket_count",
                        Arrays.asList("category"), Arrays.asList(tag),
                        entry.getValue().getBucketCounts().get(start)));
            } else {
                bucketSamples.add(sampleBuilder.createSample(metricName, "_bucket_count",
                        Arrays.asList("category"), Arrays.asList(tag), 0));
            }
        }

        samples.add(new MetricFamilySamples(bucketSamples.get(0).name, Type.GAUGE, helpMessage, bucketSamples));

        List<MetricFamilySamples.Sample> addonSamples = new ArrayList<MetricFamilySamples.Sample>();

        // addon count
        for (Map.Entry<String, BucketCounter> entry : compass.getAddonCounts().entrySet()) {
            String tag = entry.getKey();
            if (entry.getValue().getBucketCounts().containsKey(start)) {
                addonSamples.add(sampleBuilder.createSample(metricName, "_addon_bucket_count",
                        Arrays.asList("addon"), Arrays.asList(tag),
                        entry.getValue().getBucketCounts().get(start)));
            } else {
                addonSamples.add(sampleBuilder.createSample(metricName, "_addon_bucket_count",
                        Arrays.asList("addon"), Arrays.asList(tag), 0));
            }
        }

        samples.add(new MetricFamilySamples(addonSamples.get(0).name, Type.GAUGE, helpMessage, addonSamples));
    }

    public void fromFastCompass(List<MetricFamilySamples> samples, MetricName metricName,
                                FastCompass fastCompass, long timestamp) {
        int bucketInterval = fastCompass.getBucketInterval();
        long start = getNormalizedStartTime(timestamp, bucketInterval);
        List<MetricFamilySamples.Sample> bucketSamples = new ArrayList<MetricFamilySamples.Sample>();
        Map<String, Map<Long, Long>> countPerCategory = fastCompass.getMethodCountPerCategory(start);
        for (Map.Entry<String, Map<Long, Long>> entry: countPerCategory.entrySet()) {
            String tag = entry.getKey();
            if (entry.getValue().containsKey(start)) {
                long count = entry.getValue().get(start);
                bucketSamples.add(sampleBuilder.createSample(metricName, "_bucket_count",
                        Arrays.asList("category"), Arrays.asList(tag), count));
            } else {
                bucketSamples.add(sampleBuilder.createSample(metricName, "_bucket_count",
                        Arrays.asList("category"), Arrays.asList(tag), 0));
            }
        }
        for (Map.Entry<String, Map<Long, Long>> entry: fastCompass.getMethodRtPerCategory(start).entrySet()) {
            String tag = entry.getKey();
            if (entry.getValue().containsKey(start)) {
                long rt = entry.getValue().get(start);
                bucketSamples.add(sampleBuilder.createSample(metricName, "_bucket_sum",
                        Arrays.asList("category"), Arrays.asList(tag), rt));
            } else {
                bucketSamples.add(sampleBuilder.createSample(metricName, "_bucket_sum",
                        Arrays.asList("category"), Arrays.asList(tag), 0));
            }
        }

        samples.add(new MetricFamilySamples(bucketSamples.get(0).name, Type.GAUGE,
                    getHelpMessage(metricName.getKey(), fastCompass), bucketSamples));
    }

    private void fromSnapshot(List<MetricFamilySamples> samples, MetricName metricName, Snapshot snapshot,
                              double factor, String helpMessage) {
        List<MetricFamilySamples.Sample> snapshotSamples = Arrays.asList(
                sampleBuilder.createSample(metricName, "_summary", Arrays.asList("quantile"), Arrays.asList("0.5"),
                        snapshot.getMedian() * factor),
                sampleBuilder.createSample(metricName, "_summary", Arrays.asList("quantile"), Arrays.asList("0.75"),
                        snapshot.get75thPercentile() * factor),
                sampleBuilder.createSample(metricName, "_summary", Arrays.asList("quantile"), Arrays.asList("0.95"),
                        snapshot.get95thPercentile() * factor),
                sampleBuilder.createSample(metricName, "_summary", Arrays.asList("quantile"), Arrays.asList("0.99"),
                        snapshot.get99thPercentile() * factor)
        );
        samples.add(new MetricFamilySamples(snapshotSamples.get(0).name, Type.SUMMARY, helpMessage, snapshotSamples));
        samples.add(new GaugeMetricFamily(normalizeName(metricName.getKey() + "_min"), helpMessage, snapshot.getMin() * factor));
        samples.add(new GaugeMetricFamily(normalizeName(metricName.getKey() + "_max"), helpMessage, snapshot.getMax() * factor));
        samples.add(new GaugeMetricFamily(normalizeName(metricName.getKey() + "_mean"), helpMessage, snapshot.getMean() * factor));
        samples.add(new GaugeMetricFamily(normalizeName(metricName.getKey() + "_stddev"), helpMessage, snapshot.getStdDev() * factor));
    }

    private String getHelpMessage(String metricName, Metric metric) {
        return String.format("Generated from Alibaba metrics exporter (metric=%s, type=%s)",
                metricName, metric.getClass().getName());
    }

    private long getNormalizedStartTime(long current, int interval) {
        return (TimeUnit.MILLISECONDS.toSeconds(current) - interval) / interval * interval * 1000;
    }

    private String normalizeName(String name) {
        return Collector.sanitizeMetricName(name);
    }

}
