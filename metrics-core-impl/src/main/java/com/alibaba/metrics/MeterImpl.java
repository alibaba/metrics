package com.alibaba.metrics;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A meter metric which measures mean throughput and one-, five-, and fifteen-minute
 * exponentially-weighted moving average throughputs.
 *
 * @see EWMA
 */
public class MeterImpl implements Meter {

    private static final long TICK_INTERVAL = TimeUnit.SECONDS.toNanos(5);
    private static final int DEFAULT_NUM_OF_BUCKET =
            Integer.getInteger("com.alibaba.metrics.numberOfBucket", 10);

    private final EWMA m1Rate = EWMA.oneMinuteEWMA();
    private final EWMA m5Rate = EWMA.fiveMinuteEWMA();
    private final EWMA m15Rate = EWMA.fifteenMinuteEWMA();

    private final long startTime;
    private final AtomicLong lastTick;
    private final Clock clock;

    private final BucketCounterImpl bucketCounter;

    private final LongAdder uncounted = new LongAdder();

    /**
     * Creates a new {@link Meter}.
     */
    public MeterImpl() {
        this(Clock.defaultClock(), DEFAULT_NUM_OF_BUCKET, 60);
    }

    /**
     * Creates a new {@link Meter} with given bucket interval
     */
    public MeterImpl(int interval) {
        this(Clock.defaultClock(), DEFAULT_NUM_OF_BUCKET, interval);
    }

    public MeterImpl(Clock clock) {
        this(clock, DEFAULT_NUM_OF_BUCKET, 60);
    }

    public MeterImpl(Clock clock, int interval) {
        this(clock, DEFAULT_NUM_OF_BUCKET, interval);
    }

    /**
     * Creates a new {@link Meter}.
     *
     * @param clock          the clock to use for the meter ticks
     * @param numberOfBucket the number of bucket to store
     * @param interval       the time interval for each bucket
     */
    public MeterImpl(Clock clock, int numberOfBucket, int interval) {
        this.clock = clock;
        this.startTime = this.clock.getTick();
        this.lastTick = new AtomicLong(startTime);
        this.bucketCounter = new BucketCounterImpl(interval, numberOfBucket, clock);
    }

    /**
     * Mark the occurrence of an event.
     */
    public void mark() {
        mark(1);
    }

    /**
     * Mark the occurrence of a given number of events.
     *
     * @param n the number of events
     */
    public void mark(long n) {
        tickIfNecessary();
        uncounted.add(n);
        bucketCounter.update(n);
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

    public long getCount() {
        return bucketCounter.getCount();
    }

    @Override
    public Map<Long, Long> getInstantCount() {
        return bucketCounter.getBucketCounts();
    }

    @Override
    public Map<Long, Long> getInstantCount(long startTime) {
        return bucketCounter.getBucketCounts(startTime);
    }

    @Override
    public int getInstantCountInterval() {
        return bucketCounter.getBucketInterval();
    }

    public double getFifteenMinuteRate() {
        tickIfNecessary();
        return m15Rate.getRate(TimeUnit.SECONDS);
    }

    public double getFiveMinuteRate() {
        tickIfNecessary();
        return m5Rate.getRate(TimeUnit.SECONDS);
    }

    public double getMeanRate() {
        if (getCount() == 0) {
            return 0.0;
        } else {
            final double elapsed = (clock.getTick() - startTime);
            return getCount() / elapsed * TimeUnit.SECONDS.toNanos(1);
        }
    }

    public double getOneMinuteRate() {
        tickIfNecessary();
        return m1Rate.getRate(TimeUnit.SECONDS);
    }

    @Override
    public long lastUpdateTime() {
        return bucketCounter.lastUpdateTime();
    }
}
