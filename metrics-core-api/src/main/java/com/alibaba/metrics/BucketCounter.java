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

/**
 * 提供分桶计数功能，每个桶统计一定时间间隔内的计数。
 * BucketCounter只保留最近N个时间间隔内的计数，再老的会被丢弃。
 * 同时保存从创建开始到现在的累计计数。
 */
public interface BucketCounter extends Counter {

    /**
     * update the counter to the given bucket
     */
    void update();

    /**
     * update the counter to the given bucket
     */
    void update(long n);

    /**
     * Return the bucket count, keyed by timestamp
     * @return the bucket count, keyed by timestamp
     */
    Map<Long, Long> getBucketCounts();

    /**
     * Return the bucket count, keyed by timestamp, since (including) the startTime.
     * 返回从startTime开始的分桶统计功能
     * @param startTime 查询起始时间, 单位是毫秒
     * @return the bucket count, keyed by timestamp
     */
    Map<Long, Long> getBucketCounts(long startTime);

    /**
     * Get the interval of the bucket
     * @return the interval of the bucket
     */
    int getBucketInterval();
}
