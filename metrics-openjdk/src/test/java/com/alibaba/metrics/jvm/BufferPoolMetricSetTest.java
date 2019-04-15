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
package com.alibaba.metrics.jvm;

import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.MetricName;
import org.junit.Before;
import org.junit.Test;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class BufferPoolMetricSetTest {
    private final MBeanServer mBeanServer = mock(MBeanServer.class);
    private final BufferPoolMetricSet buffers = new BufferPoolMetricSet(mBeanServer);

    private ObjectName mapped;
    private ObjectName direct;

    private final MetricName DIRECT = MetricName.build("direct");
    private final MetricName MAPPED = MetricName.build("mapped");
    private final MetricName DIRECT_COUNT = DIRECT.resolve("count");
    private final MetricName DIRECT_CAPACITY = DIRECT.resolve("capacity");
    private final MetricName DIRECT_USED = DIRECT.resolve("used");
    private final MetricName MAPPED_COUNT = MAPPED.resolve("count");
    private final MetricName MAPPED_CAPACITY = MAPPED.resolve("capacity");
    private final MetricName MAPPED_USED = MAPPED.resolve("used");

    @Before
    public void setUp() throws Exception {
        this.mapped = new ObjectName("java.nio:type=BufferPool,name=mapped");
        this.direct = new ObjectName("java.nio:type=BufferPool,name=direct");

    }

    @Test
    public void includesGaugesForDirectAndMappedPools() throws Exception {
        assertThat(buffers.getMetrics().keySet())
                .containsOnly(DIRECT_COUNT,
                              DIRECT_USED,
                              DIRECT_CAPACITY,
                              MAPPED_COUNT,
                              MAPPED_USED,
                              MAPPED_CAPACITY);
    }

    @Test
    public void ignoresGaugesForObjectsWhichCannotBeFound() throws Exception {
        when(mBeanServer.getMBeanInfo(mapped)).thenThrow(new InstanceNotFoundException());

        assertThat(buffers.getMetrics().keySet())
                .containsOnly(DIRECT_COUNT,
                              DIRECT_USED,
                              DIRECT_CAPACITY);
    }

    @Test
    public void includesAGaugeForDirectCount() throws Exception {
        final Gauge gauge = (Gauge) buffers.getMetrics().get(DIRECT_COUNT);

        when(mBeanServer.getAttribute(direct, "Count")).thenReturn(100);

        assertThat(gauge.getValue())
                .isEqualTo(100);
    }

    @Test
    public void includesAGaugeForDirectMemoryUsed() throws Exception {
        final Gauge gauge = (Gauge) buffers.getMetrics().get(DIRECT_USED);

        when(mBeanServer.getAttribute(direct, "MemoryUsed")).thenReturn(100);

        assertThat(gauge.getValue())
                .isEqualTo(100);
    }

    @Test
    public void includesAGaugeForDirectCapacity() throws Exception {
        final Gauge gauge = (Gauge) buffers.getMetrics().get(DIRECT_CAPACITY);

        when(mBeanServer.getAttribute(direct, "TotalCapacity")).thenReturn(100);

        assertThat(gauge.getValue())
                .isEqualTo(100);
    }

    @Test
    public void includesAGaugeForMappedCount() throws Exception {
        final Gauge gauge = (Gauge) buffers.getMetrics().get(MAPPED_COUNT);

        when(mBeanServer.getAttribute(mapped, "Count")).thenReturn(100);

        assertThat(gauge.getValue())
                .isEqualTo(100);
    }

    @Test
    public void includesAGaugeForMappedMemoryUsed() throws Exception {
        final Gauge gauge = (Gauge) buffers.getMetrics().get(MAPPED_USED);

        when(mBeanServer.getAttribute(mapped, "MemoryUsed")).thenReturn(100);

        assertThat(gauge.getValue())
                .isEqualTo(100);
    }

    @Test
    public void includesAGaugeForMappedCapacity() throws Exception {
        final Gauge gauge = (Gauge) buffers.getMetrics().get(MAPPED_CAPACITY);

        when(mBeanServer.getAttribute(mapped, "TotalCapacity")).thenReturn(100);

        assertThat(gauge.getValue())
                .isEqualTo(100);
    }
}
