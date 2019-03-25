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
package com.alibaba.metrics.bin;

import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.bean.MetricResult;
import com.alibaba.metrics.common.MetricObject.MetricType;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.bin.DataSource;
import com.alibaba.metrics.server.MetricsMemoryCache;
import com.alibaba.metrics.utils.Constants;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public class MetricsMemoryCacheTest {

    private static MetricsMemoryCache cache = new MetricsMemoryCache(Constants.DATA_CACHE_TIME);
    private static MetricsCollectPeriodConfig metricsCollectPeriodConfig = new MetricsCollectPeriodConfig();
    private static long startTimestamp = System.currentTimeMillis() / 60000 * 60000;
    private static Map<MetricLevel, SortedMap<Long, List<MetricResult>>> results = new HashMap<MetricLevel, SortedMap<Long, List<MetricResult>>>();
    private static Map<MetricLevel, Set<DataSource>> dataSources = new HashMap<MetricLevel, Set<DataSource>>();

    @Test
    public void mainProcess() {
        initData();
        inBoundAssert();
        outBoundAssert();
    }

    public void initData() {

        for (final MetricLevel level : MetricLevel.values()) {

            long[] dataBlock = new long[20];
            int interval = metricsCollectPeriodConfig.period(level);

            for (int i = 0; i < 20; i++) {
                dataBlock[i] = i;
            }

            for (int i = 0; i < 15; i++) {
                cache.add(level, startTimestamp + i * interval * 1000, dataBlock);
            }

            dataSources.put(level, new HashSet<DataSource>() {
                {
                    add(new DataSource("METRIC_NAME_" + level + "_15", new HashMap<String, String>(), MetricType.COUNTER, 1, level, 1, 0.9, 128, 16, startTimestamp, "FastCompass"));
                }
            });
        }

    }

    public void outBoundAssert() {

        Map<MetricLevel, SortedMap<Long, List<MetricResult>>> tempResults = null;

        tempResults = cache.getDataFromCache(results, dataSources, startTimestamp, startTimestamp + 84000000, 60);

        SortedMap<Long, List<MetricResult>> result0 = tempResults.get(MetricLevel.CRITICAL);
        System.out.println(MetricLevel.CRITICAL + ":" + result0.size());
        assert(result0.size() == 15);

        SortedMap<Long, List<MetricResult>> result1 = tempResults.get(MetricLevel.MAJOR);
        System.out.println(MetricLevel.MAJOR + ":" + result1.size());
        assert(result1.size() == 15);

        SortedMap<Long, List<MetricResult>> result2 = tempResults.get(MetricLevel.NORMAL);
        System.out.println(MetricLevel.NORMAL + ":" + result2.size());
        assert(result2.size() == 7);

        SortedMap<Long, List<MetricResult>> result3 = tempResults.get(MetricLevel.MINOR);
        System.out.println(MetricLevel.MINOR + ":" + result3.size());
        assert(result3.size() == 3);

        SortedMap<Long, List<MetricResult>> result4 = tempResults.get(MetricLevel.TRIVIAL);
        System.out.println(MetricLevel.TRIVIAL + ":" + result4.size());
        assert(result4.size() == 1);

    }

    public void inBoundAssert() {

        Map<MetricLevel, SortedMap<Long, List<MetricResult>>> tempResults = null;

        tempResults = cache.getDataFromCache(results, dataSources, startTimestamp, startTimestamp + 3000, 60);

        SortedMap<Long, List<MetricResult>> result0 = tempResults.get(MetricLevel.CRITICAL);
        System.out.println(MetricLevel.CRITICAL + ":" + result0.size());
        assert(result0.size() == 4);

        tempResults = cache.getDataFromCache(results, dataSources, startTimestamp + 10000, startTimestamp + 20000, 60);
        SortedMap<Long, List<MetricResult>> result1 = tempResults.get(MetricLevel.MAJOR);
        System.out.println(MetricLevel.MAJOR + ":" + result1.size());
        assert(result1.size() == 4);

        tempResults = cache.getDataFromCache(results, dataSources, startTimestamp + 150000, startTimestamp + 165000, 60);
        SortedMap<Long, List<MetricResult>> result2 = tempResults.get(MetricLevel.NORMAL);
        System.out.println(MetricLevel.NORMAL + ":" + result2.size());
        assert(result2.size() == 2);

        tempResults = cache.getDataFromCache(results, dataSources, startTimestamp + 420000, startTimestamp + 450000, 60);
        SortedMap<Long, List<MetricResult>> result3 = tempResults.get(MetricLevel.MINOR);
        System.out.println(MetricLevel.MINOR + ":" + result3.size());
        assert(result3.size() == 1);

        tempResults = cache.getDataFromCache(results, dataSources, startTimestamp, startTimestamp, 60);
        SortedMap<Long, List<MetricResult>> result4 = tempResults.get(MetricLevel.TRIVIAL);
        System.out.println(MetricLevel.TRIVIAL + ":" + result4.size());
        assert(result4.size() == 0);
    }
}
