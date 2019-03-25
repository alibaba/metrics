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


public class BucketReservoirTest {

    @Test
    public void testAverageWhenBucketCountIsAvailable() {
        ManualClock clock = new ManualClock();
        BucketCounter total = new BucketCounterImpl(5, 10, clock);
        BucketReservoir bucketReservoir = new BucketReservoir(5, 10, clock, total);
        clock.addSeconds(5);
        bucketReservoir.update(10);
        bucketReservoir.update(20);
        total.update(2);
        clock.addSeconds(2);
        Snapshot snapshot = bucketReservoir.getSnapshot();
        assertThat(snapshot.getMean()).isEqualTo(15);
        assertThat(snapshot.get75thPercentile()).isEqualTo(Constants.NOT_AVAILABLE);
        assertThat(snapshot.get95thPercentile()).isEqualTo(Constants.NOT_AVAILABLE);
        assertThat(snapshot.get99thPercentile()).isEqualTo(Constants.NOT_AVAILABLE);
        assertThat(snapshot.getMax()).isEqualTo(Constants.NOT_AVAILABLE);
        assertThat(snapshot.getMin()).isEqualTo(Constants.NOT_AVAILABLE);

        // wait for 10 seconds
        clock.addSeconds(10);
        Snapshot s2 = bucketReservoir.getSnapshot();
        // should down to 0
        assertThat(s2.getMean()).isEqualTo(0);
    }
}
