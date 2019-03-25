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
 * A meter metric which measures mean throughput and one-, five-, and fifteen-minute
 * exponentially-weighted moving average throughput.
 * 一种用于度量一段时间内吞吐率的计量器。例如，一分钟内，五分钟内，十五分钟内的qps指标，
 * 这段时间内的吞吐率通过指数加权的方式计算移动平均得出。
 */
public interface Meter extends Metered {

    /**
     * Mark the occurrence of an event.
     * 标记一次事件
     */
    void mark();

    /**
     * Mark the occurrence of a given number of events.
     * 标记n次事件
     *
     * @param n the number of events
     */
    void mark(long n);
}
