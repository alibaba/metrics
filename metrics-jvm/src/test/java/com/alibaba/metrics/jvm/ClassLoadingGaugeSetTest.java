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

import java.lang.management.ClassLoadingMXBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClassLoadingGaugeSetTest {

    private final ClassLoadingMXBean cl = mock(ClassLoadingMXBean.class);
    private final ClassLoadingGaugeSet gauges = new ClassLoadingGaugeSet(cl);

    @Before
    public void setUp() throws Exception {
        when(cl.getTotalLoadedClassCount()).thenReturn(2L);
        when(cl.getUnloadedClassCount()).thenReturn(1L);
        when(cl.getLoadedClassCount()).thenReturn(1);
    }

    @Test
    public void loadedGauge() throws Exception {
        final Gauge gauge = (Gauge) gauges.getMetrics().get(MetricName.build("loaded"));
        assertThat(gauge.getValue()).isEqualTo(2L);
    }

    @Test
    public void unLoadedGauge() throws Exception {
        final Gauge gauge = (Gauge) gauges.getMetrics().get(MetricName.build("unloaded"));
        assertThat(gauge.getValue()).isEqualTo(1L);
    }


    @Test
    public void loadedCurrentGauge() throws Exception {
        final Gauge gauge = (Gauge) gauges.getMetrics().get(MetricName.build("loaded_current"));
        assertThat(gauge.getValue()).isEqualTo(1);
    }

}
