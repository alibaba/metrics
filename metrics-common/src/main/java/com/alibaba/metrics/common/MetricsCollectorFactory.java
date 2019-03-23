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
package com.alibaba.metrics.common;

import com.alibaba.metrics.MetricFilter;

import java.util.HashMap;
import java.util.Map;

public class MetricsCollectorFactory {

    public static MetricsCollector createNew(Map<String, String> globalTags,
                                             double rateFactor, double durationFactor) {
        return createNew(CollectLevel.NORMAL, globalTags, rateFactor, durationFactor, null);
    }

    public static MetricsCollector createNew(CollectLevel level, Map<String, String> globalTags,
                                             double rateFactor, double durationFactor) {
        return createNew(level, globalTags, rateFactor, durationFactor, null);
    }

    public static MetricsCollector createNew(CollectLevel level, double rateFactor, double durationFactor,
                                             MetricFilter filter) {
        return createNew(level, new HashMap<String, String>(), rateFactor, durationFactor, filter);
    }

    public static MetricsCollector createNew(double rateFactor, double durationFactor, MetricFilter filter) {
        return createNew(CollectLevel.NORMAL, new HashMap<String, String>(), rateFactor, durationFactor, filter);
    }

    public static MetricsCollector createNew(CollectLevel collectLevel, Map<String, String> globalTags, double rateFactor,
                                             double durationFactor, MetricFilter filter) {

        switch (collectLevel) {
            case COMPACT:
                return new CompactMetricsCollector(globalTags, rateFactor, durationFactor, filter);
            case NORMAL:
                return new NormalMetricsCollector(globalTags, rateFactor, durationFactor, filter);
            case CLASSIFIER:
                return new ClassifiedMetricsCollector(globalTags, rateFactor, durationFactor, filter);
            case COMPLETE:
                // FIXME: currently not supported
                throw new UnsupportedOperationException("Currently not supported!");
            default:
                throw new IllegalStateException("Unsupported CollectLevel: " + collectLevel);
        }

    }


}
