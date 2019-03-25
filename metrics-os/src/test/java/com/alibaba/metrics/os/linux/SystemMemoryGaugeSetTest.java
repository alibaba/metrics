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
package com.alibaba.metrics.os.linux;


import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.ManualClock;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SystemMemoryGaugeSetTest {

    @Test
    public void testStripMultipleSpace() {
        String line = "MemTotal:        2053456 kB";
        String[] data = line.split("\\s+");
        Assert.assertEquals("all the space must be stripped.", "2053456", data[1]);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSystemMemoryGauge() {

        ManualClock clock = new ManualClock();

        SystemMemoryGaugeSet systemMemoryGaugeSet = new SystemMemoryGaugeSet(
                "src/test/resources/proc_meminfo", 10, TimeUnit.MILLISECONDS, clock);

        Map<MetricName, Metric> metrics = systemMemoryGaugeSet.getMetrics();

        Assert.assertEquals(9, metrics.keySet().size());

        clock.addMillis(20);

        Gauge<Long> memTotal = (Gauge<Long>)metrics.get(MetricName.build("mem.total"));
        Gauge<Long> memFree = (Gauge<Long>)metrics.get(MetricName.build("mem.free"));
        Gauge<Long> memBuffers = (Gauge<Long>)metrics.get(MetricName.build("mem.buffers"));
        Gauge<Long> memCached = (Gauge<Long>)metrics.get(MetricName.build("mem.cached"));
        Gauge<Long> memUsed = (Gauge<Long>)metrics.get(MetricName.build("mem.used"));
        Gauge<Double> memUsedRatio = (Gauge<Double>)metrics.get(MetricName.build("mem.used_ratio"));
        Gauge<Long> memSwapTotal = (Gauge<Long>)metrics.get(MetricName.build("mem.swap.total"));
        Gauge<Long> memSwapUsed = (Gauge<Long>)metrics.get(MetricName.build("mem.swap.used"));
        Gauge<Long> memSwapFree = (Gauge<Long>)metrics.get(MetricName.build("mem.swap.free"));

        Assert.assertEquals(2053456L, memTotal.getValue().longValue());
        Assert.assertEquals(396264L, memFree.getValue().longValue());
        Assert.assertEquals(44280L, memBuffers.getValue().longValue());
        Assert.assertEquals(397836L, memCached.getValue().longValue());
        Assert.assertEquals(2053456L - 396264L - 44280L - 397836L, memUsed.getValue().longValue());
        Assert.assertEquals(1.0d * (2053456L - 396264L - 44280L - 397836L) / 2053456L,
                memUsedRatio.getValue().doubleValue(), 0.0001d);
        Assert.assertEquals(0L, memSwapTotal.getValue().longValue());
        Assert.assertEquals(0L, memSwapUsed.getValue().longValue());
        Assert.assertEquals(0L, memSwapFree.getValue().longValue());

    }
}
