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
import com.alibaba.metrics.Snapshot;
import com.alibaba.metrics.Timer;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JmxReporterTest {
    private final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
    private final String name = UUID.randomUUID().toString().replaceAll("[{\\-}]", "");
    private final MetricRegistry registry = new MetricRegistryImpl();

    private final JmxReporter reporter = JmxReporter.forRegistry(registry)
            .registerWith(mBeanServer)
            .inDomain(name)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .convertRatesTo(TimeUnit.SECONDS)
            .filter(MetricFilter.ALL)
            .build();

    private final Gauge gauge = mock(Gauge.class);
    private final Counter counter = mock(Counter.class);
    private final Histogram histogram = mock(Histogram.class);
    private final Meter meter = mock(Meter.class);
    private final Timer timer = mock(Timer.class);
    private final ObjectNameFactory mockObjectNameFactory = mock(ObjectNameFactory.class);
    private final ObjectNameFactory concreteObjectNameFactory = reporter.getObjectNameFactory();

    @Before
    public void setUp() throws Exception {
        when(gauge.getValue()).thenReturn(1);

        when(counter.getCount()).thenReturn(100L);

        when(histogram.getCount()).thenReturn(1L);

        final Snapshot hSnapshot = mock(Snapshot.class);
        when(hSnapshot.getMax()).thenReturn(2L);
        when(hSnapshot.getMean()).thenReturn(3.0);
        when(hSnapshot.getMin()).thenReturn(4L);
        when(hSnapshot.getStdDev()).thenReturn(5.0);
        when(hSnapshot.getMedian()).thenReturn(6.0);
        when(hSnapshot.get75thPercentile()).thenReturn(7.0);
        when(hSnapshot.get95thPercentile()).thenReturn(8.0);
        when(hSnapshot.get98thPercentile()).thenReturn(9.0);
        when(hSnapshot.get99thPercentile()).thenReturn(10.0);
        when(hSnapshot.get999thPercentile()).thenReturn(11.0);

        when(histogram.getSnapshot()).thenReturn(hSnapshot);

        when(meter.getCount()).thenReturn(1L);
        when(meter.getMeanRate()).thenReturn(2.0);
        when(meter.getOneMinuteRate()).thenReturn(3.0);
        when(meter.getFiveMinuteRate()).thenReturn(4.0);
        when(meter.getFifteenMinuteRate()).thenReturn(5.0);

        when(timer.getCount()).thenReturn(1L);
        when(timer.getMeanRate()).thenReturn(2.0);
        when(timer.getOneMinuteRate()).thenReturn(3.0);
        when(timer.getFiveMinuteRate()).thenReturn(4.0);
        when(timer.getFifteenMinuteRate()).thenReturn(5.0);

        final Snapshot tSnapshot = mock(Snapshot.class);
        when(tSnapshot.getMax()).thenReturn(TimeUnit.MILLISECONDS.toNanos(100));
        when(tSnapshot.getMean()).thenReturn((double) TimeUnit.MILLISECONDS.toNanos(200));
        when(tSnapshot.getMin()).thenReturn(TimeUnit.MILLISECONDS.toNanos(300));
        when(tSnapshot.getStdDev()).thenReturn((double) TimeUnit.MILLISECONDS.toNanos(400));
        when(tSnapshot.getMedian()).thenReturn((double) TimeUnit.MILLISECONDS.toNanos(500));
        when(tSnapshot.get75thPercentile()).thenReturn((double) TimeUnit.MILLISECONDS.toNanos(600));
        when(tSnapshot.get95thPercentile()).thenReturn((double) TimeUnit.MILLISECONDS.toNanos(700));
        when(tSnapshot.get98thPercentile()).thenReturn((double) TimeUnit.MILLISECONDS.toNanos(800));
        when(tSnapshot.get99thPercentile()).thenReturn((double) TimeUnit.MILLISECONDS.toNanos(900));
        when(tSnapshot.get999thPercentile()).thenReturn((double) TimeUnit.MILLISECONDS.toNanos(1000));

        when(timer.getSnapshot()).thenReturn(tSnapshot);

        registry.register("gauge", gauge);
        registry.register("test.counter", counter);
        registry.register("test.histogram", histogram);
        registry.register("test.meter", meter);
        registry.register("test.another.timer", timer);

        reporter.start();
    }

    @After
    public void tearDown() throws Exception {
        reporter.stop();
    }

    @Test
    public void registersMBeansForMetricObjectsUsingProvidedObjectNameFactory() throws Exception {
        ObjectName n = new ObjectName(name + ":name=dummy");
        try {
            String widgetName = "something";
            when(mockObjectNameFactory.createName(any(String.class), any(String.class), any(MetricName.class))).thenReturn(n);
            Gauge aGauge = mock(Gauge.class);
            when(aGauge.getValue()).thenReturn(1);

            JmxReporter reporter = JmxReporter.forRegistry(registry)
                    .registerWith(mBeanServer)
                    .inDomain(name)
                    .createsObjectNamesWith(mockObjectNameFactory)
                    .build();
            registry.register(widgetName, aGauge);
            reporter.start();
            verify(mockObjectNameFactory).createName(eq("gauges"), any(String.class), eq(MetricName.build("something")));
            //verifyNoMoreInteractions(mockObjectNameFactory);
        } finally {
            reporter.stop();
            if(mBeanServer.isRegistered(n)) {
                mBeanServer.unregisterMBean(n);
            }
        }
    }

    @Test
    public void registersMBeansForGauges() throws Exception {
        final AttributeList attributes = getAttributes("gauge", "Value");

        Assertions.assertThat(values(attributes))
                .contains(Assertions.entry("Value", 1));
    }

    @Test
    public void registersMBeansForCounters() throws Exception {
        final AttributeList attributes = getAttributes("test.counter", "Count");

        Assertions.assertThat(values(attributes))
                .contains(Assertions.entry("Count", 100L));
    }

    @Test
    public void registersMBeansForHistograms() throws Exception {
        final AttributeList attributes = getAttributes("test.histogram",
                "Count",
                "Max",
                "Mean",
                "Min",
                "StdDev",
                "50thPercentile",
                "75thPercentile",
                "95thPercentile",
                "98thPercentile",
                "99thPercentile",
                "999thPercentile");

        Assertions.assertThat(values(attributes))
                .contains(Assertions.entry("Count", 1L))
                .contains(Assertions.entry("Max", 2L))
                .contains(Assertions.entry("Mean", 3.0))
                .contains(Assertions.entry("Min", 4L))
                .contains(Assertions.entry("StdDev", 5.0))
                .contains(Assertions.entry("50thPercentile", 6.0))
                .contains(Assertions.entry("75thPercentile", 7.0))
                .contains(Assertions.entry("95thPercentile", 8.0))
                .contains(Assertions.entry("98thPercentile", 9.0))
                .contains(Assertions.entry("99thPercentile", 10.0))
                .contains(Assertions.entry("999thPercentile", 11.0));
    }

    @Test
    public void registersMBeansForMeters() throws Exception {
        final AttributeList attributes = getAttributes("test.meter",
                "Count",
                "MeanRate",
                "OneMinuteRate",
                "FiveMinuteRate",
                "FifteenMinuteRate",
                "RateUnit");

        Assertions.assertThat(values(attributes))
                .contains(Assertions.entry("Count", 1L))
                .contains(Assertions.entry("MeanRate", 2.0))
                .contains(Assertions.entry("OneMinuteRate", 3.0))
                .contains(Assertions.entry("FiveMinuteRate", 4.0))
                .contains(Assertions.entry("FifteenMinuteRate", 5.0))
                .contains(Assertions.entry("RateUnit", "events/second"));
    }

    @Test
    public void registersMBeansForTimers() throws Exception {
        final AttributeList attributes = getAttributes("test.another.timer",
                "Count",
                "MeanRate",
                "OneMinuteRate",
                "FiveMinuteRate",
                "FifteenMinuteRate",
                "Max",
                "Mean",
                "Min",
                "StdDev",
                "50thPercentile",
                "75thPercentile",
                "95thPercentile",
                "98thPercentile",
                "99thPercentile",
                "999thPercentile",
                "RateUnit",
                "DurationUnit");

        Assertions.assertThat(values(attributes))
                .contains(Assertions.entry("Count", 1L))
                .contains(Assertions.entry("MeanRate", 2.0))
                .contains(Assertions.entry("OneMinuteRate", 3.0))
                .contains(Assertions.entry("FiveMinuteRate", 4.0))
                .contains(Assertions.entry("FifteenMinuteRate", 5.0))
                .contains(Assertions.entry("Max", 100.0))
                .contains(Assertions.entry("Mean", 200.0))
                .contains(Assertions.entry("Min", 300.0))
                .contains(Assertions.entry("StdDev", 400.0))
                .contains(Assertions.entry("50thPercentile", 500.0))
                .contains(Assertions.entry("75thPercentile", 600.0))
                .contains(Assertions.entry("95thPercentile", 700.0))
                .contains(Assertions.entry("98thPercentile", 800.0))
                .contains(Assertions.entry("99thPercentile", 900.0))
                .contains(Assertions.entry("999thPercentile", 1000.0))
                .contains(Assertions.entry("RateUnit", "events/second"))
                .contains(Assertions.entry("DurationUnit", "milliseconds"));
    }

    @Test
    public void cleansUpAfterItselfWhenStopped() throws Exception {
        reporter.stop();

        try {
            getAttributes("gauge", "Value");
            Assertions.failBecauseExceptionWasNotThrown(InstanceNotFoundException.class);
        } catch (InstanceNotFoundException e) {

        }
    }

    @Test
    public void objectNameModifyingMBeanServer() throws Exception {
        MBeanServer mockedMBeanServer = mock(MBeanServer.class);

        // overwrite the objectName
        when(mockedMBeanServer.registerMBean(any(Object.class), any(ObjectName.class))).thenReturn(new ObjectInstance("DOMAIN:key=value","className"));

        MetricRegistry testRegistry = new MetricRegistryImpl();
        JmxReporter testJmxReporter = JmxReporter.forRegistry(testRegistry)
                .registerWith(mockedMBeanServer)
                .inDomain(name)
                .build();

        testJmxReporter.start();

        // should trigger a registerMBean
        testRegistry.timer("test");

        // should trigger an unregisterMBean with the overwritten objectName = "DOMAIN:key=value"
        testJmxReporter.stop();

        verify(mockedMBeanServer).unregisterMBean(new ObjectName("DOMAIN:key=value"));

    }

    @Test
    public void testJmxMetricNameWithAsterisk() {
        MetricRegistry metricRegistry = new MetricRegistryImpl();
        JmxReporter.forRegistry(metricRegistry).build().start();
        metricRegistry.counter("test*");
    }

    private AttributeList getAttributes(String name, String... attributeNames) throws JMException {
        ObjectName n = concreteObjectNameFactory.createName("only-for-logging-error", this.name, MetricName.build(name));
        return mBeanServer.getAttributes(n, attributeNames);
    }

    private SortedMap<String, Object> values(AttributeList attributes) {
        final TreeMap<String, Object> values = new TreeMap<String, Object>();
        for (Object o : attributes) {
            final Attribute attribute = (Attribute) o;
            values.put(attribute.getName(), attribute.getValue());
        }
        return values;
    }
}
