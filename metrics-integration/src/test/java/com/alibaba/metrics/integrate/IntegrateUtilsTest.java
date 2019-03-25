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
package com.alibaba.metrics.integrate;

import com.alibaba.metrics.Counter;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.PersistentGauge;
import com.alibaba.metrics.common.filter.TimeMetricLevelFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Properties;

public class IntegrateUtilsTest {

    @Before
    public void setUp() {
        MetricManager.getIMetricManager().clear();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnumValueOf() {
        MetricLevel.valueOf("test");
    }

    @Test
    public void testEnabled() {
        Properties properties = new Properties();
        Assert.assertTrue(MetricsIntegrateUtils.isEnabled(properties, "test"));

        properties.put("com.alibaba.metrics.tomcat.thread.enable", "false");
        Assert.assertFalse(MetricsIntegrateUtils.isEnabled(properties, "com.alibaba.metrics.tomcat.thread.enable"));

        properties.put("com.alibaba.metrics.tomcat.thread.enable", "true");
        Assert.assertTrue(MetricsIntegrateUtils.isEnabled(properties, "com.alibaba.metrics.tomcat.thread.enable"));
    }

    @Ignore
    @Test
    public void testDisableJvmMetrics() {
        Properties properties = new Properties();
        properties.put("com.alibaba.metrics.jvm.gc.enable", "false");
        MetricsIntegrateUtils.registerJvmMetrics(properties);
        Map<MetricName, Gauge> gauges = MetricManager.getIMetricManager().getGauges("jvm", new TimeMetricLevelFilter());
        if (System.getProperty("java.version").startsWith("1.8")) {
            Assert.assertEquals(67, gauges.size());
        } else if (System.getProperty("java.version").startsWith("1.7")) {
            Assert.assertEquals(61, gauges.size());
        }
    }

    @Test
    public void testConfigMetricLevel() {
        Properties properties = new Properties();
        properties.put("com.alibaba.metrics.jvm.class_load.level", "CRITICAL");
        MetricsIntegrateUtils.registerJvmMetrics(properties);
        Map<MetricName, Gauge> gauges = MetricManager.getIMetricManager().getGauges("jvm", new MetricFilter() {
            @Override
            public boolean matches(MetricName name, Metric metric) {
                return name.getKey().equals("jvm.class_load.loaded");
            }
        });
        Assert.assertEquals(1, gauges.size());
        Assert.assertEquals(MetricLevel.CRITICAL, gauges.entrySet().iterator().next().getKey().getMetricLevel());
    }

    @Ignore
    @Test
    public void testInitEvenConfigFileIsNull() {
        MetricsIntegrateUtils.registerMetrics(null);
        Map<MetricName, Gauge> gauges = MetricManager.getIMetricManager().getGauges("jvm", new TimeMetricLevelFilter());
        if (System.getProperty("java.version").startsWith("1.8")) {
            Assert.assertEquals(71, gauges.size());
        } else if (System.getProperty("java.version").startsWith("1.7")) {
            Assert.assertEquals(65, gauges.size());
        }
    }


    @Test
    public void testGenerateScrapeConfig() throws Exception {
        String tmpFile = "/tmp/.metrics_scrape_config";
        System.setProperty("com.alibaba.metrics.http.port", "8006");
        Assert.assertTrue(MetricsIntegrateUtils.generateScrapeConfigFile(tmpFile));
        BufferedReader br = new BufferedReader(new FileReader(tmpFile));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null ) {
            sb.append(line);
        }
        br.close();
        Assert.assertTrue(new File(tmpFile).delete());
    }

