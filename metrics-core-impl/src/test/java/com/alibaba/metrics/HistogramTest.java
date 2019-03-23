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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HistogramTest {
    private final Reservoir reservoir = mock(Reservoir.class);
    private final Histogram histogram = new HistogramImpl(reservoir, 10, 2, Clock.defaultClock());

    @Test
    public void updatesTheCountOnUpdates() throws Exception {
        assertThat(histogram.getCount())
                .isZero();

        histogram.update(1);

        assertThat(histogram.getCount())
                .isEqualTo(1);
    }

    @Test
    public void returnsTheSnapshotFromTheReservoir() throws Exception {
        final Snapshot snapshot = mock(Snapshot.class);
        when(reservoir.getSnapshot()).thenReturn(snapshot);

        assertThat(histogram.getSnapshot())
                .isEqualTo(snapshot);
    }

    @Test
    public void updatesTheReservoir() throws Exception {
        histogram.update(1);

        verify(reservoir).update(1);
    }

    @Test
    public void testBucketReservoir() {
        ManualClock clock = new ManualClock();
        Histogram histogram = new HistogramImpl(ReservoirType.BUCKET, 5, 2, clock);
        clock.addSeconds(10);
        histogram.update(10);
        histogram.update(20);
        Snapshot snapshot = histogram.getSnapshot();
        assertThat(snapshot.getMean()).isEqualTo(15);
        clock.addSeconds(6);
        histogram.update(200);
        histogram.update(400);
        clock.addSeconds(5);
        Snapshot snapshot2 = histogram.getSnapshot();
        assertThat(snapshot2.getMean()).isEqualTo(300);
    }
}
