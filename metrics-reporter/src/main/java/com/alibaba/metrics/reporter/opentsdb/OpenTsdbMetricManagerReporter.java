/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.metrics.reporter.opentsdb;

import com.alibaba.metrics.Clock;
import com.alibaba.metrics.ClusterHistogram;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Histogram;
import com.alibaba.metrics.IMetricManager;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.Snapshot;
import com.alibaba.metrics.Timer;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.MetricManagerReporter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A reporter which publishes all MetricManager metrics values to a OpenTSDB server.
 *
 *
 */
public class OpenTsdbMetricManagerReporter extends MetricManagerReporter {

    private final OpenTsdb opentsdb;
    private final Clock clock;
    private final String prefix;
    private final Map<String, String> globalTags;

    private final TimeUnit timestampPrecision;

    /**
     * Returns a new {@link Builder} for {@link OpenTsdbReporter}.
     *
     * @param metricManager
     *            the metricManager to report
     * @return a {@link Builder} instance for a {@link OpenTsdbReporter}
     */
    public static Builder forMetricManager(IMetricManager metricManager) {
        return new Builder(metricManager);
    }

    /**
     * A builder for {@link OpenTsdbReporter} instances. Defaults to not using a
     * prefix, using the default clock, converting rates to events/second,
     * converting durations to milliseconds, and not filtering metrics.
     */
    public static class Builder {
        private final IMetricManager metricManager;
        private Clock clock;
        private String prefix;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private MetricsCollectPeriodConfig metricsReportPeriodConfig;
        private Map<String, String> globalTags;
        private int batchSize;

        // 提交到服务器的时间戳的单位，只支持毫秒和秒，默认是秒
        private TimeUnit timestampPrecision = TimeUnit.SECONDS;

        private Builder(IMetricManager metricManager) {
            this.metricManager = metricManager;
            this.clock = Clock.defaultClock();
            this.prefix = null;
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
            this.batchSize = OpenTsdb.DEFAULT_BATCH_SIZE_LIMIT;
        }

        /**
         * Use the given {@link Clock} instance for the time.
         *
         * @param clock
         *            a {@link Clock} instance
         * @return {@code this}
         */
        public Builder withClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        /**
         * Prefix all metric names with the given string.
         *
         * @param prefix
         *            the prefix for all metric names
         * @return {@code this}
         */
        public Builder prefixedWith(String prefix) {
            this.prefix = prefix;
            return this;
        }

        /**
         * Convert rates to the given time unit.
         *
         * @param rateUnit
         *            a unit of time
         * @return {@code this}
         */
        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        /**
         * Convert durations to the given time unit.
         *
         * @param durationUnit
         *            a unit of time
         * @return {@code this}
         */
        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        /**
         * Only report metrics which match the given filter.
         *
         * @param filter
         *            a {@link MetricFilter}
         * @return {@code this}
         */
        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        /**
         *
         * @param metricsReportPeriodConfig
         * @return
         */
        public Builder metricsReportPeriodConfig(MetricsCollectPeriodConfig metricsReportPeriodConfig) {
            this.metricsReportPeriodConfig = metricsReportPeriodConfig;
            return this;
        }

        /**
         * Append tags to all reported metrics
         *
         * @param globalTags
         * @return
         */
        public Builder withGlobalTags(Map<String, String> globalTags) {
            this.globalTags = globalTags;
            return this;
        }

        /**
         * specify number of metrics send in each request
         *
         * @param batchSize
         * @return
         */
        public Builder withBatchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder timestampPrecision(TimeUnit timestampPrecision) {
            if (TimeUnit.SECONDS.equals(timestampPrecision) || TimeUnit.MILLISECONDS.equals(timestampPrecision)) {
                this.timestampPrecision = timestampPrecision;
                return this;
            } else {
                throw new IllegalArgumentException(
                        "timestampPrecision must be TimeUnit.SECONDS or TimeUnit.MILLISECONDS!, do not support: "
                                + timestampPrecision);
            }
        }