    @Test
    public void testCleaner() {
        Counter c = MetricManager.getCounter("cleaner", MetricName.build("test.cleaner"));
        c.inc();
        Properties properties = new Properties();
        properties.put(ConfigFields.METRICS_CLEANER_ENABLE, "true");
        properties.put(ConfigFields.METRICS_CLEANER_KEEP_INTERVAL, "1");
        properties.put(ConfigFields.METRICS_CLEANER_DELAY, "1");
        MetricsIntegrateUtils.startMetricsCleaner(properties);
        try {
            Thread.sleep(2000);
            Assert.assertEquals(0,
                    MetricManager.getIMetricManager().getCounters("cleaner", MetricFilter.ALL).size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MetricsIntegrateUtils.stopMetricsCleaner();
    }

    @Test
    public void testDisableCleaner() {
        Counter c = MetricManager.getCounter("cleaner2", MetricName.build("test.cleaner2"));
        c.inc();
        Properties properties = new Properties();
        properties.put(ConfigFields.METRICS_CLEANER_ENABLE, "false");
        MetricsIntegrateUtils.startMetricsCleaner(properties);
        try {
            Thread.sleep(2000);
            Assert.assertEquals(1,
                    MetricManager.getIMetricManager().getCounters("cleaner2", MetricFilter.ALL).size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MetricsIntegrateUtils.stopMetricsCleaner();
    }

    @Test
    public void testCleanPersistentGauge() {
        PersistentGauge<Integer> g = new PersistentGauge<Integer>() {
            @Override
            public Integer getValue() {
                return 1;
            }
        };
        MetricManager.register("ppp", MetricName.build("ppp1"), g);
        Properties properties = new Properties();
        properties.put(ConfigFields.METRICS_CLEANER_ENABLE, "true");
        properties.put(ConfigFields.METRICS_CLEANER_KEEP_INTERVAL, "1");
        properties.put(ConfigFields.METRICS_CLEANER_DELAY, "1");
        MetricsIntegrateUtils.startMetricsCleaner(properties);
        try {
            Thread.sleep(2000);
            Assert.assertEquals(1,
                    MetricManager.getIMetricManager().getGauges("ppp", MetricFilter.ALL).size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MetricsIntegrateUtils.stopMetricsCleaner();
    }

    @Test
    public void testDoNotBeCleaned() {
        MetricManager.getCounter("cleaner3", MetricName.build("test.cleaner"));
        Properties properties = new Properties();
        properties.put(ConfigFields.METRICS_CLEANER_ENABLE, "true");
        properties.put(ConfigFields.METRICS_CLEANER_KEEP_INTERVAL, "10");
        properties.put(ConfigFields.METRICS_CLEANER_DELAY, "1");
        MetricsIntegrateUtils.startMetricsCleaner(properties);
        try {
            Thread.sleep(2000);
            Assert.assertEquals("Because keep interval is 10 seconds, the counter should not be cleaned.",
                    1, MetricManager.getIMetricManager().getCounters("cleaner3", MetricFilter.ALL).size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MetricsIntegrateUtils.stopMetricsCleaner();
    }

    @Test
    public void testDisableFromSystemProperty() {
        System.setProperty(ConfigFields.METRICS_CLEANER_ENABLE, "false");
        Assert.assertFalse(MetricsIntegrateUtils.isEnabled(null, ConfigFields.METRICS_CLEANER_ENABLE));
        System.setProperty(ConfigFields.METRICS_CLEANER_ENABLE, "true");
    }

    @Test
    public void testDisableDruidOrder() {
        System.clearProperty(ConfigFields.DRUID_FIELD);
        // check system property first
        Assert.assertFalse(MetricsIntegrateUtils.isEnabled(null, ConfigFields.DRUID_FIELD, false));
        // system property over input properties
        System.setProperty(ConfigFields.DRUID_FIELD, "true");
        Properties properties = new Properties();
        properties.setProperty(ConfigFields.DRUID_FIELD, "false");
        Assert.assertTrue(MetricsIntegrateUtils.isEnabled(properties, ConfigFields.DRUID_FIELD));
        System.clearProperty(ConfigFields.DRUID_FIELD);
    }
}
