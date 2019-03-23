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

import java.io.OutputStream;

import static com.alibaba.metrics.Constants.NOT_AVAILABLE;

public class BucketSnapshot implements Snapshot {

    private static final long[] EMPTY = new long[0];

    private long count = 0;
    private long value = 0;

    public BucketSnapshot(long count, long value) {
        this.count = count;
        this.value = value;
    }

    @Override
    public double getValue(double quantile) {
        return NOT_AVAILABLE;
    }

    @Override
    public long[] getValues() {
        return EMPTY;
    }

    @Override
    public int size() {
        return (int)count;
    }

    @Override
    public double getMedian() {
        return NOT_AVAILABLE;
    }

    @Override
    public double get75thPercentile() {
        return NOT_AVAILABLE;
    }

    @Override
    public double get95thPercentile() {
        return NOT_AVAILABLE;
    }

    @Override
    public double get98thPercentile() {
        return NOT_AVAILABLE;
    }

    @Override
    public double get99thPercentile() {
        return NOT_AVAILABLE;
    }

    @Override
    public double get999thPercentile() {
        return NOT_AVAILABLE;
    }

    @Override
    public long getMax() {
        return NOT_AVAILABLE;
    }

    @Override
    public double getMean() {
        if (count == 0) return 0;
        return 1.0d * value / count;
    }

    @Override
    public long getMin() {
        return NOT_AVAILABLE;
    }

    @Override
    public double getStdDev() {
        return NOT_AVAILABLE;
    }

    @Override
    public void dump(OutputStream output) {
        // do nothing
    }
}
