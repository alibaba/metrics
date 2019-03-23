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
package com.alibaba.metrics.rest;

import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricName;

public class MetricLevelFilter implements MetricFilter {

    private boolean matchLevelAbove;

    private MetricLevel level;

    public MetricLevelFilter(MetricLevel level, boolean above) {
        this.level = level;
        this.matchLevelAbove = above;
    }

    public boolean matches(MetricName name, Metric metric) {
        if (matchLevelAbove) {
            return (level != null) && level.compareTo(name.getMetricLevel()) <= 0;
        } else {
            return (level != null) && level.toString().equalsIgnoreCase(name.getMetricLevel().toString());
        }
    }
}
