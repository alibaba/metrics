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

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * A timer metric which aggregates timing durations and provides duration statistics, plus
 * throughput statistics via {@link Meter}.
 */
public class TimerImpl implements Timer {
    /**
     * A timing context.
     *
     * @see Timer#time()
     */
    public static class ContextImpl implements Context {
        private final Timer timer;
        private final Clock clock;
        private final long startTime;

        private ContextImpl(Timer timer, Clock clock) {
            this.timer = timer;
            this.clock = clock;
            this.startTime = clock.getTick();
        }

        /**
         * Updates the timer with the difference between current and start time. Call to this method will
         * not reset the start time. Multiple calls result in multiple updates.
         * @return the elapsed time in nanoseconds
         */
        public long stop() {
            final long elapsed = clock.getTick() - startTime;
            timer.update(elapsed, TimeUnit.NANOSECONDS);
            return elapsed;
        }

        /** Equivalent to calling {@link #stop()}. */
        public void close() {
            stop();
        }
    }

    private final Meter meter;
    private final Histogram histogram;
    private final Clock clock;

    /**
     * Creates a new {@link Timer} using an {@link ExponentiallyDecayingReservoir} and the default
     * {@link Clock}.
     */
    public TimerImpl() {
        this(ReservoirType.EXPONENTIALLY_DECAYING, Clock.defaultClock(), 60);
    }

    public TimerImpl(int interval) {
        this(ReservoirType.EXPONENTIALLY_DECAYING, Clock.defaultClock(), interval);
    }

    public TimerImpl(int interval, ReservoirType type) {
        this(type, Clock.defaultClock(), interval);
    }

    /**
     * Creates a new {@link Timer} that uses the given {@link Reservoir}.
     *
     * @param reservoir the {@link Reservoir} implementation the timer should use
     */
    public TimerImpl(ReservoirType reservoir) {
        this(reservoir, Clock.defaultClock(), 60);
    }

    /**
     * Creates a new {@link Timer} that uses the given {@link Reservoir} and {@link Clock}.
     *
     * @param reservoir the {@link Reservoir} implementation the timer should use
     * @param clock  the {@link Clock} implementation the timer should use
     * @param interval the interval in seconds for bucket counter
     */
    public TimerImpl(ReservoirType reservoir, Clock clock, int interval) {
        this.meter = new MeterImpl(clock, interval);
        this.clock = clock;
        this.histogram = new HistogramImpl(reservoir, interval, 10, clock);
    }

    public TimerImpl(Reservoir reservoir, Clock clock, int interval) {
        this.meter = new MeterImpl(clock, interval);
        this.clock = clock;
        this.histogram = new HistogramImpl(reservoir, interval, 10, clock);
    }

    /**
     * Adds a recorded duration.
     *
     * @param duration the length of the duration
     * @param unit     the scale unit of {@code duration}
     */
    public void update(long duration, TimeUnit unit) {
        update(unit.toNanos(duration));
    }

    /**
     * Times and records the duration of event.
     *
     * @param event a {@link Callable} whose {@link Callable#call()} method implements a process
     *              whose duration should be timed
     * @param <T>   the type of the value returned by {@code event}
     * @return the value returned by {@code event}
     * @throws Exception if {@code event} throws an {@link Exception}
     */
    public <T> T time(Callable<T> event) throws Exception {
        final long startTime = clock.getTick();
        try {
            return event.call();
        } finally {
            update(clock.getTick() - startTime);
        }
    }

    /**
     * Returns a new {@link Context}.
     *
     * @return a new {@link Context}
     * @see Context
     */
    public Context time() {
        return new ContextImpl(this, clock);
    }

    public long getCount() {
        return histogram.getCount();
    }

    public double getFifteenMinuteRate() {
        return meter.getFifteenMinuteRate();
    }

    public double getFiveMinuteRate() {
        return meter.getFiveMinuteRate();
    }

    public double getMeanRate() {
        return meter.getMeanRate();
    }

    public double getOneMinuteRate() {
        return meter.getOneMinuteRate();
    }

    @Override
    public Map<Long, Long> getInstantCount() {
        return meter.getInstantCount();
    }

    public Snapshot getSnapshot() {
        return histogram.getSnapshot();
    }

    @Override
    public int getInstantCountInterval() {
        return meter.getInstantCountInterval();
    }

    @Override
    public Map<Long, Long> getInstantCount(long startTime) {
        return meter.getInstantCount(startTime);
    }

    @Override
    public long lastUpdateTime() {
        return meter.lastUpdateTime();
    }

    private void update(long duration) {
        if (duration >= 0) {
            histogram.update(duration);
            meter.mark();
        }
    }
}
