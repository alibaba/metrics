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
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;

/**
 * 不同Level的Metric有配置不同的report时间间隔。这个Filter用于在report时，
 * 计算某一个Level的Metric是否允许report。
 *
 * The array based implementation is roughly 35% faster than the HashMap base implementation.
 * Please refer to TimeMetricLevelFilterTest for more details.
 *
 * e.g.
 * TimeMetricLevelFilter#matches costs: 227 ms for 20,000,000 calls. (HashMap based)
 * TimeMetricLevelFilter#matches costs: 145 ms for 20,000,000 calls. (Array based)
 *
 *
 */
public class TimeMetricLevelFilter implements MetricFilter {

    /**
     * 记录上次MetricLevel的report相关的信息
     * 用数组加快查询速度
     */
    protected MetricsLevelInfo[] levelInfos = new MetricsLevelInfo[MetricLevel.getMaxValue() + 1];

    protected MetricsCollectPeriodConfig config;

    class MetricsLevelInfo {
        public MetricsLevelInfo(boolean allow, long lastReportTimeStamp) {
            this.allow = allow;
            this.lastReportTimeStamp = lastReportTimeStamp;
        }

        /**
         * 是否允许report
         */
        Boolean allow;
        /**
         * 上一次report的时间点
         */
        Long lastReportTimeStamp;
    }

    public TimeMetricLevelFilter() {

    }

    public TimeMetricLevelFilter(MetricsCollectPeriodConfig config) {
        this.config = config;

        /**
         * 如果没有config配置，则不需要做任何计算，所有的都是允许
         */
        if (config == null) {
            return;
        }

        long currentTimeMillis = System.currentTimeMillis();
        MetricLevel[] levels = MetricLevel.values();

        for (MetricLevel level : levels) {
            levelInfos[level.ordinal()] = new MetricsLevelInfo(false, currentTimeMillis);
        }
    }

    /**
     * 在report之前调用。计算并标记好每一个Level的Metric是否可以report
     */
    public void beforeReport() {
        if (config == null) {
            return;
        }

        long currentTimeMillis = System.currentTimeMillis();
        MetricLevel[] levels = MetricLevel.values();
        for (MetricLevel level : levels) {
            int period = config.period(level);
            // 如果配置的时间间隔是负数，则直接忽略
            if(period < 0) {
            	continue;
            }

            MetricsLevelInfo metricsLevelInfo = levelInfos[level.ordinal()];

            /**
             * 计算出离上次report的时间间隔，判断是否可以再次report了
             */
            if ((currentTimeMillis - metricsLevelInfo.lastReportTimeStamp)/1000 >= period) {
                metricsLevelInfo.allow = true;
                metricsLevelInfo.lastReportTimeStamp = currentTimeMillis;
            }
        }
    }

    /**
     * 在report之后调用。将Metrics Level里允许被report的标记清除
     */
    public void afterReport() {
        if (config == null) {
            return;
        }

        for (MetricsLevelInfo info : levelInfos) {
            if (info.allow) {
                info.allow = false;
            }
        }
    }

    @Override
    public boolean matches(MetricName name, Metric metric) {
        if (config == null) {
            return true;
        }
        return levelInfos[name.getMetricLevel().ordinal()].allow;
    }

}
