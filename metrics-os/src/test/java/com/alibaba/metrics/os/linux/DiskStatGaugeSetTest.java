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
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiskStatGaugeSetTest {

    private final File root = mock(File.class);

    @Before
    public void setUp() throws Exception {
        when(root.getTotalSpace()).thenReturn(10000L);
        when(root.getUsableSpace()).thenReturn(8000L);
        when(root.getAbsolutePath()).thenReturn("/");
    }

    @Test
    public void testFreeDiskUsageJDK6() {
        File root = new File("/");
        Assert.assertTrue(root.getFreeSpace() > 0);
        Assert.assertTrue(root.getTotalSpace() > 0);
        Assert.assertTrue(root.getUsableSpace() > 0);
        Assert.assertTrue(root.getTotalSpace() - root.getUsableSpace() > 0);
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testDiskStatGaugeSet() {
        ManualClock clock = new ManualClock();
        DiskStatGaugeSet diskStatGaugeSet = new DiskStatGaugeSet(10, TimeUnit.MILLISECONDS, clock, new File[]{ root });

        Map<MetricName, Metric> metrics = diskStatGaugeSet.getMetrics();
        Assert.assertEquals("should have 3 metrics", 3, metrics.keySet().size());

        clock.addMillis(20);

        Gauge<Long> diskTotal = (Gauge<Long>)metrics.get(MetricName.build("disk.partition.total").tagged("partition", "/"));
        Gauge<Long> diskFree = (Gauge<Long>)metrics.get(MetricName.build("disk.partition.free").tagged("partition", "/"));
        Gauge<Double> usedRatio = (Gauge<Double>)metrics.get(MetricName.build("disk.partition.used_ratio").tagged("partition", "/"));

        Assert.assertEquals(10000L, diskTotal.getValue().longValue());
        Assert.assertEquals(8000L, diskFree.getValue().longValue());
        Assert.assertEquals(0.2d, usedRatio.getValue(), 0.0001d);
    }
}
