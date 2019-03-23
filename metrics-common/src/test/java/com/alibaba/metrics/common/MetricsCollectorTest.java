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
package com.alibaba.metrics.common;

import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.MetricName;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class MetricsCollectorTest {

    @Test
    public void testDoNotCollectNullValue() {
        MetricsCollector collector =
                MetricsCollectorFactory.createNew(TimeUnit.SECONDS.toSeconds(1),
                        1.0 / TimeUnit.MILLISECONDS.toNanos(1), null);
        collector.collect(MetricName.build("TEST"), new Gauge<Object>() {
            @Override
            public Object getValue() {
                return null;
            }
            @Override
            public long lastUpdateTime() {
                return 0;
            }
        }, System.currentTimeMillis());
        // null value should not be collected.
        Assert.assertEquals(0, collector.build().size());
    }
}
