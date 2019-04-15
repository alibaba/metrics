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

import org.junit.Test;

import java.lang.management.CompilationMXBean;
import java.util.concurrent.TimeUnit;

import static com.alibaba.metrics.Constants.NOT_AVAILABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompilationGaugeTest {

    private final CompilationMXBean cm = mock(CompilationMXBean.class);
    private final CompilationGauge gauge = new CompilationGauge(1L, TimeUnit.SECONDS, cm);

    @Test
    public void compilationGauge() throws Exception {
        when(cm.isCompilationTimeMonitoringSupported()).thenReturn(true);
        when(cm.getTotalCompilationTime()).thenReturn(100L);
        assertThat(gauge.getValue()).isEqualTo(NOT_AVAILABLE);
        when(cm.getTotalCompilationTime()).thenReturn(120L);
        Thread.sleep(1200);
        assertThat(gauge.getValue()).isEqualTo(20L);
    }
}
