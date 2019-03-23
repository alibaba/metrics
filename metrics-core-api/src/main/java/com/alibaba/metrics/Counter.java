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

/**
 * <pre>
 * An incrementing and decrementing counter metric.
 *
 * 计数器型指标，适用于记录调用总量等类型的数据
 * </pre>
 */
public interface Counter extends Metric, Counting {

    /**
     * Increment the counter by one.
     * 计数器加1
     */
    void inc();

    /**
     * Increment the counter by {@code n}.
     * 计数器加n
     *
     * @param n the amount by which the counter will be increased
     */
    void inc(long n);

    /**
     * Decrement the counter by one.
     * 计数器减1
     */
    void dec();

    /**
     * Decrement the counter by {@code n}.
     * 计数器减n
     *
     * @param n the amount by which the counter will be decreased
     */
    void dec(long n);

}
