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
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.Timer;
import com.alibaba.metrics.common.CollectLevel;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.common.filter.BucketMetricLevelFilter;
import com.alibaba.metrics.common.filter.TimeMetricLevelFilter;
import com.alibaba.metrics.reporter.MetricManagerReporter;
import com.alibaba.metrics.reporter.file.MetricFormat;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class EmptyMetricManagerReporter extends MetricManagerReporter {
    //一个空的reporter，什么都没做

    public EmptyMetricManagerReporter(IMetricManager metricManager, BinAppender binAppender, Clock clock,
            TimeUnit rateUnit, TimeUnit timestampPrecision, TimeUnit durationUnit, MetricFilter filter,
            MetricsCollectPeriodConfig metricsReportPeriodConfig, Map<String, String> globalTags,
            MetricFormat metricFormat, CollectLevel collectLevel) {

        super(metricManager, "empty-reporter", filter, new BucketMetricLevelFilter(metricsReportPeriodConfig), rateUnit,
                durationUnit);
    }

    protected EmptyMetricManagerReporter(IMetricManager metricManager, MetricFilter filter,
            TimeMetricLevelFilter timeMetricLevelFilter, TimeUnit rateUnit, TimeUnit durationUnit,
            ScheduledExecutorService executor) {
        super(metricManager, filter, timeMetricLevelFilter, rateUnit, durationUnit, executor);
    }

    @Override
    public void report(Map<MetricName, Gauge> gauges, Map<MetricName, Counter> counters,
            Map<MetricName, Histogram> histograms, Map<MetricName, Meter> meters, Map<MetricName, Timer> timers,
            Map<MetricName, Compass> compasses, Map<MetricName, FastCompass> fastCompasses) {
        //donothing
    }

}
