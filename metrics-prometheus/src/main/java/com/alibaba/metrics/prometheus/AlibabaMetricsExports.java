package com.alibaba.metrics.prometheus;

import com.alibaba.metrics.BucketCounter;
import com.alibaba.metrics.ClusterHistogram;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Histogram;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.Snapshot;
import com.alibaba.metrics.Timer;
import com.alibaba.metrics.prometheus.samplebuilder.DefaultSampleBuilder;
import com.alibaba.metrics.prometheus.samplebuilder.SampleBuilder;
import io.prometheus.client.Collector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AlibabaMetricsExports extends Collector {

    private SampleBuilder sampleBuilder;


    public AlibabaMetricsExports() {
        this.sampleBuilder = new DefaultSampleBuilder();
    }

    @Override
    public List<MetricFamilySamples> collect() {
        List<String> groups = MetricManager.getIMetricManager().listMetricGroups();
        List<MetricFamilySamples> metricFamilySamples = new ArrayList<MetricFamilySamples>();
        for (String group : groups) {
            MetricRegistry metricRegistry = MetricManager.getIMetricManager().getMetricRegistryByGroup(group);
            for (Map.Entry<MetricName, Counter> entry : metricRegistry.getCounters().entrySet()) {
                metricFamilySamples.add(fromCounter(entry.getKey(), entry.getValue()));
            }
            for (Map.Entry<MetricName, Meter> entry : metricRegistry.getMeters().entrySet()) {
                metricFamilySamples.add(fromMeter(entry.getKey(), entry.getValue()));
            }
            for (Map.Entry<MetricName, Gauge> entry : metricRegistry.getGauges().entrySet()) {
                metricFamilySamples.add(fromGauge(entry.getKey(), entry.getValue()));
            }
            for (Map.Entry<MetricName, Timer> entry : metricRegistry.getTimers().entrySet()) {
                metricFamilySamples.add(fromTimer(entry.getKey(), entry.getValue()));
            }
            for (Map.Entry<MetricName, Histogram> entry : metricRegistry.getHistograms().entrySet()) {
                metricFamilySamples.add(fromHistogram(entry.getKey(), entry.getValue()));
            }
            for (Map.Entry<MetricName, ClusterHistogram> entry : metricRegistry.getClusterHistograms().entrySet()) {
                metricFamilySamples.add(fromClusterHistogram(entry.getKey(), entry.getValue()));
            }
            for (Map.Entry<MetricName, Compass> entry : metricRegistry.getCompasses().entrySet()) {
                metricFamilySamples.add(fromCompass(entry.getKey(), entry.getValue()));
            }
            for (Map.Entry<MetricName, FastCompass> entry : metricRegistry.getFastCompasses().entrySet()) {
                metricFamilySamples.add(fromFastCompass(entry.getKey(), entry.getValue()));
            }
        }
        return metricFamilySamples;
    }

    public MetricFamilySamples fromCounter(MetricName metricName, Counter counter) {
        MetricFamilySamples.Sample sample = sampleBuilder.createSample(metricName, "", new ArrayList<String>(), new ArrayList<String>(), new Long(counter.getCount()).doubleValue());
        return new MetricFamilySamples(sample.name, Type.COUNTER, getHelpMessage(metricName.getKey(), counter), Arrays.asList(sample));
    }

    public MetricFamilySamples fromGauge(MetricName metricName, Gauge gauge) {
        Object o = gauge.getValue();
        double value;
        if (o instanceof Number) {
            value = ((Number) o).doubleValue();
        } else if (o instanceof Boolean) {
            value = ((Boolean) o) ? 1: 0;
        } else {
            return null;
        }
        MetricFamilySamples.Sample sample = sampleBuilder.createSample(metricName, "", new ArrayList<String>(), new ArrayList<String>(), value);
        return new MetricFamilySamples(sample.name, Type.GAUGE, getHelpMessage(metricName.getKey(), gauge), Arrays.asList
                (sample));
    }

    public MetricFamilySamples fromTimer(MetricName metricName, Timer timer) {
        return fromSnapshotAndCount(metricName, timer.getSnapshot(), timer.getCount(),
                1.0D / TimeUnit.MILLISECONDS.toNanos(1L), getHelpMessage(metricName.getKey(), timer));
    }

    public MetricFamilySamples fromMeter(MetricName metricName, Meter meter) {
        List<MetricFamilySamples.Sample> samples = Arrays.asList(
                sampleBuilder.createSample(metricName, "_total", Collections.EMPTY_LIST, Collections.EMPTY_LIST, meter.getCount()),
                sampleBuilder.createSample(metricName, "_m1", Collections.EMPTY_LIST, Collections.EMPTY_LIST, meter.getOneMinuteRate()),
                sampleBuilder.createSample(metricName, "_m5", Collections.EMPTY_LIST, Collections.EMPTY_LIST, meter.getFiveMinuteRate()),
                sampleBuilder.createSample(metricName, "_m15", Collections.EMPTY_LIST, Collections.EMPTY_LIST, meter.getFifteenMinuteRate())
        );
        return new MetricFamilySamples(samples.get(0).name, Type.COUNTER, getHelpMessage(metricName.getKey(), meter), samples);
    }

    public MetricFamilySamples fromHistogram(MetricName metricName, Histogram histogram) {
        return fromSnapshotAndCount(metricName, histogram.getSnapshot(), histogram.getCount(), 1.0,
                getHelpMessage(metricName.getKey(), histogram));
    }

    public MetricFamilySamples fromClusterHistogram(MetricName metricName, ClusterHistogram clusterHistogram) {
        List<MetricFamilySamples.Sample> samples = new ArrayList<MetricFamilySamples.Sample>();
        Map<Long, Map<Long, Long>> buckets = clusterHistogram.getBucketValues(System.currentTimeMillis());
        for (Map.Entry<Long, Map<Long, Long>> entry : buckets.entrySet()) {
            String suffix = "_cluster_percentile";
            Map<Long, Long> bucket = entry.getValue();
            for (Map.Entry<Long, Long> entry1 : bucket.entrySet()) {
                samples.add(sampleBuilder.createSample(metricName, suffix, Arrays.asList("bucket"), Arrays.asList(entry1.getKey().toString()), entry1.getValue()));
            }
        }
        return new MetricFamilySamples(samples.get(0).name, Type.HISTOGRAM, getHelpMessage(metricName.getKey(), clusterHistogram), samples);
    }

    public MetricFamilySamples fromCompass(MetricName metricName, Compass compass) {
        long lastUpdateTime = compass.lastUpdateTime();
        int bucketInterval = compass.getInstantCountInterval();
        long start = getNormalizedStartTime(lastUpdateTime, bucketInterval);
        BucketCounter successCounter = compass.getBucketSuccessCount();
        List<MetricFamilySamples.Sample> samples = new ArrayList<MetricFamilySamples.Sample>();
        for (Map.Entry<String, BucketCounter> entry : compass.getErrorCodeCounts().entrySet()) {
            String tag = entry.getKey();
            if (entry.getValue().getBucketCounts().get(start) != null) {
                samples.add(sampleBuilder.createSample(metricName, "_bucket_count", Arrays.asList("category"), Arrays.asList(tag),
                        entry.getValue().getBucketCounts().get(start)));
            }
        }
        for (Map.Entry<String, BucketCounter> entry : compass.getAddonCounts().entrySet()) {
            String tag = entry.getKey();
            if (entry.getValue().getBucketCounts().get(start) != null) {
                samples.add(sampleBuilder.createSample(metricName, "_bucket_count", Arrays.asList("category"), Arrays.asList(tag),
                        entry.getValue().getBucketCounts().get(start)));
            }
        }
        if (successCounter.getBucketCounts().get(start) != null) {
            samples.add(sampleBuilder.createSample(metricName, "_bucket_count", Arrays.asList("category"), Arrays.asList("success"),
                    successCounter.getBucketCounts().get(start)));

        }
        return new MetricFamilySamples(samples.get(0).name, Type.COUNTER, getHelpMessage(metricName.getKey(), compass), samples);
    }

    public MetricFamilySamples fromFastCompass(MetricName metricName, FastCompass fastCompass) {
        long lastUpdateTime = fastCompass.lastUpdateTime();
        int bucketInterval = fastCompass.getBucketInterval();
        long start = getNormalizedStartTime(lastUpdateTime, bucketInterval);
        List<MetricFamilySamples.Sample> samples = new ArrayList<MetricFamilySamples.Sample>();
        Map<String, Map<Long, Long>> countPerCategory = fastCompass.getMethodCountPerCategory(start);
        for (Map.Entry<String, Map<Long, Long>> entry: countPerCategory.entrySet()) {
            if (entry.getValue().containsKey(start)) {
                String tag = entry.getKey();
                long count = entry.getValue().get(start);
                samples.add(sampleBuilder.createSample(metricName, "_bucket_count", Arrays.asList("category"), Arrays.asList(tag), count));
            }
        }
        for (Map.Entry<String, Map<Long, Long>> entry: fastCompass.getMethodRtPerCategory(start).entrySet()) {
            if (entry.getValue().containsKey(start)) {
                String tag = entry.getKey();
                long rt = entry.getValue().get(start);
                samples.add(sampleBuilder.createSample(metricName, "_bucket_sum", Arrays.asList("category"), Arrays.asList(tag), rt));
            }
        }
        return new MetricFamilySamples(samples.get(0).name, Type.COUNTER, getHelpMessage(metricName.getKey(), fastCompass),
                samples);
    }

    private MetricFamilySamples fromSnapshotAndCount(MetricName metricName, Snapshot snapshot, long count, double factor, String helpMessage) {
        List<MetricFamilySamples.Sample> samples = Arrays.asList(
                sampleBuilder.createSample(metricName, "", Arrays.asList("quantile"), Arrays.asList("0.5"), snapshot.getMedian() * factor),
                sampleBuilder.createSample(metricName, "", Arrays.asList("quantile"), Arrays.asList("0.75"), snapshot.get75thPercentile() * factor),
                sampleBuilder.createSample(metricName, "", Arrays.asList("quantile"), Arrays.asList("0.95"), snapshot.get95thPercentile() * factor),
                sampleBuilder.createSample(metricName, "", Arrays.asList("quantile"), Arrays.asList("0.98"), snapshot.get98thPercentile() * factor),
                sampleBuilder.createSample(metricName, "", Arrays.asList("quantile"), Arrays.asList("0.99"), snapshot.get99thPercentile() * factor),
                sampleBuilder.createSample(metricName, "", Arrays.asList("quantile"), Arrays.asList("0.999"), snapshot.get999thPercentile() * factor),
                sampleBuilder.createSample(metricName, "_count", new ArrayList<String>(), new ArrayList<String>(), count)
        );
        return new MetricFamilySamples(samples.get(0).name, Type.SUMMARY, helpMessage, samples);
    }

    private String getHelpMessage(String metricName, Metric metric) {
        return String.format("Generated from dubbo metric import (metric=%s, type=%s)",
                metricName, metric.getClass().getName());
    }

    private long getNormalizedStartTime(long current, int interval) {
        return (TimeUnit.MILLISECONDS.toSeconds(current) - interval) / interval * interval * 1000;
    }

}
