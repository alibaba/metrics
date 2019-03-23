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

import java.util.concurrent.TimeUnit;

/**
 * A metric which calculates the distribution of a value.
 *
 * @see <a href="http://www.johndcook.com/standard_deviation.html">Accurately computing running
 *      variance</a>
 */
public class HistogramImpl implements Histogram {

    private final Reservoir reservoir;
    private final BucketCounter count;

    /**
     * Creates a new {@link Histogram} with the given reservoir.
     *
     * @param type the reservoir type to create a histogram from
     */
    public HistogramImpl(ReservoirType type) {
        this(type, 60, 10, Clock.defaultClock());
    }

    public HistogramImpl(int interval, ReservoirType type) {
        this(type, interval, 10, Clock.defaultClock());
    }

    public HistogramImpl(int interval) {
        this(ReservoirType.EXPONENTIALLY_DECAYING, interval, 10, Clock.defaultClock());
    }

    /**
     * Creates a new {@link Histogram} with the given reservoir.
     *
     * @param type the reservoir type to create a histogram from
     * @param interval the interval to create bucket counter
     * @param numberOfBucket the number of bucket to create bucket counter
     * @param clock the clock the create bucket counter
     */
    public HistogramImpl(ReservoirType type, int interval, int numberOfBucket, Clock clock) {
        this.count = new BucketCounterImpl(interval, numberOfBucket, clock);
        switch (type) {
            case EXPONENTIALLY_DECAYING:
                this.reservoir = new ExponentiallyDecayingReservoir(clock);
                break;
            case SLIDING_TIME_WINDOW:
                this.reservoir = new SlidingTimeWindowReservoir(interval, TimeUnit.SECONDS);
                break;
            case SLIDING_WINDOW:
                this.reservoir = new SlidingWindowReservoir(1024);
                break;
            case UNIFORM:
                this.reservoir = new UniformReservoir(1024);
                break;
            case BUCKET:
                this.reservoir = new BucketReservoir(interval, numberOfBucket, clock, count);
                break;
            default:
                this.reservoir = new ExponentiallyDecayingReservoir(clock);
        }
    }

    public HistogramImpl(Reservoir reservoir, int interval, int numberOfBucket, Clock clock) {
        this.reservoir = reservoir;
        this.count = new BucketCounterImpl(interval, numberOfBucket, clock);
    }

    /**
     * Adds a recorded value.
     *
     * @param value the length of the value
     */
    public void update(int value) {
        update((long) value);
    }

    /**
     * Adds a recorded value.
     *
     * @param value the length of the value
     */
    public void update(long value) {
        count.update();
        reservoir.update(value);
    }

    /**
     * Returns the number of values recorded.
     *
     * @return the number of values recorded
     */
    public long getCount() {
        return count.getCount();
    }

    public Snapshot getSnapshot() {
        return reservoir.getSnapshot();
    }

    @Override
    public long lastUpdateTime() {
        return count.lastUpdateTime();
    }
}
