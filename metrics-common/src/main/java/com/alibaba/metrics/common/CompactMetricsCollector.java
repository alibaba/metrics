package com.alibaba.metrics.common;

import com.alibaba.metrics.BucketCounter;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Histogram;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.Snapshot;
import com.alibaba.metrics.Timer;

import java.util.Map;

/**
 * The collector implementation that only collect minimal metrics
 */
public class CompactMetricsCollector extends MetricsCollector {

    CompactMetricsCollector(Map<String, String> globalTags, double rateFactor,
                            double durationFactor, MetricFilter filter) {
        super(globalTags, rateFactor, durationFactor, filter);
    }

    @Override
    public void collect(MetricName name, Timer timer, long timestamp) {
        final Snapshot snapshot = timer.getSnapshot();
        this.addMetric(name, "count", timer.getCount(), timestamp, MetricObject.MetricType.COUNTER)
                // convert rate
                .addMetric(name, "m1", convertRate(timer.getOneMinuteRate()), timestamp)
                // convert duration
                .addMetric(name, "rt", convertDuration(snapshot.getMean()), timestamp)
                .addMetric(name, "mean", convertDuration(snapshot.getMean()), timestamp);

        // instant count
        addInstantCountMetric(timer.getInstantCount(), name, timer.getInstantCountInterval(), timestamp);
    }

    @Override
    public void collect(MetricName name, Histogram histogram, long timestamp) {
        final Snapshot snapshot = histogram.getSnapshot();
        this.addMetric(name, "mean", snapshot.getMean(), timestamp);
    }

    @Override
    public void collect(MetricName name, Compass compass, long timestamp) {
        final Snapshot snapshot = compass.getSnapshot();
        this.addMetric(name, "count", compass.getCount(), timestamp, MetricObject.MetricType.COUNTER)
                // convert rate
                .addMetric(name, "m1", convertRate(compass.getOneMinuteRate()), timestamp)
                // convert duration
                .addMetric(name, "rt", convertDuration(snapshot.getMean()), timestamp)
                .addMetric(name, "mean", convertDuration(snapshot.getMean()), timestamp);

        // instant count
        addInstantCountMetric(compass.getInstantCount(), name, compass.getInstantCountInterval(), timestamp);

        // instant success count
        addInstantSuccessCount(name, compass, timestamp);

        // instant error code count
        addCompassErrorCode(name, compass, timestamp);

        // instant addon count
        addAddonMetric(name, compass, timestamp);
    }

    @Override
    public void collect(MetricName name, Meter meter, long timestamp) {
        this.addMetric(name, "count", meter.getCount(), timestamp, MetricObject.MetricType.COUNTER)
                // convert rate
                .addMetric(name, "m1", convertRate(meter.getOneMinuteRate()), timestamp);

        // instant count
        addInstantCountMetric(meter.getInstantCount(), name, meter.getInstantCountInterval(), timestamp);
    }

    @Override
    public void collect(MetricName name, Counter counter, long timestamp) {
        MetricName normalizedName = name.getKey().endsWith("count") ? name : name.resolve("count");
        this.addMetric(normalizedName, counter.getCount(), timestamp, MetricObject.MetricType.COUNTER,
                metricsCollectPeriodConfig.period(name.getMetricLevel()));

        if (counter instanceof BucketCounter) {
            int countInterval = ((BucketCounter) counter).getBucketInterval();
            // bucket count
            addInstantCountMetric(((BucketCounter) counter).getBucketCounts(), name, countInterval, timestamp);
        }
    }

    @Override
    public void collect(MetricName name, Gauge gauge, long timestamp) {
        this.addMetric(name, gauge.getValue(), timestamp, MetricObject.MetricType.GAUGE,
                metricsCollectPeriodConfig.period(name.getMetricLevel()));
    }
}
