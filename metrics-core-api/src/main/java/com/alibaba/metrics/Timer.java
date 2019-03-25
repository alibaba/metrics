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

import java.io.Closeable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * A timer metric which aggregates timing durations and provides duration statistics, plus
 * throughput statistics via {@link Meter}.
 * Timer相当于Meter+Histogram的组合，同时统计一段代码，一个方法的qps，以及执行时间的分布情况
 */
public interface Timer extends Metered, Sampling {
    /**
     * A timing context.
     *
     * @see Timer#time()
     */
    interface Context extends Closeable {

        /**
         * Updates the timer with the difference between current and start time. Call to this method will
         * not reset the start time. Multiple calls result in multiple updates.
         * @return the elapsed time in nanoseconds
         */
        long stop();
    }


    /**
     * Adds a recorded duration.
     *
     * @param duration the length of the duration
     * @param unit     the scale unit of {@code duration}
     */
    void update(long duration, TimeUnit unit);

    /**
     * Times and records the duration of event.
     *
     * @param event a {@link Callable} whose {@link Callable#call()} method implements a process
     *              whose duration should be timed
     * @param <T>   the type of the value returned by {@code event}
     * @return the value returned by {@code event}
     * @throws Exception if {@code event} throws an {@link Exception}
     */
    <T> T time(Callable<T> event) throws Exception;

    /**
     * Returns a new {@link Context}.
     *
     * @return a new {@link Context}
     * @see Context
     */
    Context time();


}
