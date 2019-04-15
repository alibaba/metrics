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
package com.alibaba.metrics.jvm;

import com.alibaba.metrics.CachedGauge;

import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

import static com.alibaba.metrics.Constants.NOT_AVAILABLE;

/**
 * A cached gauge for jvm compilation statistics
 */
public class CompilationGauge extends CachedGauge<Long> {

    /**
     * The compilation mbean
     */
    private final CompilationMXBean mxBean;

    /**
     * Last total compilation time
     */
    private long lastValue = 0;

    public CompilationGauge(long timeout, TimeUnit timeoutUnit) {
        super(timeout, timeoutUnit);
        mxBean = ManagementFactory.getCompilationMXBean();
    }

    public CompilationGauge(long timeout, TimeUnit timeoutUnit, CompilationMXBean mxBean) {
        super(timeout, timeoutUnit);
        this.mxBean = mxBean;
    }

    @Override
    protected Long loadValue() {
        if (mxBean != null && mxBean.isCompilationTimeMonitoringSupported()) {
            long currentValue = mxBean.getTotalCompilationTime();
            if (lastValue > 0) {
                long delta = currentValue - lastValue;
                if (delta > 0) {
                    return delta;
                }
            }
            lastValue = currentValue;
        }
        return NOT_AVAILABLE;
    }
}
