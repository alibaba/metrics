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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class CompassImpl implements Compass {

    private static final int MAX_ERROR_CODE_COUNT =
            Integer.getInteger("com.alibaba.metrics.maxCompassErrorCodeCount", 100);
    private static final int MAX_ADDON_COUNT =
            Integer.getInteger("com.alibaba.metrics.maxCompassAddonCount", 10);
    private static final long TICK_INTERVAL = TimeUnit.SECONDS.toNanos(5);

    /**
     * The count per collect interval
     */
    private final BucketCounter totalCount;
    /**
     * The number of successful count per collect interval
     */
    private final BucketCounter successCount;
    /**
     * The number of error count per code per collect interval
     */
    private final ConcurrentHashMap<String, BucketCounter> errorCodes;
    /**
     * The number of addon count per addon per collect interval
     */
    private final ConcurrentHashMap<String, BucketCounter> addons;

    /**
     * The 1-min moving average
     */
    private final EWMA m1Rate = EWMA.oneMinuteEWMA();

    /**
     * The 5-min moving average
     */
    private final EWMA m5Rate = EWMA.fiveMinuteEWMA();

    /**
     * The 15-min moving average
     */
    private final EWMA m15Rate = EWMA.fifteenMinuteEWMA();

    /**
     * The time of start collecting
     */
    private final long startTime;

    /**
     * The last tick timestamp
     */
    private final AtomicLong lastTick;

    /**
     * The number of events that is not update to moving average yet
     */
    private final LongAdder uncounted = new LongAdder();

    /**
     * The clock implementation
     */
    private final Clock clock;
    /**
     * The max number of error code that is allowed
     */
    private final int maxErrorCodeCount;
    /**
     * The max number of addon that is allowed
     */
    private final int maxAddonCount;
    /**
     * The collect interval
     */
    private final int bucketInterval;
    /**
     * The number of bucket
     */
    private int numberOfBucket = 10;
    /**
     * The reservoir implementation
     */
    private Reservoir reservoir;


    public class ContextImpl implements Context {
        private final Compass compass;
        private final Clock clock;
        private final long startTime;

        private ContextImpl(Compass compass, Clock clock) {
            this.compass = compass;
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
            compass.update(elapsed, TimeUnit.NANOSECONDS);
            return elapsed;
        }

        /** Equivalent to calling {@link #stop()}. */
        public void close() {
            stop();
        }

        @Override
        public void success() {
            successCount.update();
        }

        @Override
        public void error(String errorCode) {
            if (!errorCodes.containsKey(errorCode)) {
                if (errorCodes.keySet().size() >= maxErrorCodeCount) {
                    // ignore if maxErrorCodeCount is exceeded, no exception will be thrown
                    return;
                }
                errorCodes.putIfAbsent(errorCode, new BucketCounterImpl(bucketInterval, numberOfBucket, clock));
            }
            errorCodes.get(errorCode).update();
        }

        @Override
        public void markAddon(String suffix) {
            if (!addons.containsKey(suffix)) {
                if (addons.keySet().size() >= maxAddonCount) {
                    // ignore if maxAddonCount is exceeded, no exception will be thrown
                    return;
                }
                addons.putIfAbsent(suffix, new BucketCounterImpl(bucketInterval, numberOfBucket, clock));
            }
            addons.get(suffix).update();
        }
    }

    /**
     * Creates a new {@link CompassImpl} using an {@link ExponentiallyDecayingReservoir} and the default
     * {@link Clock}.
     */
    public CompassImpl() {
        this(ReservoirType.EXPONENTIALLY_DECAYING, Clock.defaultClock(),
                10, 60, MAX_ERROR_CODE_COUNT, MAX_ADDON_COUNT);
    }

    public CompassImpl(int bucketInterval) {
        this(ReservoirType.EXPONENTIALLY_DECAYING, Clock.defaultClock(), 10, bucketInterval,
                MAX_ERROR_CODE_COUNT, MAX_ADDON_COUNT);
    }

    public CompassImpl(int bucketInterval, ReservoirType type) {
        this(type, Clock.defaultClock(), 10, bucketInterval, MAX_ERROR_CODE_COUNT, MAX_ADDON_COUNT);
    }

    /**
     * Creates a new {@link CompassImpl} that uses the given {@link Reservoir} and {@link Clock}.
     *
     * @param type the {@link Reservoir} implementation the timer should use
     * @param clock  the {@link Clock} implementation the timer should use
     * @param maxErrorCodeCount the max number of error code allowed
     * @param bucketInterval the bucket interval
     * @param maxAddonCount the max number of add on allowed
     */
    public CompassImpl(ReservoirType type, Clock clock, int numberOfBucket, int bucketInterval,
                       int maxErrorCodeCount, int maxAddonCount) {
        this.bucketInterval = bucketInterval;
        this.numberOfBucket = numberOfBucket;
        this.totalCount = new BucketCounterImpl(bucketInterval, numberOfBucket, clock);
        this.clock = clock;
        this.startTime = this.clock.getTick();
        this.lastTick = new AtomicLong(this.startTime);
        this.successCount = new BucketCounterImpl(bucketInterval, numberOfBucket, clock);
        this.errorCodes = new ConcurrentHashMap<String, BucketCounter>();
        this.maxErrorCodeCount = maxErrorCodeCount;
        this.addons = new ConcurrentHashMap<String, BucketCounter>();
        this.maxAddonCount = maxAddonCount;
        switch (type) {
            case EXPONENTIALLY_DECAYING:
                this.reservoir = new ExponentiallyDecayingReservoir(clock);
                break;
            case SLIDING_TIME_WINDOW:
                this.reservoir = new SlidingTimeWindowReservoir(bucketInterval, TimeUnit.SECONDS);
                break;
            case SLIDING_WINDOW:
                this.reservoir = new SlidingWindowReservoir(1024);
                break;
            case UNIFORM:
                this.reservoir = new UniformReservoir(1024);
                break;
            case BUCKET:
                this.reservoir = new BucketReservoir(bucketInterval, numberOfBucket, clock, totalCount);
                break;
            default:
                this.reservoir = new ExponentiallyDecayingReservoir(clock);
        }
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
     * Adds a recorded duration
     * @param duration the length of the duration
     * @param unit the scale unit of {@code duration}
     * @param isSuccess whether it is success
     * @param errorCode the error code with this record, if not, null be passed
     * @param addon the addon with this record, if not, null be passed
     */
    public void update(long duration, TimeUnit unit, boolean isSuccess, String errorCode, String addon) {
        update(unit.toNanos(duration));
        if (isSuccess) {
            successCount.update();
        }
        if (null != errorCode) {
            if (!errorCodes.containsKey(errorCode) && errorCodes.keySet().size() < maxErrorCodeCount) {
                errorCodes.putIfAbsent(errorCode, new BucketCounterImpl(bucketInterval, numberOfBucket, clock));
            }
            errorCodes.get(errorCode).update();
        }
        if (null != addon) {
            if (!addons.containsKey(addon) && addons.keySet().size() < maxAddonCount) {
                addons.putIfAbsent(addon, new BucketCounterImpl(bucketInterval, numberOfBucket, clock));
            }
            addons.get(addon).update();
        }
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

    @Override
    public long getCount() {
        return totalCount.getCount();
    }

    @Override
    public double getFifteenMinuteRate() {
        tickIfNecessary();
        return m15Rate.getRate(TimeUnit.SECONDS);
    }

    @Override
    public double getFiveMinuteRate() {
        tickIfNecessary();
        return m5Rate.getRate(TimeUnit.SECONDS);
    }

    @Override
    public double getMeanRate() {
        if (getCount() == 0) {
            return 0.0;
        } else {
            final double elapsed = (clock.getTick() - startTime);
            return getCount() / elapsed * TimeUnit.SECONDS.toNanos(1);
        }
    }

    @Override
    public double getOneMinuteRate() {
        tickIfNecessary();
        return m1Rate.getRate(TimeUnit.SECONDS);
    }

    @Override
    public Map<Long, Long> getInstantCount() {
        return totalCount.getBucketCounts();
    }

    @Override
    public Snapshot getSnapshot() {
        return this.reservoir.getSnapshot();
    }

    public Map<String, BucketCounter> getErrorCodeCounts() {
        Map<String, BucketCounter> errorCodeMap = new HashMap<String, BucketCounter>();
        for (Map.Entry<String, BucketCounter> entry: errorCodes.entrySet()) {
            errorCodeMap.put(entry.getKey(), entry.getValue());
        }
        return errorCodeMap;
    }

    @Override
    public double getSuccessRate() {
        return Double.NaN;
    }

    @Override
    public long getSuccessCount() {
        return successCount.getCount();
    }

    @Override
    public BucketCounter getBucketSuccessCount() {
        return successCount;
    }

    @Override
    public int getInstantCountInterval() {
        return bucketInterval;
    }

    @Override
    public Map<String, BucketCounter> getAddonCounts() {
        Map<String, BucketCounter> addonsMap = new HashMap<String, BucketCounter>();
        for (Map.Entry<String, BucketCounter> entry: addons.entrySet()) {
            addonsMap.put(entry.getKey(), entry.getValue());
        }
        return addonsMap;
    }

    @Override
    public Map<Long, Long> getInstantCount(long startTime) {
        return totalCount.getBucketCounts(startTime);
    }

    /**
     * This is only for unit test
     * @param reservoir
     */
    public void setReservoir(Reservoir reservoir) {
        this.reservoir = reservoir;
    }

    @Override
    public long lastUpdateTime() {
        return totalCount.lastUpdateTime();
    }

    private void update(long duration) {
        if (duration >= 0) {
            tickIfNecessary();
            uncounted.add(1);
            totalCount.update();
            if (reservoir instanceof BucketReservoir) {
                if (duration > 0) {
                    // only update the reservoir when rt > 0
                    reservoir.update(duration);
                }
            } else {
                reservoir.update(duration);
            }
        }
    }

    private void tickIfNecessary() {
        final long oldTick = lastTick.get();
        final long newTick = clock.getTick();
        final long age = newTick - oldTick;
        if (age > TICK_INTERVAL) {
            final long newIntervalStartTick = newTick - age % TICK_INTERVAL;
            if (lastTick.compareAndSet(oldTick, newIntervalStartTick)) {
                final long requiredTicks = age / TICK_INTERVAL;
                for (long i = 0; i < requiredTicks; i++) {
                    final long count = uncounted.sumThenReset();
                    m1Rate.tick(count);
                    m5Rate.tick(count);
                    m15Rate.tick(count);
                }
            }
        }
    }
}
