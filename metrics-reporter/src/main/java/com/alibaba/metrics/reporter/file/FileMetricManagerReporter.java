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
package com.alibaba.metrics.reporter.file;

import com.alibaba.metrics.Clock;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.ClusterHistogram;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Histogram;
import com.alibaba.metrics.IMetricManager;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.Timer;
import com.alibaba.metrics.common.CollectLevel;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.common.MetricsCollector;
import com.alibaba.metrics.common.MetricsCollectorFactory;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.MetricManagerReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class FileMetricManagerReporter extends MetricManagerReporter {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final byte[] DEFAULT_DELIMITER_BYTES = "\n".getBytes(UTF_8);

    private final Logger logger = LoggerFactory.getLogger(FileMetricManagerReporter.class);
    private final Clock clock;
    private final Map<String, String> globalTags;
    private final TimeUnit timestampPrecision;
    private final MetricFormat metricFormat;
    private final FileAppender fileAppender;
    /**
     * 控制metrics落盘时需要收集的指标内容
     */
    private CollectLevel collectLevel;

    private FileMetricManagerReporter(IMetricManager metricManager, FileAppender fileAppender, Clock clock,
            TimeUnit rateUnit, TimeUnit timestampPrecision, TimeUnit durationUnit, MetricFilter filter,
            MetricsCollectPeriodConfig metricsReportPeriodConfig, Map<String, String> globalTags,
            MetricFormat metricFormat, CollectLevel collectLevel) {
        super(metricManager, "file-reporter", filter, metricsReportPeriodConfig, rateUnit, durationUnit);
        this.clock = clock;
        this.globalTags = globalTags;
        this.timestampPrecision = timestampPrecision;
        this.fileAppender = fileAppender;
        this.metricFormat = metricFormat;
        this.collectLevel = collectLevel;
    }

    /**
     * Returns a new {@link Builder} for {@link FileMetricManagerReporter}.
     *
     * @param metricManager
     *            the metricManager to report
     * @return a {@link Builder} instance for a {@link FileMetricManagerReporter}
     */
    public static Builder forMetricManager(IMetricManager metricManager) {
        return new Builder(metricManager);
    }

    /**
     * A builder for {@link FileMetricManagerReporter} instances. Defaults to not using
     * a prefix, using the default clock, converting rates to events/second,
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
        private MetricFormat metricFormat;
        private FileAppender fileAppender;
        private CollectLevel collectLevel;
        // 提交到服务器的时间戳的单位，只支持毫秒和秒，默认是秒
        private TimeUnit timestampPrecision = TimeUnit.SECONDS;

        private Builder(IMetricManager metricManager) {
            this.metricManager = metricManager;
            this.clock = Clock.defaultClock();
            this.prefix = null;
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
            this.metricFormat = new SimpleTextMetricFormat();
            this.collectLevel = CollectLevel.COMPACT;
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

        public Builder withCollectLevel(CollectLevel level) {
            this.collectLevel = collectLevel;
            return this;
        }

        /**
         * Default metricFormat is {@link SimpleTextMetricFormat}
         * @param metricFormat
         * @return
         */
        public Builder metricFormat(MetricFormat metricFormat) {
            this.metricFormat = metricFormat;
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

        public Builder fileAppender(FileAppender fileAppender) {
            this.fileAppender = fileAppender;
            return this;
        }

        /**
         * Builds a {@link FileMetricManagerReporter} with the given properties, sending
         * metrics using the given
         *
         * @return a {@link FileMetricManagerReporter}
         * @throws IOException
         */
        public FileMetricManagerReporter build() {
            if (globalTags == null) {
                globalTags = Collections.emptyMap();
            }

            return new FileMetricManagerReporter(metricManager,
                    fileAppender, clock, rateUnit, timestampPrecision, durationUnit, filter,
                    metricsReportPeriodConfig, globalTags, metricFormat, collectLevel);
        }
    }

    @Override
    public void report(Map<MetricName, Gauge> gauges, Map<MetricName, Counter> counters,
            Map<MetricName, Histogram> histograms, Map<MetricName, Meter> meters,
            Map<MetricName, Timer> timers, Map<MetricName, Compass> compasses, Map<MetricName, FastCompass> fastCompasses, Map<MetricName, ClusterHistogram> clusterHistogrames) {

        long timestamp = clock.getTime();

        if (TimeUnit.MICROSECONDS.equals(timestampPrecision)) {
            timestamp = timestamp / 1000;
        }

        MetricsCollector collector =
                MetricsCollectorFactory.createNew(collectLevel, globalTags, rateFactor, durationFactor);

        for (Entry<MetricName, Gauge> g : gauges.entrySet()) {
            if (g.getValue().getValue() instanceof Collection && ((Collection) g.getValue().getValue()).isEmpty()) {
                continue;
            }
            collector.collect(g.getKey(), g.getValue(), timestamp);
        }

        writeMetricObject(collector.build());
        collector.clear();

        for (Entry<MetricName, Counter> entry : counters.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), timestamp);
        }

        writeMetricObject(collector.build());
        collector.clear();

        for (Entry<MetricName, Histogram> entry : histograms.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), timestamp);
        }

        writeMetricObject(collector.build());
        collector.clear();

        for (Entry<MetricName, Meter> entry : meters.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), timestamp);
        }

        writeMetricObject(collector.build());
        collector.clear();

        for (Entry<MetricName, Timer> entry : timers.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), timestamp);
        }

        writeMetricObject(collector.build());
        collector.clear();

        for (Entry<MetricName, Compass> entry : compasses.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), timestamp);
        }

        writeMetricObject(collector.build());
        collector.clear();

        for (Entry<MetricName, FastCompass> entry : fastCompasses.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), timestamp);
        }

        writeMetricObject(collector.build());
        collector.clear();
    }


    public void writeMetricObject(List<MetricObject> metricObjects) {
        try {
            for (MetricObject metric : metricObjects) {
                fileAppender.append(metricFormat.formatToBytes(metric));
                fileAppender.append(DEFAULT_DELIMITER_BYTES);
            }
            fileAppender.flush();
        } catch (Throwable e) {
            logger.error("write metrics data error!", e);
        }
    }

    public void setCollectLevel(CollectLevel collectLevel) {
        this.collectLevel = collectLevel;
    }
}