        /**
         * Builds a {@link OpenTsdbReporter} with the given properties, sending
         * metrics using the given
         * {@link OpenTsdb} client.
         *
         * @param opentsdb
         *            a {@link OpenTsdb} client
         * @return a {@link OpenTsdbReporter}
         */
        public OpenTsdbMetricManagerReporter build(OpenTsdb opentsdb) {
            opentsdb.setBatchSizeLimit(batchSize);
            if (globalTags == null) {
                globalTags = Collections.emptyMap();
            }
            return new OpenTsdbMetricManagerReporter(metricManager, opentsdb, clock, prefix, rateUnit, timestampPrecision, durationUnit, filter, metricsReportPeriodConfig, globalTags);
        }
    }

    private OpenTsdbMetricManagerReporter(IMetricManager metricManager, OpenTsdb opentsdb, Clock clock, String prefix, TimeUnit rateUnit,
            TimeUnit durationUnit, TimeUnit timestampPrecision, MetricFilter filter, MetricsCollectPeriodConfig metricsReportPeriodConfig, Map<String, String> globalTags) {
        super(metricManager, "opentsdb-reporter", filter, metricsReportPeriodConfig, rateUnit, durationUnit);
        this.opentsdb = opentsdb;
        this.clock = clock;
        this.prefix = prefix;
        this.globalTags = globalTags;

        this.timestampPrecision = timestampPrecision;
    }

    @Override
    public void report(Map<MetricName, Gauge> gauges, Map<MetricName, Counter> counters,
            Map<MetricName, Histogram> histograms, Map<MetricName, Meter> meters,
            Map<MetricName, Timer> timers, Map<MetricName, Compass> compasses, Map<MetricName, FastCompass> fastCompasses, Map<MetricName, ClusterHistogram> clusterHistogrames) {

        long timestamp = clock.getTime();

        if (TimeUnit.MICROSECONDS.equals(timestampPrecision)) {
            timestamp = timestamp / 1000;
        }

        final Set<OpenTsdbMetric> metrics = new HashSet<OpenTsdbMetric>();

        for (Entry<MetricName, Gauge> g : gauges.entrySet()) {
            if (g.getValue().getValue() instanceof Collection && ((Collection) g.getValue().getValue()).isEmpty()) {
                continue;
            }
            metrics.add(buildGauge(g.getKey(), g.getValue(), timestamp));
        }

        for (Entry<MetricName, Counter> entry : counters.entrySet()) {
            metrics.add(buildCounter(entry.getKey(), entry.getValue(), timestamp));
        }

        for (Entry<MetricName, Histogram> entry : histograms.entrySet()) {
            metrics.addAll(buildHistograms(entry.getKey(), entry.getValue(), timestamp));
        }

        for (Entry<MetricName, Meter> entry : meters.entrySet()) {
            metrics.addAll(buildMeters(entry.getKey(), entry.getValue(), timestamp));
        }

        for (Entry<MetricName, Timer> entry : timers.entrySet()) {
            metrics.addAll(buildTimers(entry.getKey(), entry.getValue(), timestamp));
        }

        for (Entry<MetricName, Compass> entry : compasses.entrySet()) {
            metrics.addAll(buildCompass(entry.getKey(), entry.getValue(), timestamp));
        }

        opentsdb.send(metrics);
    }

    private Set<OpenTsdbMetric> buildTimers(MetricName name, Timer timer, long timestamp) {

        final MetricsCollector collector = MetricsCollector.createNew(prefix(name.getKey()),
                merge(globalTags, name.getTags()), timestamp);
        final Snapshot snapshot = timer.getSnapshot();

        return collector.addMetric("count", timer.getCount())
                // convert rate
                .addMetric("m15", convertRate(timer.getFifteenMinuteRate()))
                .addMetric("m5", convertRate(timer.getFiveMinuteRate()))
                .addMetric("m1", convertRate(timer.getOneMinuteRate()))
                .addMetric("mean_rate", convertRate(timer.getMeanRate()))
                // convert duration
                .addMetric("max", convertDuration(snapshot.getMax()))
                .addMetric("min", convertDuration(snapshot.getMin()))
                .addMetric("mean", convertDuration(snapshot.getMean()))
                .addMetric("stddev", convertDuration(snapshot.getStdDev()))
                .addMetric("median", convertDuration(snapshot.getMedian()))
                .addMetric("p75", convertDuration(snapshot.get75thPercentile()))
                .addMetric("p95", convertDuration(snapshot.get95thPercentile()))
                .addMetric("p98", convertDuration(snapshot.get98thPercentile()))
                .addMetric("p99", convertDuration(snapshot.get99thPercentile()))
                .addMetric("p999", convertDuration(snapshot.get999thPercentile())).build();
    }

