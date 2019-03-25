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
package com.alibaba.metrics.jvm;

import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.MetricSet;
import com.alibaba.metrics.PersistentGauge;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A set of gauges for the counts and elapsed times of garbage collections.
 */
public class GarbageCollectorMetricSet implements MetricSet {
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");

    private final List<GarbageCollectorMXBean> garbageCollectors;
    private final List<Long> lastGarbageCountCollectors;
    private final List<Long> lastGarbageTimeCollectors;

    /**
     * Creates a new set of gauges for all discoverable garbage collectors.
     */
    public GarbageCollectorMetricSet() {
        this(ManagementFactory.getGarbageCollectorMXBeans());
    }

    /**
     * Creates a new set of gauges for the given collection of garbage collectors.
     *
     * @param garbageCollectors    the garbage collectors
     */
    public GarbageCollectorMetricSet(Collection<GarbageCollectorMXBean> garbageCollectors) {
        this.garbageCollectors = new ArrayList<GarbageCollectorMXBean>(garbageCollectors);
        this.lastGarbageCountCollectors = new ArrayList<Long>(){{
            add(0L);add(0L);add(0L);add(0L);add(0L);add(0L);add(0L);add(0L);add(0L);add(0L);
        }};
        this.lastGarbageTimeCollectors = new ArrayList<Long>(){{
            add(0L);add(0L);add(0L);add(0L);add(0L);add(0L);add(0L);add(0L);add(0L);add(0L);
        }};
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        final Map<MetricName, Metric> gauges = new HashMap<MetricName, Metric>();
        int index = 0;
        for (final GarbageCollectorMXBean gc : garbageCollectors) {
            final String name = WHITESPACE.matcher(gc.getName()).replaceAll("_").toLowerCase();
            final int i = index;
            gauges.put(MetricRegistry.name(name, "count"), new PersistentGauge<Long>() {
                @Override
                public Long getValue() {
                    return gc.getCollectionCount();
                }
            });

            gauges.put(MetricRegistry.name(name, "time"), new PersistentGauge<Long>() {
                @Override
                public Long getValue() {
                    return gc.getCollectionTime();
                }
            });

            gauges.put(MetricRegistry.name(name, "count.delta"), new PersistentGauge<Long>() {
                @Override
                public Long getValue() {
                    long current = gc.getCollectionCount();
                    long result = current - lastGarbageCountCollectors.get(i);
                    lastGarbageCountCollectors.set(i, current);
                    return result;
                }
            });

            gauges.put(MetricRegistry.name(name, "time.delta"), new PersistentGauge<Long>() {
                @Override
                public Long getValue() {
                    long current = gc.getCollectionTime();
                    long result = current - lastGarbageTimeCollectors.get(i);
                    lastGarbageTimeCollectors.set(i, current);
                    return result;
                }
            });

            index = i + 1;
        }
        return Collections.unmodifiableMap(gauges);
    }

    @Override
    public long lastUpdateTime() {
        return System.currentTimeMillis();
    }
}
