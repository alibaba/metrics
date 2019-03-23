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
package com.alibaba.metrics.common.filter;

import com.alibaba.metrics.ClusterHistogram;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Histogram;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.Timer;
import com.alibaba.metrics.common.MetricObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class MetricNameSetFilter implements MetricFilter {

    private static final Set<String> counterSuffixSet = new LinkedHashSet<String>();
    private static final Set<String> meterSuffixSet = new LinkedHashSet<String>();
    private static final Set<String> histogramSuffixSet = new LinkedHashSet<String>();
    private static final Set<String> timerSuffixSet = new LinkedHashSet<String>();
    private static final Set<String> compassSuffixSet = new LinkedHashSet<String>();
    private static final Set<String> gaugeSuffixSet = Collections.emptySet();
    private static final Set<String> fastCompassSuffixSet = new LinkedHashSet<String>();
    private static final Set<String> clusterHistogramSuffixSet = new LinkedHashSet<String>();

    static {
        counterSuffixSet.add(".count");
        counterSuffixSet.add(".bucket_count");
        counterSuffixSet.add(".qps");

        meterSuffixSet.add(".count");
        meterSuffixSet.add(".bucket_count");
        meterSuffixSet.add(".qps");
        meterSuffixSet.add(".s1");
        meterSuffixSet.add(".m1");
        meterSuffixSet.add(".m5");
        meterSuffixSet.add(".m15");
        meterSuffixSet.add(".mean_rate");

        histogramSuffixSet.add(".count");
        histogramSuffixSet.add(".max");
        histogramSuffixSet.add(".min");
        histogramSuffixSet.add(".mean");
        histogramSuffixSet.add(".stddev");
        histogramSuffixSet.add(".median");
        histogramSuffixSet.add(".p75");
        histogramSuffixSet.add(".p95");
        histogramSuffixSet.add(".p99");

        timerSuffixSet.add(".count");
        timerSuffixSet.add(".bucket_count");
        timerSuffixSet.add(".qps");
        timerSuffixSet.add(".s1");
        timerSuffixSet.add(".m1");
        timerSuffixSet.add(".m5");
        timerSuffixSet.add(".m15");
        timerSuffixSet.add(".mean_rate");
        timerSuffixSet.add(".max");
        timerSuffixSet.add(".min");
        timerSuffixSet.add(".mean");
        timerSuffixSet.add(".rt");
        timerSuffixSet.add(".stddev");
        timerSuffixSet.add(".median");
        timerSuffixSet.add(".p75");
        timerSuffixSet.add(".p95");
        timerSuffixSet.add(".p99");

        compassSuffixSet.add(".count");
        compassSuffixSet.add(".bucket_count");
        compassSuffixSet.add(".qps");
        compassSuffixSet.add(".s1");
        compassSuffixSet.add(".m1");
        compassSuffixSet.add(".m5");
        compassSuffixSet.add(".m15");
        compassSuffixSet.add(".mean_rate");
        compassSuffixSet.add(".max");
        compassSuffixSet.add(".min");
        compassSuffixSet.add(".mean");
        compassSuffixSet.add(".rt");
        compassSuffixSet.add(".stddev");
        compassSuffixSet.add(".median");
        compassSuffixSet.add(".p75");
        compassSuffixSet.add(".p95");
        compassSuffixSet.add(".p99");
        compassSuffixSet.add(".success_count");
        compassSuffixSet.add(".success_rate");
        compassSuffixSet.add(".error.count");
        compassSuffixSet.add(".success_bucket_count");
        compassSuffixSet.add(".error_bucket_count");

        fastCompassSuffixSet.add(".qps");
        fastCompassSuffixSet.add(".rt");
        fastCompassSuffixSet.add(".success_rate");
        fastCompassSuffixSet.add(".bucket_count");
        fastCompassSuffixSet.add(".bucket_sum");

        clusterHistogramSuffixSet.add(".cluster_percentile");
    }

    private Set<String> metricNames;

    public MetricNameSetFilter(String... names) {
        this.metricNames = new HashSet<String>(Arrays.asList(names));
    }

    public MetricNameSetFilter(final Set<String> metricNames) {
        this.metricNames = metricNames;
    }

    @Override
    public boolean matches(MetricName name, Metric metric) {
        for (String nameToMatch: metricNames) {
            boolean success;
            if (metric instanceof Counter) {
                success = matchInternal(nameToMatch, name, counterSuffixSet, false);
            } else if (metric instanceof Meter) {
                success = matchInternal(nameToMatch, name, meterSuffixSet, false);
            } else if (metric instanceof Histogram) {
                success = matchInternal(nameToMatch, name, histogramSuffixSet, false);
            } else if (metric instanceof Timer) {
                success = matchInternal(nameToMatch, name, timerSuffixSet, false);
            } else if (metric instanceof Compass) {
                success = matchInternal(nameToMatch, name, compassSuffixSet, true);
                if (!success) {
                    success = matchCompassAddon(nameToMatch, name, (Compass)metric);
                }
            } else if (metric instanceof Gauge) {
                success = matchInternal(nameToMatch, name, gaugeSuffixSet, false);
            } else if (metric instanceof FastCompass) {
                success = matchInternal(nameToMatch, name, fastCompassSuffixSet, true);
            } else if (metric instanceof ClusterHistogram) {
                success = matchInternal(nameToMatch, name, clusterHistogramSuffixSet, false);
            } else if (metric == null) {
                /**
                 * Should only come from {@link com.alibaba.metrics.common.MetricsCollector#addMetric(MetricObject)}
                 * For compatibility reason.
                 */
                if (nameToMatch.equals(name.getKey())) {
                    success = true;
                } else {
                    int lastIndexOfDot = name.getKey().lastIndexOf(".");
                    if (lastIndexOfDot < 0) {
                        success = false;
                    } else {
                        success = name.getKey().startsWith(nameToMatch) && nameToMatch.length() == lastIndexOfDot;
                    }
                }
            } else {
                success = nameToMatch.equals(name.getKey());
            }
            if (success) return true;
        }
        return false;
    }

    private boolean matchInternal(String nameToMatch, MetricName name, Set<String> suffixSet, boolean matchDynamic) {
        if (nameToMatch == null) {
            return false;
        }

        if (nameToMatch.equals(name.getKey())) {
            return true;
        }

        if (!nameToMatch.startsWith(name.getKey())) {
            return false;
        }

        for (String suffix: suffixSet) {
            String nameWithSuffix = name.getKey() + suffix;
            if (nameWithSuffix.equals(nameToMatch)) {
                return true;
            }
        }

        if (matchDynamic && nameToMatch.endsWith("bucket_count")) {
            return true;
        }

        return false;
    }

    private boolean matchCompassAddon(String nameToMatch, MetricName name, Compass compass) {
        if (nameToMatch == null) {
            return false;
        }
        for (String addon: compass.getAddonCounts().keySet()) {
            String nameWithAddon = name.getKey() + "." + addon + ".count";
            if (nameToMatch.equals(nameWithAddon)) {
                return true;
            }
            String nameWithAddonBucket = name.getKey() + "." + addon + "_bucket_count";
            if (nameToMatch.equals(nameWithAddonBucket)) {
                return true;
            }
            String nameWithAddonRate = name.getKey() + "." + addon + "_rate";
            if (nameToMatch.equals(nameWithAddonRate)) {
                return true;
            }
        }
        return false;
    }
}
