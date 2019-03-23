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

import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.common.MetricsCollector;
import com.alibaba.metrics.common.MetricsCollectorFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MetricsCollectorTest {

    @Test
    public void testDoNotNaNValue() {
        MetricsCollector collector = MetricsCollectorFactory.createNew(TimeUnit.SECONDS.toSeconds(1),
                1.0 / TimeUnit.MILLISECONDS.toNanos(1), null);

        Timer t1 = MetricManager.getTimer("abc", MetricName.build("Test"), ReservoirType.BUCKET);
        t1.update(20, TimeUnit.MILLISECONDS);

        collector.collect(MetricName.build("Test"), t1, System.currentTimeMillis());
        // no .min, .max, .p95 etc.
        Assert.assertEquals(8, collector.build().size());
    }

    @Test
    public void testCollectCompass() {
        MetricsCollector collector = MetricsCollectorFactory.createNew(TimeUnit.SECONDS.toSeconds(1),
                1.0 / TimeUnit.MILLISECONDS.toNanos(1), null);

        Compass c1 = MetricManager.getCompass("abc", MetricName.build("222"), ReservoirType.BUCKET);
        c1.update(20, TimeUnit.MILLISECONDS);
        Compass.Context context = c1.time();
        context.error("error1");
        Compass.Context context2 = c1.time();
        context2.error("error2");

        collector.collect(MetricName.build("Test"), c1, System.currentTimeMillis());

        Map<String, String> tags = new HashMap<String, String>();
        tags.put("error", "error1");
        MetricObject errorObj = MetricObject.named("Test.error.count").withTags(tags)
                .withType(MetricObject.MetricType.COUNTER).withLevel(MetricLevel.NORMAL).build();

        Map<String, String> tags2 = new HashMap<String, String>();
        tags2.put("error", "error2");
        MetricObject errorObj2 = MetricObject.named("Test.error.count").withTags(tags2)
                .withType(MetricObject.MetricType.COUNTER).withLevel(MetricLevel.NORMAL).build();
        Assert.assertTrue(collector.build().contains(errorObj));
        Assert.assertTrue(collector.build().contains(errorObj2));
    }

    @Test
    public void testCollectBucketSumForFastCompass() {
        MetricsCollector collector = MetricsCollectorFactory.createNew(TimeUnit.SECONDS.toSeconds(1),
                1.0 / TimeUnit.MILLISECONDS.toNanos(1), null);

        FastCompass c1 = MetricManager.getFastCompass("abc", MetricName.build("fff").level(MetricLevel.MAJOR));
        c1.record(20, "success");
        c1.record(40, "fail");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // ignore
        }

        collector.collect(MetricName.build("fff"), c1, System.currentTimeMillis());

        MetricObject bucketSum = MetricObject.named("fff.bucket_sum")
                .withType(MetricObject.MetricType.DELTA).withLevel(MetricLevel.NORMAL).build();

        List<MetricObject> result = collector.build();
        int index = result.indexOf(bucketSum);
        Assert.assertTrue(index >= 0);
        Assert.assertEquals(60L, result.get(index).getValue());
    }

    @Test
    public void testGaugeInterval() {
        MetricsCollector collector = MetricsCollectorFactory.createNew(TimeUnit.SECONDS.toSeconds(1),
                1.0 / TimeUnit.MILLISECONDS.toNanos(1), null);

        collector.collect(MetricName.build("zzz").level(MetricLevel.CRITICAL), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return 0L;
            }
        }, System.currentTimeMillis());

        List<MetricObject> result = collector.build();
        Assert.assertTrue(result.size() == 1);
        Assert.assertEquals(1, result.get(0).getInterval());
    }
}
