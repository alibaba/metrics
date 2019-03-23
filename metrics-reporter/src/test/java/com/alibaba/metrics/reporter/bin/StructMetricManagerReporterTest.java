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
package com.alibaba.metrics.reporter.bin;

import com.alibaba.metrics.Clock;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.IMetricManager;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.common.CollectLevel;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.file.JsonMetricFormat;
import com.alibaba.metrics.reporter.file.RollingFileAppender;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StructMetricManagerReporterTest {

    @Test
    public void testEnableWriteToFile() throws InterruptedException {
        IMetricManager metricManager = MetricManager.getIMetricManager();

        metricManager.clear();

        RollingFileAppender fileAppender = mock(RollingFileAppender.class);
        BinAppender binAppender = mock(BinAppender.class);

        // 设置全局的report时间间隔是3秒
        MetricsCollectPeriodConfig config = new MetricsCollectPeriodConfig(3);
        // 设置CRITICAL的report时间间隔为1秒
        config.configPeriod(MetricLevel.CRITICAL, 1);

        StructMetricManagerReporter reporter = new StructMetricManagerReporter(metricManager, binAppender, fileAppender,
                Clock.defaultClock(), TimeUnit.MILLISECONDS, TimeUnit.MILLISECONDS, TimeUnit.MILLISECONDS, MetricFilter.ALL,
                config, new HashMap<String, String>(), new JsonMetricFormat(), CollectLevel.CLASSIFIER);

        reporter.setWriteMetricToFile(true);

        long period = 1;
        try {
            reporter.start(period, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Counter c = MetricManager.getCounter("struct", MetricName.build("AAA")
                .tagged("hi", null).level(MetricLevel.CRITICAL));
        c.inc(10000);

        TimeUnit.MILLISECONDS.sleep(2200);

        // there are some other metrics, e.g. "middleware.metrics.cache.meta_load.mean"
        verify(fileAppender, atLeast(4)).append((byte[]) any());
    }

    @Test
    public void testDisableWriteToFile() throws InterruptedException {
        IMetricManager metricManager = MetricManager.getIMetricManager();

        metricManager.clear();

        RollingFileAppender fileAppender = mock(RollingFileAppender.class);
        BinAppender binAppender = mock(BinAppender.class);

        // 设置全局的report时间间隔是3秒
        MetricsCollectPeriodConfig config = new MetricsCollectPeriodConfig(3);
        // 设置CRITICAL的report时间间隔为1秒
        config.configPeriod(MetricLevel.CRITICAL, 1);

        StructMetricManagerReporter reporter = new StructMetricManagerReporter(metricManager, binAppender, fileAppender,
                Clock.defaultClock(), TimeUnit.MILLISECONDS, TimeUnit.MILLISECONDS, TimeUnit.MILLISECONDS, MetricFilter.ALL,
                config, new HashMap<String, String>(), new JsonMetricFormat(), CollectLevel.CLASSIFIER);

        long period = 1;
        try {
            reporter.start(period, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Counter c = MetricManager.getCounter("struct", MetricName.build("AAA")
                .tagged("hi", null).level(MetricLevel.CRITICAL));
        c.inc(10000);

        TimeUnit.MILLISECONDS.sleep(2200);

        verify(fileAppender, times(0)).append((byte[]) any());
    }
}
