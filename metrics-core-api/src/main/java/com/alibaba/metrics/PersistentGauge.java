package com.alibaba.metrics;

/**
 * A subclass of {@link Gauge} which should be persistent.
 * A gauge that is never invalidated.
 */
public abstract class PersistentGauge<T> implements Gauge<T> {

    /**
     * This gauge is always available, and be updated constantly.
     */
    @Override
    public long lastUpdateTime() {
        return System.currentTimeMillis();
    }
}
