package com.alibaba.metrics;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BucketReservoir implements Reservoir {

    private BucketCounter countPerBucket;
    private BucketCounter valuePerBucket;
    private Clock clock;
    private int interval;

    /**
     * Create a {@link BucketReservoir} instance with given count and value counter
     * @param interval the bucket interval
     * @param clock the clock implementation
     * @param numberOfBucket the number of buckets
     * @param count the total count, which will be update outside
     */
    public BucketReservoir(int interval, int numberOfBucket, Clock clock, BucketCounter count) {
        this.clock = clock;
        this.interval = interval;
        this.valuePerBucket = new BucketCounterImpl(interval, numberOfBucket, clock);
        this.countPerBucket = count;
    }

    @Override
    public int size() {
        return (int)countPerBucket.getCount();
    }

    @Override
    public void update(long value) {
        valuePerBucket.update(value);
    }

    /**
     * 获取最新的bucket的快照
     * @return a {@link BucketSnapshot} instance
     */
    @Override
    public Snapshot getSnapshot() {
        long startTime = getNormalizedStartTime(clock.getTime());
        Map<Long, Long> valueResult = valuePerBucket.getBucketCounts(startTime);
        long value = 0;
        if (!valueResult.isEmpty()) {
            value = valueResult.entrySet().iterator().next().getValue();
        }
        Map<Long, Long> countResult = countPerBucket.getBucketCounts(startTime);
        long count = 0;
        if (!countResult.isEmpty()) {
            count = countResult.entrySet().iterator().next().getValue();
        }
        return new BucketSnapshot(count, value);
    }

    private long getNormalizedStartTime(long current) {
        return (TimeUnit.MILLISECONDS.toSeconds(current) - interval) / interval * interval * 1000;
    }
}
