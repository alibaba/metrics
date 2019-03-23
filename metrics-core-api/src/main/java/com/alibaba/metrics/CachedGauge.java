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
import java.util.concurrent.atomic.AtomicLong;

/**
 * A {@link Gauge} implementation which caches its value for a period of time.
 *
 * @param <T>    the type of the gauge's value
 */
public abstract class CachedGauge<T> implements Gauge<T> {
    private final Clock clock;
    private final AtomicLong reloadAt;
    private final long timeoutNS;
    private long lastUpdate = System.currentTimeMillis();

    private volatile T value;

    /**
     * Creates a new cached gauge with the given timeout period.
     *
     * @param timeout        the timeout
     * @param timeoutUnit    the unit of {@code timeout}
     */
    protected CachedGauge(long timeout, TimeUnit timeoutUnit) {
        this(Clock.defaultClock(), timeout, timeoutUnit);
    }

    /**
     * Creates a new cached gauge with the given clock and timeout period.
     *
     * @param clock          the clock used to calculate the timeout
     * @param timeout        the timeout
     * @param timeoutUnit    the unit of {@code timeout}
     */
    protected CachedGauge(Clock clock, long timeout, TimeUnit timeoutUnit) {
        this.clock = clock;
        this.reloadAt = new AtomicLong(clock.getTick());
        this.timeoutNS = timeoutUnit.toNanos(timeout);
    }

    /**
     * Loads the value and returns it.
     *
     * @return the new value
     */
    protected abstract T loadValue();

    public T getValue() {
        if (shouldLoad()) {
            this.value = loadValue();
            lastUpdate = System.currentTimeMillis();
        }
        return value;
    }

    @Override
    public long lastUpdateTime() {
        return lastUpdate;
    }

    private boolean shouldLoad() {
        for (; ; ) {
            final long currentTick = clock.getTick();
            final long reloadAtTick = reloadAt.get();
            if (currentTick < reloadAtTick) {
                return false;
            }
            if (reloadAt.compareAndSet(reloadAtTick, currentTick + timeoutNS)) {
                return true;
            }
        }
    }
}
