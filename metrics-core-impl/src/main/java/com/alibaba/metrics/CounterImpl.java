package com.alibaba.metrics;

/**
 * An incrementing and decrementing counter metric.
 */
public class CounterImpl implements Counter {

    private final LongAdder count;

    public CounterImpl() {
        this.count = new LongAdder();
    }

    /**
     * Increment the counter by one.
     */
    public void inc() {
        inc(1);
    }

    /**
     * Increment the counter by {@code n}.
     *
     * @param n the amount by which the counter will be increased
     */
    public void inc(long n) {
        count.add(n);
    }

    /**
     * Decrement the counter by one.
     */
    public void dec() {
        dec(1);
    }

    /**
     * Decrement the counter by {@code n}.
     *
     * @param n the amount by which the counter will be decreased
     */
    public void dec(long n) {
        count.add(-n);
    }

    /**
     * Returns the counter's current value.
     *
     * @return the counter's current value
     */
    public long getCount() {
        return count.sum();
    }

    /**
     * Implementation notes:
     * Recording the last updated time for each update is very expensive according to JMH benchmark,
     * about 6x slower.
     * Because this implementation is only used internally, so this function just returns 0.
     * @return always return 0.
     */
    @Override
    public long lastUpdateTime() {
        return System.currentTimeMillis();
    }
}
