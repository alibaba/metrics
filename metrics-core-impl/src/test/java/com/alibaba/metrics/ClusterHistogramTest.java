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

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * @author wangtao 2019-01-16 11:08
 */
public class ClusterHistogramTest {

    private final Clock clock = mock(Clock.class);

    @Test
    public void testClusterHistogramImpl() {
        ClusterHistogram ch = new ClusterHistogramImpl(new long[]{1,10,100}, 5, clock);
        ch.update(0);
        ch.update(5);
        ch.update(6);
        ch.update(7);
        ch.update(20);
        ch.update(100);
        Map<Long, Map<Long, Long>> result = ch.getBucketValues(0);
        Assert.assertEquals(4, result.get(0L).size()); // there are four buckets
        Assert.assertEquals(1L, result.get(0L).get(1L).longValue());
        Assert.assertEquals(3L, result.get(0L).get(10L).longValue());
        Assert.assertEquals(1L, result.get(0L).get(100L).longValue());
        Assert.assertEquals(1L, result.get(0L).get(Long.MAX_VALUE).longValue());
    }

    @Test
    public void testGetByMetricManager() {
        ClusterHistogram ch = MetricManager.getClusterHistogram("test", MetricName.build("cluster"),
                new long[]{1, 10, 100});
        Assert.assertEquals("com.alibaba.metrics.ClusterHistogramImpl", ch.getClass().getName());
    }
}
