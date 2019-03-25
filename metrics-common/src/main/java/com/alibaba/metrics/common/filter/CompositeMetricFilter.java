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

import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompositeMetricFilter implements MetricFilter {

    MetricFilter[] filters;

    /**
     * 如果包含 {@link MetricFilter#ALL} 则直接丢弃，减少一次无谓的判断
     * @param filters
     */
    public CompositeMetricFilter(MetricFilter... filters) {
        List<MetricFilter> filterList = new ArrayList<MetricFilter>(Arrays.asList(filters));
        filterList.remove(MetricFilter.ALL);
        if (!filterList.isEmpty()) {
            this.filters = filterList.toArray(new MetricFilter[filterList.size()]);
        }
    }

    @Override
    public boolean matches(MetricName name, Metric metric) {
        if (filters != null) {
            for (MetricFilter filter : filters) {
                if (!filter.matches(name, metric)) {
                    return false;
                }
            }
        }
        return true;
    }


}
