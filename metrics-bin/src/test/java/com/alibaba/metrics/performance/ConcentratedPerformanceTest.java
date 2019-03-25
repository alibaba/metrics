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
package com.alibaba.metrics.performance;

import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.common.MetricObject.MetricType;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.bin.BinAppender;
import com.alibaba.metrics.server.MetricsMemoryCache;
import com.alibaba.metrics.server.MetricsOnDisk;
import com.alibaba.metrics.status.LogDescriptionManager;
import com.alibaba.metrics.utils.Constants;
import com.alibaba.metrics.utils.FigureUtil;
import com.alibaba.metrics.utils.FileUtil;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ConcentratedPerformanceTest {

    private static int METRIC_NUM_EACH_LEVEL = 200;

    private static String logRootPath = "logs/metrics/bin/test/";
    private static MetricsOnDisk diskServer = new MetricsOnDisk(logRootPath);

    private MetricsCollectPeriodConfig metricsCollectPeriodConfig = new MetricsCollectPeriodConfig();
    private LogDescriptionManager logDescriptionManager = new LogDescriptionManager(logRootPath);
    private MetricsMemoryCache cache = new MetricsMemoryCache(Constants.DATA_CACHE_TIME);

    private static Map<MetricLevel, Long> lastUpdateTime = new HashMap<MetricLevel, Long>() {
        {
            put(MetricLevel.CRITICAL, 0L);
            put(MetricLevel.MAJOR, 0L);
            put(MetricLevel.NORMAL, 0L);
            put(MetricLevel.MINOR, 0L);
            put(MetricLevel.TRIVIAL, 0L);
        }
    };

    private static Map<MetricLevel, List<MetricObject>> metricObjectsGeneral = new HashMap<MetricLevel, List<MetricObject>>();


    @Test
    public void mainProcess() {

        try {
            initData();
            performanceTest();
        } finally {
            clear();
        }

    }

    public void initData() {

        Random random = new Random(System.currentTimeMillis());

        for(MetricLevel level : MetricLevel.values()){

            List<MetricObject> metricObjectsEach = new ArrayList<MetricObject>();
            for (int j = 0; j < METRIC_NUM_EACH_LEVEL; j++) {
                MetricObject metricObject = MetricObject.named("METRIC_" + level + "_TEST_" + j).withLevel(level)
                        .withTimestamp(0L).withType(MetricType.DELTA).withValue(random.nextInt(1000000)).build();
                metricObjectsEach.add(metricObject);
            }
            metricObjectsGeneral.put(level, metricObjectsEach);

        }


    }

    public void performanceTest() {

        long baseTimestamp = FigureUtil.getTodayStartTimestamp(System.currentTimeMillis());

        BinAppender binAppender = new BinAppender(baseTimestamp, logRootPath, metricsCollectPeriodConfig,
                logDescriptionManager, cache, 5, 5);
        binAppender.initWithoutCheckThread();

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 86400; i++) {

            Map<MetricLevel, List<MetricObject>> metricObjects = new HashMap<MetricLevel, List<MetricObject>>();

            for (MetricLevel level : MetricLevel.values()) {
                if (i % metricsCollectPeriodConfig.period(level) == 0) {
                    metricObjects.put(level, metricObjectsGeneral.get(level));
                }
            }

            long metricsTime = baseTimestamp + i * 1000;
            binAppender.append(metricsTime, metricObjects, lastUpdateTime);

        }
        long endTime = System.currentTimeMillis();

        System.out.println("Proformance Test");
        System.out.println("Total cost:" + (endTime - startTime) + "ms");
    }

    public void clear(){
        File file = new File(FileUtil.getBasePath(logRootPath));
        FileUtil.deleteDir(file);
    }
}
