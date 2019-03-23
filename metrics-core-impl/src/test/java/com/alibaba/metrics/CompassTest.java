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

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class CompassTest {

    private final Reservoir reservoir = mock(Reservoir.class);
    private final Clock clock = new Clock() {
        // a mock clock that increments its ticker by 50 ms per call
        private long val = 0;

        @Override
        public long getTick() {
            return val += 50000000;
        }
    };

    private CompassImpl compass;

    @Before
    public void setUp() {
        compass = new CompassImpl(ReservoirType.BUCKET, clock, 10, 60, 100, 2);
        compass.setReservoir(reservoir);
    }

    @Test
    public void hasRates() throws Exception {
        assertThat(compass.getCount())
                .isZero();

        assertThat(compass.getMeanRate())
                .isEqualTo(0.0, offset(0.001));

        assertThat(compass.getOneMinuteRate())
                .isEqualTo(0.0, offset(0.001));

        assertThat(compass.getFiveMinuteRate())
                .isEqualTo(0.0, offset(0.001));

        assertThat(compass.getFifteenMinuteRate())
                .isEqualTo(0.0, offset(0.001));
    }

    @Test
    public void updatesTheCountOnUpdates() throws Exception {
        assertThat(compass.getCount())
                .isZero();

        compass.update(1, TimeUnit.SECONDS);

        assertThat(compass.getCount())
                .isEqualTo(1);
    }

    @Test
    public void timesCallableInstances() throws Exception {
        final String value = compass.time(new Callable<String>() {
            public String call() throws Exception {
                return "one";
            }
        });

        assertThat(compass.getCount())
                .isEqualTo(1);

        assertThat(value)
                .isEqualTo("one");

        verify(reservoir).update(50000000);
    }

    @Test
    public void timesContexts() throws Exception {
        compass.time().stop();

        assertThat(compass.getCount())
                .isEqualTo(1);

        verify(reservoir).update(50000000);
    }

    @Test
    public void returnsTheSnapshotFromTheReservoir() throws Exception {
        final Snapshot snapshot = mock(Snapshot.class);
        when(reservoir.getSnapshot()).thenReturn(snapshot);

        assertThat(compass.getSnapshot())
                .isEqualTo(snapshot);
    }

    @Test
    public void ignoresNegativeValues() throws Exception {
        compass.update(-1, TimeUnit.SECONDS);

        assertThat(compass.getCount())
                .isZero();

        verifyZeroInteractions(reservoir);
    }

    @Test
    public void testErrorCode() {
        Compass.Context context = compass.time();
        context.error("code1");
        context.stop();

        assertThat(compass.getCount()).isEqualTo(1);

        assertThat(compass.getErrorCodeCounts().get("code1").getCount()).isEqualTo(1);

        verify(reservoir).update(50000000);
    }

    @Test
    public void testMaxErrorCodeCount() {
        Compass c = new CompassImpl(ReservoirType.EXPONENTIALLY_DECAYING, Clock.defaultClock(), 10, 60, 3, 5);
        Compass.Context context = c.time();
        context.error("code1");
        context.error("code2");
        context.error("code3");
        context.error("code4");
        context.stop();

        assertThat(c.getErrorCodeCounts().size()).isEqualTo(3);

        assertThat(c.getErrorCodeCounts().get("code1").getCount()).isEqualTo(1);
        assertThat(c.getErrorCodeCounts().get("code2").getCount()).isEqualTo(1);
        assertThat(c.getErrorCodeCounts().get("code3").getCount()).isEqualTo(1);
        assertThat(c.getErrorCodeCounts().get("code4")).isNull();

    }

    @Test
    public void testAddon() {
        Compass.Context context = compass.time();
        context.markAddon("hit");
        context.markAddon("loss");
        context.markAddon("goodbye");
        context.stop();

        assertThat(compass.getCount()).isEqualTo(1);

        assertThat(compass.getAddonCounts().get("hit").getCount()).isEqualTo(1);
        assertThat(compass.getAddonCounts().get("loss").getCount()).isEqualTo(1);
        assertThat(compass.getAddonCounts().get("goodbye")).isNull();

        verify(reservoir).update(50000000);
    }

    @Test
    public void testCompassBucket() {
        ManualClock clock = new ManualClock();
        Compass c = new CompassImpl(ReservoirType.EXPONENTIALLY_DECAYING, clock, 10, 60, 10, 5);
        Compass.Context context = c.time();
        clock.addSeconds(60);
        context.error("code1");
        clock.addSeconds(30);
        context.markAddon("hit");
        context.success();
        context.stop();

        assertThat(c.getCount()).isEqualTo(1);
        assertThat(c.getInstantCountInterval()).isEqualTo(60);

        assertThat(c.getInstantCount().get(60000L)).isEqualTo(1);
        assertThat(c.getAddonCounts().get("hit").getBucketCounts().get(60000L)).isEqualTo(1);
        assertThat(c.getBucketSuccessCount().getBucketCounts().get(60000L)).isEqualTo(1);
        assertThat(c.getCount()).isEqualTo(1);
    }

    @Test
    public void testSkipZeroResponseTime() {
        ManualClock clock = new ManualClock();
        Compass compass = new CompassImpl(ReservoirType.BUCKET, clock, 10, 60, 10, 5);

        compass.update(1, TimeUnit.MILLISECONDS);
        compass.update(1, TimeUnit.MILLISECONDS);
        compass.update(1, TimeUnit.MILLISECONDS);
        compass.update(0, TimeUnit.MILLISECONDS);
        compass.update(0, TimeUnit.MILLISECONDS);
        compass.update(0, TimeUnit.MILLISECONDS);

        clock.addSeconds(60);
        assertThat(compass.getCount()).isEqualTo(6);
        // average rt should be 0.5ms
        assertThat(compass.getSnapshot().getMean()).isEqualTo(TimeUnit.MICROSECONDS.toNanos(500));
    }

    @Test
    public void testUpdateWithSuccessAndAddon() {
        ManualClock clock = new ManualClock();
        Compass compass = new CompassImpl(ReservoirType.BUCKET, clock, 10, 60, 10, 5);
        compass.update(10, TimeUnit.MILLISECONDS, true, null, "hit");
        compass.update(15, TimeUnit.MILLISECONDS, true, null, null);
        compass.update(20, TimeUnit.MILLISECONDS, false, "error1", null);
        clock.addSeconds(60);
        assertThat(compass.getCount()).isEqualTo(3);
        assertThat(compass.getSuccessCount()).isEqualTo(2);
        assertThat(compass.getErrorCodeCounts()).containsKey("error1");
        assertThat(compass.getErrorCodeCounts().get("error1").getBucketCounts().get(0L)).isEqualTo(1);
        assertThat(compass.getAddonCounts().containsKey("hit"));
        assertThat(compass.getAddonCounts().get("hit").getBucketCounts().get(0L)).isEqualTo(1);
        assertThat(compass.getSnapshot().getMean()).isEqualTo(TimeUnit.MILLISECONDS.toNanos(15));

        compass.update(10, TimeUnit.MILLISECONDS, true, null, "hit");
        compass.update(15, TimeUnit.MILLISECONDS, true, null, null);
        compass.update(20, TimeUnit.MILLISECONDS, false, "error1", null);

        clock.addSeconds(60);
        assertThat(compass.getCount()).isEqualTo(6);
        assertThat(compass.getInstantCount().get(60000L)).isEqualTo(3);
        assertThat(compass.getSuccessCount()).isEqualTo(4);
        assertThat(compass.getBucketSuccessCount().getBucketCounts().get(60000L)).isEqualTo(2);
        assertThat(compass.getErrorCodeCounts()).containsKey("error1");
        assertThat(compass.getErrorCodeCounts().get("error1").getBucketCounts().get(60000L)).isEqualTo(1);
        assertThat(compass.getAddonCounts().containsKey("hit"));
        assertThat(compass.getAddonCounts().get("hit").getBucketCounts().get(60000L)).isEqualTo(1);
        assertThat(compass.getSnapshot().getMean()).isEqualTo(TimeUnit.MILLISECONDS.toNanos(15));
    }

}
