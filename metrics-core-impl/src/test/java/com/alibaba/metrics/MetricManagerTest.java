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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MetricManagerTest {

    @Test
    public void testGetHistogramWithType() {
        Histogram his = MetricManager.getHistogram("test", MetricName.build("AAA"),
                ReservoirType.EXPONENTIALLY_DECAYING);
        assertThat(his.getSnapshot()).isInstanceOf(WeightedSnapshot.class);

        Histogram his2 = MetricManager.getHistogram("test", MetricName.build("BBB"),
                ReservoirType.BUCKET);
        assertThat(his2.getSnapshot()).isInstanceOf(BucketSnapshot.class);

        Histogram his3 = MetricManager.getHistogram("test", MetricName.build("CCC"),
                ReservoirType.SLIDING_TIME_WINDOW);
        assertThat(his3.getSnapshot()).isInstanceOf(UniformSnapshot.class);
    }

    @Test
    public void testGetTimerWithType() {
        Timer t = MetricManager.getTimer("test", MetricName.build("AAA1"),
                ReservoirType.EXPONENTIALLY_DECAYING);
        assertThat(t.getSnapshot()).isInstanceOf(WeightedSnapshot.class);

        Timer t2 = MetricManager.getTimer("test", MetricName.build("BBB1"),
                ReservoirType.BUCKET);
        assertThat(t2.getSnapshot()).isInstanceOf(BucketSnapshot.class);

        Timer t3 = MetricManager.getTimer("test", MetricName.build("CCC1"),
                ReservoirType.SLIDING_TIME_WINDOW);
        assertThat(t3.getSnapshot()).isInstanceOf(UniformSnapshot.class);
    }

    @Test
    public void testGetCompassWithType() {
        Compass c = MetricManager.getCompass("test", MetricName.build("AAA2"),
                ReservoirType.EXPONENTIALLY_DECAYING);
        assertThat(c.getSnapshot()).isInstanceOf(WeightedSnapshot.class);

        Compass c2 = MetricManager.getCompass("test", MetricName.build("BBB2"),
                ReservoirType.BUCKET);
        assertThat(c2.getSnapshot()).isInstanceOf(BucketSnapshot.class);

        Compass c3 = MetricManager.getCompass("test", MetricName.build("CCC2"),
                ReservoirType.SLIDING_TIME_WINDOW);
        assertThat(c3.getSnapshot()).isInstanceOf(UniformSnapshot.class);
    }
}