    private Set<OpenTsdbMetric> buildCompass(MetricName name, Compass compass, long timestamp) {

        final MetricsCollector collector = MetricsCollector.createNew(prefix(name.getKey()),
                merge(globalTags, name.getTags()), timestamp);

        // TODO add build compass logic

        return collector.build();
    }

    private Set<OpenTsdbMetric> buildHistograms(MetricName name, Histogram histogram, long timestamp) {

        final MetricsCollector collector = MetricsCollector.createNew(prefix(name.getKey()),
                merge(globalTags, name.getTags()), timestamp);
        final Snapshot snapshot = histogram.getSnapshot();

        return collector.addMetric("count", histogram.getCount()).addMetric("max", snapshot.getMax())
                .addMetric("min", snapshot.getMin()).addMetric("mean", snapshot.getMean())
                .addMetric("stddev", snapshot.getStdDev()).addMetric("median", snapshot.getMedian())
                .addMetric("p75", snapshot.get75thPercentile()).addMetric("p95", snapshot.get95thPercentile())
                .addMetric("p98", snapshot.get98thPercentile()).addMetric("p99", snapshot.get99thPercentile())
                .addMetric("p999", snapshot.get999thPercentile()).build();
    }

    private Set<OpenTsdbMetric> buildMeters(MetricName name, Meter meter, long timestamp) {

        final MetricsCollector collector = MetricsCollector.createNew(prefix(name.getKey()),
                merge(globalTags, name.getTags()), timestamp);

        return collector.addMetric("count", meter.getCount())
                // convert rate
                .addMetric("mean_rate", convertRate(meter.getMeanRate()))
                .addMetric("m1", convertRate(meter.getOneMinuteRate()))
                .addMetric("m5", convertRate(meter.getFiveMinuteRate()))
                .addMetric("m15", convertRate(meter.getFifteenMinuteRate())).build();
    }

    private OpenTsdbMetric buildCounter(MetricName name, Counter counter, long timestamp) {
        return OpenTsdbMetric.named(prefix(name.getKey(), "count")).withTimestamp(timestamp)
                .withValue(counter.getCount()).withTags(merge(globalTags, name.getTags())).build();
    }

    private OpenTsdbMetric buildGauge(MetricName name, Gauge gauge, long timestamp) {
        return OpenTsdbMetric.named(prefix(name.getKey(), "value")).withValue(gauge.getValue()).withTimestamp(timestamp)
                .withTags(merge(globalTags, name.getTags())).build();
    }

    private String prefix(String... components) {
        return MetricRegistry.name(prefix, components).getKey();
    }

    private Map<String, String> merge(Map<String, String> map1, Map<String, String> map2) {
        Map<String, String> result = new HashMap<String, String>();
        result.putAll(map1);
        result.putAll(map2);
        return result;
    }

    private static class MetricsCollector {
        private final String prefix;
        private final Map<String, String> tags;
        private final long timestamp;
        private final Set<OpenTsdbMetric> metrics = new HashSet<OpenTsdbMetric>();

        private MetricsCollector(String prefix, Map<String, String> tags, long timestamp) {
            this.prefix = prefix;
            this.tags = tags;
            this.timestamp = timestamp;
        }

        public static MetricsCollector createNew(String prefix, Map<String, String> tags, long timestamp) {
            return new MetricsCollector(prefix, tags, timestamp);
        }

        public MetricsCollector addMetric(String metricName, Object value) {
            this.metrics.add(OpenTsdbMetric.named(MetricRegistry.name(prefix, metricName).getKey())
                    .withTimestamp(timestamp).withValue(value).withTags(tags).build());
            return this;
        }

        public Set<OpenTsdbMetric> build() {
            return metrics;
        }
    }

}
