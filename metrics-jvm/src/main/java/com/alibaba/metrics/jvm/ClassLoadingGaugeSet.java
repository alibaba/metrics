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
import com.alibaba.metrics.MetricSet;
import com.alibaba.metrics.PersistentGauge;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * A set of gauges for JVM classloader usage.
 */
public class ClassLoadingGaugeSet implements MetricSet {

    private final ClassLoadingMXBean mxBean;

    public ClassLoadingGaugeSet() {
        this(ManagementFactory.getClassLoadingMXBean());
    }

    public ClassLoadingGaugeSet(ClassLoadingMXBean mxBean) {
        this.mxBean = mxBean;
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        final Map<MetricName, Metric> gauges = new HashMap<MetricName, Metric>();

        gauges.put(MetricName.build("loaded"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getTotalLoadedClassCount();
            }
        });

        gauges.put(MetricName.build("loaded_current"), new PersistentGauge<Integer>() {
            @Override
            public Integer getValue() {
                return mxBean.getLoadedClassCount();
            }
        });

        gauges.put(MetricName.build("unloaded"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getUnloadedClassCount();
            }
        });

        return gauges;
    }

    @Override
    public long lastUpdateTime() {
        return System.currentTimeMillis();
    }
}
