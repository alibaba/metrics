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
package com.alibaba.metrics.instrument;

import com.alibaba.metrics.Counter;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.MetricRegistryImpl;
import com.alibaba.metrics.Timer;
import org.junit.After;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class InstrumentedExecutorServiceTest {
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final MetricRegistry registry = new MetricRegistryImpl();
    private final InstrumentedExecutorService instrumentedExecutorService = new InstrumentedExecutorService(executor, registry, "xs");

    @Test
    public void reportsTasksInformation() throws Exception {
        final Meter submitted = registry.meter("xs.submitted");
        final Counter running = registry.counter("xs.running");
        final Meter completed = registry.meter("xs.completed");
        final Timer duration = registry.timer("xs.duration");

        assertThat(submitted.getCount()).isEqualTo(0);
        assertThat(running.getCount()).isEqualTo(0);
        assertThat(completed.getCount()).isEqualTo(0);
        assertThat(duration.getCount()).isEqualTo(0);

        Future<?> theFuture = instrumentedExecutorService.submit(new Runnable() {
            public void run() {
                assertThat(submitted.getCount()).isEqualTo(1);
                assertThat(running.getCount()).isEqualTo(1);
                assertThat(completed.getCount()).isEqualTo(0);
                assertThat(duration.getCount()).isEqualTo(0);
            }
        });

        theFuture.get();

        assertThat(submitted.getCount()).isEqualTo(1);
        assertThat(running.getCount()).isEqualTo(0);
        assertThat(completed.getCount()).isEqualTo(1);
        assertThat(duration.getCount()).isEqualTo(1);
        assertThat(duration.getSnapshot().size()).isEqualTo(1);
    }

    @After
    public void tearDown() throws Exception {
        instrumentedExecutorService.shutdown();
        if (!instrumentedExecutorService.awaitTermination(2, TimeUnit.SECONDS)) {
            System.err.println("InstrumentedExecutorService did not terminate.");
        }
    }

}
