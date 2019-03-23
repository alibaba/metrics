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
package com.alibaba.metrics.reporter;

import com.alibaba.metrics.Counter;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Histogram;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.MetricRegistryImpl;
import com.alibaba.metrics.Timer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ScheduledReporterTest {
    private final Gauge gauge = mock(Gauge.class);
    private final Counter counter = mock(Counter.class);
    private final Histogram histogram = mock(Histogram.class);
    private final Meter meter = mock(Meter.class);
    private final Timer timer = mock(Timer.class);

    private final MetricRegistry registry = new MetricRegistryImpl();
    private final ScheduledReporter reporter = spy(
            new ScheduledReporter(registry,
                    "example",
                    MetricFilter.ALL,
                    TimeUnit.SECONDS,
                    TimeUnit.MILLISECONDS) {
                @Override
                public void report(SortedMap<MetricName, Gauge> gauges,
                                   SortedMap<MetricName, Counter> counters,
                                   SortedMap<MetricName, Histogram> histograms,
                                   SortedMap<MetricName, Meter> meters,
                                   SortedMap<MetricName, Timer> timers) {
                    // nothing doing!
                }
            }
    );

    @Before
    public void setUp() throws Exception {
        registry.register("gauge", gauge);
        registry.register("counter", counter);
        registry.register("histogram", histogram);
        registry.register("meter", meter);
        registry.register("timer", timer);

        reporter.start(200, TimeUnit.MILLISECONDS);
    }

    @After
    public void tearDown() throws Exception {
        reporter.stop();
    }

    @Test
    public void pollsPeriodically() throws Exception {
        Thread.sleep(500);
        verify(reporter, times(2)).report(
                map("gauge", gauge),
                map("counter", counter),
                map("histogram", histogram),
                map("meter", meter),
                map("timer", timer)
        );
    }

    private <T> SortedMap<MetricName, T> map(String name, T value) {
        final SortedMap<MetricName, T> map = new TreeMap<MetricName, T>();
        map.put(MetricName.build(name), value);
        return map;
    }
}
