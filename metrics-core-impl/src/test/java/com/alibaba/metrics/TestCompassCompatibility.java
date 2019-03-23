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
package com.alibaba.metrics;

import com.alibaba.metrics.common.CollectLevel;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.common.MetricsCollector;
import com.alibaba.metrics.common.MetricsCollectorFactory;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCompassCompatibility {

    @Test()
    public void testQps() {
        MetricRegistry registry = new MetricRegistryImpl();
        Compass c = registry.compass(MetricName.build("TEST").level(MetricLevel.CRITICAL), ReservoirType.BUCKET);
        c.update(30, TimeUnit.MILLISECONDS);
        c.update(20, TimeUnit.MILLISECONDS);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double rateFactor = TimeUnit.SECONDS.toSeconds(1);
        double durationFactor = 1.0 / TimeUnit.MILLISECONDS.toNanos(1);
        MetricsCollector collector = MetricsCollectorFactory.createNew(
                CollectLevel.COMPACT, Collections.EMPTY_MAP, rateFactor, durationFactor, null);
        collector.collect(MetricName.build("aaa"), c, System.currentTimeMillis());
        List<MetricObject> objs = collector.build();
        assertThat(collector.build().size()).isEqualTo(9);
        for (MetricObject obj: objs) {
            if (obj.getMetric().equals("aaa.qps")) {
                assertThat(obj.getValue()).isEqualTo(2.0);
            }
            if (obj.getMetric().equals("aaa.rt")) {
                assertThat(obj.getValue()).isEqualTo(25.0);
            }
            if (obj.getMetric().equals("aaa.mean")) {
                assertThat(obj.getValue()).isEqualTo(25.0);
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MetricsCollector c2 = MetricsCollectorFactory.createNew(
                CollectLevel.COMPACT, Collections.EMPTY_MAP, rateFactor, durationFactor, null);
        c2.collect(MetricName.build("aaa"), c, System.currentTimeMillis());
        List<MetricObject> objs2 = c2.build();
        assertThat(collector.build().size()).isEqualTo(9);

        for (MetricObject obj: objs2) {
            if (obj.getMetric().equals("aaa.qps")) {
                assertThat(obj.getValue()).isEqualTo(0.0);
            }
            if (obj.getMetric().equals("aaa.rt")) {
                assertThat(obj.getValue()).isEqualTo(0.0);
            }
            if (obj.getMetric().equals("aaa.mean")) {
                assertThat(obj.getValue()).isEqualTo(0.0);
            }
        }
    }

    @Test()
    public void testSuccessCount() {
        MetricRegistry registry = new MetricRegistryImpl();
        Compass c = registry.compass(MetricName.build("TEST").level(MetricLevel.CRITICAL), ReservoirType.BUCKET);
        c.update(30, TimeUnit.MILLISECONDS);
        Compass.Context context = c.time();
        context.success();
        context.markAddon("hit");
        c.update(20, TimeUnit.MILLISECONDS);
        c.time().error("error1");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double rateFactor = TimeUnit.SECONDS.toSeconds(1);
        double durationFactor = 1.0 / TimeUnit.MILLISECONDS.toNanos(1);
        MetricsCollector collector = MetricsCollectorFactory.createNew(
                CollectLevel.COMPACT, Collections.EMPTY_MAP, rateFactor, durationFactor, null);
        collector.collect(MetricName.build("aaa"), c, System.currentTimeMillis());
        List<MetricObject> objs = collector.build();
        assertThat(collector.build().size()).isEqualTo(14);
        for (MetricObject obj: objs) {
            if (obj.getMetric().equals("aaa.success_count")) {
                assertThat(obj.getValue()).isEqualTo(1L);
            } else if (obj.getMetric().equals("aaa.success_bucket_count")) {
                assertThat(obj.getValue()).isEqualTo(1L);
            } else if (obj.getMetric().equals("aaa.success_rate")) {
                assertThat(obj.getValue()).isEqualTo(0.5);
            } else if (obj.getMetric().equals("aaa.error.count")) {
                assertThat(obj.getValue()).isEqualTo(1L);
            } else if (obj.getMetric().equals("aaa.error_bucket_count")) {
                assertThat(obj.getValue()).isEqualTo(1L);
            } else if (obj.getMetric().equals("aaa.hit.count")) {
                assertThat(obj.getValue()).isEqualTo(1L);
            } else if (obj.getMetric().equals("aaa.hit_bucket_count")) {
                assertThat(obj.getValue()).isEqualTo(1L);
            } else if (obj.getMetric().equals("aaa.hit_rate")) {
                assertThat(obj.getValue()).isEqualTo(1.0d);
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MetricsCollector c2 = MetricsCollectorFactory.createNew(
                CollectLevel.COMPACT, Collections.EMPTY_MAP, rateFactor, durationFactor, null);
        c2.collect(MetricName.build("aaa"), c, System.currentTimeMillis());
        List<MetricObject> objs2 = c2.build();
        assertThat(collector.build().size()).isEqualTo(14);

        for (MetricObject obj: objs2) {
            if (obj.getMetric().equals("aaa.success_count")) {
                assertThat(obj.getValue()).isEqualTo(1L);
            } else if (obj.getMetric().equals("aaa.success_bucket_count")) {
                assertThat(obj.getValue()).isEqualTo(0L);
            } else if (obj.getMetric().equals("aaa.success_rate")) {
                assertThat(obj.getValue()).isEqualTo(0.0);
            } else if (obj.getMetric().equals("aaa.error.count")) {
                assertThat(obj.getValue()).isEqualTo(1L);
            } else if (obj.getMetric().equals("aaa.error_bucket_count")) {
                assertThat(obj.getValue()).isEqualTo(0L);
            } else if (obj.getMetric().equals("aaa.hit.count")) {
                assertThat(obj.getValue()).isEqualTo(1L);
            } else if (obj.getMetric().equals("aaa.hit_bucket_count")) {
                assertThat(obj.getValue()).isEqualTo(0L);
            } else if (obj.getMetric().equals("aaa.hit_rate")) {
                assertThat(obj.getValue()).isEqualTo(0.0d);
            }
        }
    }
}
