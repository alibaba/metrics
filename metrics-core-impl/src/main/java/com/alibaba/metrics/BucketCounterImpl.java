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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Record the accurate number of events within each given time interval, one for each bucket.
 */
public class BucketCounterImpl implements BucketCounter {

    /**
     * 保存从创建开始累积的计数
     */
    private final Counter totalCount;

    /**
     * 保存最近N次精确计数对应的时间
     */
    private final AtomicReference<Bucket> latestBucket;

    /**
     * 保存最近N次的精确计数, 采用环形队列避免数据的挪动
     */
    private final BucketDeque buckets;
    private final Clock clock;

    /**
     * 是否更新总次数
     */
    private final boolean updateTotalCount;

    /**
     * 每一次精确计数的之间的时间间隔，只能是1秒，5秒，10秒, 30秒, 60秒这几个数字
     */
    private int interval;

    /**
     * The time stamp this object has been initialized.
     */
    private long initTimestamp;

    public BucketCounterImpl(int interval, boolean updateTotalCount) {
        this(interval, 10, Clock.defaultClock(), updateTotalCount);
    }

    public BucketCounterImpl(int interval) {
        this(interval, 10, Clock.defaultClock(), true);
    }

    public BucketCounterImpl(int interval, int numberOfBucket, Clock clock) {
        this(interval, numberOfBucket, clock, true);
    }

    public BucketCounterImpl(int interval, int numberOfBucket, Clock clock, boolean updateTotalCount) {
        this.totalCount = new CounterImpl();
        this.interval = interval;
        this.buckets = new BucketDeque(numberOfBucket + 1);
        this.clock = clock;
        this.latestBucket = new AtomicReference<Bucket>(buckets.peek());
        this.updateTotalCount = updateTotalCount;
        this.initTimestamp = clock.getTime();
    }

    /**
     * update the counter to the given bucket
     */
    public void update() {
        update(1L);
    }

    /**
     * update the counter to the given bucket
     */
    public void update(long n) {
        if (updateTotalCount) {
            totalCount.inc(n);
        }
        // align current timestamp
        long curTs = TimeUnit.MILLISECONDS.toSeconds(clock.getTime()) / interval * interval;
        Bucket oldBucket = latestBucket.get();
        if (curTs > latestBucket.get().timestamp) {
            // create a new bucket and evict the oldest one
            Bucket newBucket = new Bucket();
            newBucket.timestamp = curTs;
            if (latestBucket.compareAndSet(oldBucket, newBucket)) {
                // this is a single thread operation
                buckets.addLast(newBucket);
                oldBucket = newBucket;
            } else {
                oldBucket = latestBucket.get();
            }
        }
        // reduce the call to latestBucket.get() to avoid cache line invalidation
        // because internally latestBucket is a volatile object
        oldBucket.count.add(n);
    }

    /**
     * Return the bucket count, keyed by timestamp
     * @return the bucket count, keyed by timestamp
     */
    public Map<Long, Long> getBucketCounts(long startTime) {
        Map<Long, Long> counts = new LinkedHashMap<Long, Long>();
        long curTs = calculateCurrentTimestamp(clock.getTime());
        for (Bucket bucket: buckets.getBucketList()) {
            if (1000L * bucket.timestamp >= startTime && bucket.timestamp <= curTs) {
                counts.put(1000L * bucket.timestamp, bucket.count.sum());
            }
        }
        return counts;
    }

    @Override
    public Map<Long, Long> getBucketCounts() {
        return getBucketCounts(0L);
    }

    @Override
    public int getBucketInterval() {
        return interval;
    }


    @Override
    public void inc() {
        update();
    }

    @Override
    public void inc(long n) {
        update(n);
    }

    @Override
    public void dec() {
        update(-1L);
    }

    @Override
    public void dec(long n) {
        update(-n);
    }

    @Override
    public long getCount() {
        return totalCount.getCount();
    }

    @Override
    public long lastUpdateTime() {
        long ts = latestBucket.get().timestamp;
        if (ts < 0) {
            // never updated
            return initTimestamp;
        }
        return TimeUnit.SECONDS.toMillis(ts);
    }

    private long calculateCurrentTimestamp(long timestamp) {
        return TimeUnit.MILLISECONDS.toSeconds(timestamp) / interval * interval;
    }
}
