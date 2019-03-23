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
package com.alibaba.metrics.reporter.opentsdb;

import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.MetricRegistryImpl;
import com.alibaba.metrics.jvm.GarbageCollectorMetricSet;
import com.alibaba.metrics.jvm.MemoryUsageGaugeSet;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class OpenTsdbReporterTest {

    static final MetricRegistry metrics = new MetricRegistryImpl();

    @Test
    public void test() throws InterruptedException {
        String baseUrl = "http://100.69.200.55:8242/";
        OpenTsdb opentsdb = OpenTsdb.forService(baseUrl)
        		.withReadTimeout(20000)
        		.create();

        OpenTsdbReporter openTsdbReporter = OpenTsdbReporter.forRegistry(metrics)
                .withGlobalTags(Collections.singletonMap("foo", "bar"))
                .timestampPrecision(TimeUnit.MILLISECONDS)
                .build(opentsdb);

        long period = 3;
        openTsdbReporter.start(period, TimeUnit.SECONDS);

        metrics.register("jvm.mem", new MemoryUsageGaugeSet());
        metrics.register("jvm.gc", new GarbageCollectorMetricSet());

        TimeUnit.SECONDS.sleep(6);
    }

}
