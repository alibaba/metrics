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
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过1个LongAdder来同时完成count和rt的累加操作
 * Java里面1个Long有64个bit, 除去最高位表示符号的1个bit，还有63个bit可以使用
 * 在一个不超过60s统计周期内，方法调用的总次数和总次数其实完全用不到63个bit
 * 所以可以将这两个统计项放到一个long里面来表示
 * 这里高位的25个bit表示统计周期内调用总次数，后38位表示总rt
 *
 * Since the total number of method invocation and total time of method execution
 * within a collecting interval never exceed the range a LONG can represent,
 * we can use one LongAdder to record both the total count and total number
 */
public class FastCompassImpl implements FastCompass {

    /**
     * 控制总次数的bit数, 理论统计上限为 2 ^ (64 -38 -1) = 33554432
     * This magic number divide a long into two parts,
     * where the higher part is used to record the total number of method invocations,
     * and the lower part is used to record the total method execution time.
     * The max number of count per collecting interval will be 2 ^ (64 -38 -1) = 33554432
     */
    private static final long COUNT_OFFSET = 38;

    /**
     * 次数统计的累加基数，和rt相加得到实际更新到LongAdder的数
     * The base number of count that is added to total rt,
     * to derive a number which will be added to {@link LongAdder}
     */
    private static final long COUNT_BASE = 1L << 38;

    /**
     * 总数和此数进行二进制与得到总rt统计
     * The base number is used to do BITWISE AND operation with the value of {@link LongAdder}
     * to derive the total number of execution time
     */
    private static final long RT_BITWISE_AND_BASE = (1L << 38) - 1;

    private static final int MAX_SUBCATEGORY_SIZE =
            Integer.getInteger("com.alibaba.metrics.maxSubCategoryCount", 20);

    private static final int DEFAULT_BUCKET_COUNT = 10;

    private int bucketInterval;

    private int numberOfBuckets;

    private Clock clock;

    private int maxCategoryCount;

    /**
     * The number of addon count per addon per collect interval
     */
    private final ConcurrentHashMap<String, BucketCounter> subCategories;

    public FastCompassImpl(int bucketInterval) {
        this(bucketInterval, DEFAULT_BUCKET_COUNT, Clock.defaultClock(), MAX_SUBCATEGORY_SIZE);
    }

    public FastCompassImpl(int bucketInterval, int numberOfBuckets, Clock clock, int maxCategoryCount) {
        this.bucketInterval = bucketInterval;
        this.numberOfBuckets = numberOfBuckets;
        this.clock = clock;
        this.maxCategoryCount = maxCategoryCount;
        this.subCategories = new ConcurrentHashMap<String, BucketCounter>();
    }

    @Override
    public void record(long duration, String subCategory) {
        if (duration < 0 || subCategory == null) {
            return;
        }
        if (!subCategories.containsKey(subCategory)) {
            if (subCategories.size() >= maxCategoryCount) {
                // ignore if maxCategoryCount is exceeded, no exception will be thrown
                return;
            }
            subCategories.putIfAbsent(subCategory, new BucketCounterImpl(bucketInterval, numberOfBuckets, clock, false));
        }
        long data = COUNT_BASE + duration;
        subCategories.get(subCategory).update(data);
    }

    @Override
    public Map<String, Map<Long, Long>> getMethodCountPerCategory() {
        return getMethodCountPerCategory(0L);
    }

    @Override
    public Map<String, Map<Long, Long>> getMethodRtPerCategory() {
        return getMethodRtPerCategory(0L);
    }

    @Override
    public Map<String, Map<Long, Long>> getMethodCountPerCategory(long startTime) {
        Map<String, Map<Long, Long>> countPerCategory = new HashMap<String, Map<Long, Long>>();
        for (Map.Entry<String, BucketCounter> entry: subCategories.entrySet()) {
            Map<Long, Long> bucketCount = new HashMap<Long, Long>();
            for (Map.Entry<Long, Long> bucket: entry.getValue().getBucketCounts(startTime).entrySet()) {
                bucketCount.put(bucket.getKey(), bucket.getValue() >> COUNT_OFFSET);
            }
            countPerCategory.put(entry.getKey(), bucketCount);
        }
        return countPerCategory;
    }

    @Override
    public Map<String, Map<Long, Long>> getMethodRtPerCategory(long startTime) {
        Map<String, Map<Long, Long>> rtPerCategory = new HashMap<String, Map<Long, Long>>();
        for (Map.Entry<String, BucketCounter> entry: subCategories.entrySet()) {
            Map<Long, Long> bucketCount = new HashMap<Long, Long>();
            for (Map.Entry<Long, Long> bucket: entry.getValue().getBucketCounts(startTime).entrySet()) {
                bucketCount.put(bucket.getKey(), bucket.getValue() & RT_BITWISE_AND_BASE);
            }
            rtPerCategory.put(entry.getKey(), bucketCount);
        }
        return rtPerCategory;
    }

    @Override
    public int getBucketInterval() {
        return bucketInterval;
    }

    @Override
    public Map<String, Map<Long, Long>> getCountAndRtPerCategory() {
        return getCountAndRtPerCategory(0L);
    }

    @Override
    public Map<String, Map<Long, Long>> getCountAndRtPerCategory(long startTime) {

        Map<String, Map<Long, Long>> countAndRtPerCategory = new HashMap<String, Map<Long, Long>>();

        for (Map.Entry<String, BucketCounter> entry: subCategories.entrySet()) {
            Map<Long, Long> bucketCount = new HashMap<Long, Long>();
            for (Map.Entry<Long, Long> bucket: entry.getValue().getBucketCounts(startTime).entrySet()) {
                bucketCount.put(bucket.getKey(), bucket.getValue());
            }
            countAndRtPerCategory.put(entry.getKey(), bucketCount);
        }
        return countAndRtPerCategory;

    }

    @Override
    public long lastUpdateTime() {
        long latest = 0;
        for (Map.Entry<String, BucketCounter> entry: subCategories.entrySet()) {
            if (latest < entry.getValue().lastUpdateTime()) {
                latest = entry.getValue().lastUpdateTime();
            }
        }
        return latest;
    }
}
