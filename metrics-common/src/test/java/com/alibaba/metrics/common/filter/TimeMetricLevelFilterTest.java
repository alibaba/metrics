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

import com.alibaba.metrics.Counter;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TimeMetricLevelFilterTest {

    private Counter counter = new Counter() {
        @Override
        public void inc() {

        }

        @Override
        public void inc(long n) {

        }

        @Override
        public void dec() {

        }

        @Override
        public void dec(long n) {

        }

        @Override
        public long getCount() {
            return 0;
        }

        @Override
        public long lastUpdateTime() {
            return 0;
        }
    };

    private class AnotherTimeMetricLevelFilter extends TimeMetricLevelFilter {

        private MetricsCollectPeriodConfig config;

        private MetricsLevelInfo[] levelInfos = new MetricsLevelInfo[MetricLevel.values().length];

        public AnotherTimeMetricLevelFilter(MetricsCollectPeriodConfig config) {
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

        @Override
        public boolean matches(MetricName name, Metric metric) {
            if (config == null) {
                return true;
            }
            return levelInfos[name.getMetricLevel().ordinal()].allow;
        }
    }

    @Test
    public void test() throws InterruptedException {
        MetricsCollectPeriodConfig config = new MetricsCollectPeriodConfig();
        config.configPeriod(MetricLevel.TRIVIAL, 3);
        config.configPeriod(MetricLevel.CRITICAL, 1);
        config.configPeriod(MetricLevel.NORMAL, 1);

        TimeMetricLevelFilter filter = new TimeMetricLevelFilter(config);


        TimeUnit.SECONDS.sleep(1);

        {
            MetricName name = new MetricName("test", MetricLevel.TRIVIAL);
            int matchCounter = 0;
            for (int i = 0; i < 7; ++i) {
                filter.beforeReport();
                boolean matches = filter.matches(name, null);
                filter.afterReport();
                if (matches) {
                    matchCounter++;
                }
                System.err.println(matches);
                TimeUnit.SECONDS.sleep(1);
            }

            Assert.assertEquals(matchCounter, 2);
        }

        TimeUnit.SECONDS.sleep(2);
        {
            System.out.println("========");
            MetricName name = new MetricName("test", MetricLevel.CRITICAL);
            int matchCounter = 0;
            for (int i = 0; i < 15; ++i) {
                filter.beforeReport();
                boolean matches = filter.matches(name, null);
                filter.afterReport();
                if (matches) {
                    matchCounter++;
                }
                System.err.println(matches);
                TimeUnit.MILLISECONDS.sleep(100);
            }

            Assert.assertEquals(matchCounter, 2);
        }

        TimeUnit.SECONDS.sleep(2);
        {
            System.out.println("========");
            MetricName name = new MetricName("test", MetricLevel.NORMAL);
            int matchCounter = 0;
            for (int i = 0; i < 3; ++i) {
                filter.beforeReport();
                boolean matches = filter.matches(name, null);
                filter.afterReport();
                if (matches) {
                    matchCounter++;
                }
                System.err.println(matches);
                TimeUnit.SECONDS.sleep(2);
            }

            Assert.assertEquals(matchCounter, 3);
        }
    }

    @Test
    public void testNull() throws InterruptedException {
        TimeMetricLevelFilter filter = new TimeMetricLevelFilter(null);
        MetricName name = new MetricName("test", MetricLevel.NORMAL);
        Assert.assertTrue(filter.matches(name, null));
        TimeUnit.SECONDS.sleep(2);

        Assert.assertTrue(filter.matches(name, null));
    }

    @Test
    public void testTimeLevelFilterPerformance() {
        TimeMetricLevelFilter filter = new TimeMetricLevelFilter(new MetricsCollectPeriodConfig());

        int iterations = 20000000;

        testTimeLevelFilterPerformance(filter, iterations);

        TimeMetricLevelFilter filter2 = new AnotherTimeMetricLevelFilter(new MetricsCollectPeriodConfig());

        testTimeLevelFilterPerformance(filter2, iterations);
    }

    private void testTimeLevelFilterPerformance(TimeMetricLevelFilter filter, int iterations) {
        MetricName name = new MetricName("test", MetricLevel.CRITICAL);

        long start = System.currentTimeMillis();

        for (int i=0; i< iterations; i++) {
            filter.matches(name, counter);
        }

        System.out.println("TimeMetricLevelFilter#matches costs: "
                + (System.currentTimeMillis() - start) + " ms for " + iterations + " calls.");
    }



}
