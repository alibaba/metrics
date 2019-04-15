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
import com.alibaba.metrics.RatioGauge;
import com.sun.management.UnixOperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * A gauge for the ratio of used to total file descriptors.
 */
public class FileDescriptorRatioGauge extends RatioGauge {

    private final OperatingSystemMXBean os;

    private final CachedGauge<Ratio> cachedRatio;

    /**
     * Creates a new gauge using the platform OS bean.
     */
    public FileDescriptorRatioGauge() {
        this(ManagementFactory.getOperatingSystemMXBean(), 1, TimeUnit.MINUTES);
    }

    public FileDescriptorRatioGauge(OperatingSystemMXBean os) {
        this(os, 1, TimeUnit.MINUTES);
    }

    /**
     * Creates a new gauge using the given OS bean.
     *
     * @param os    an {@link OperatingSystemMXBean}
     */
    public FileDescriptorRatioGauge(OperatingSystemMXBean os, long timeout, TimeUnit unit) {
        this.os = os;
        cachedRatio = new CachedGauge<Ratio>(timeout, unit) {
            @Override
            protected Ratio loadValue() {
                return getRatioInternal();
            }
        };
    }

    @Override
    protected Ratio getRatio() {
        return cachedRatio.getValue();
    }

    private Ratio getRatioInternal() {
        if (os instanceof UnixOperatingSystemMXBean) {
            final UnixOperatingSystemMXBean unixOs = (UnixOperatingSystemMXBean) os;
            return Ratio.of(unixOs.getOpenFileDescriptorCount(), unixOs.getMaxFileDescriptorCount());
        } else {
            return Ratio.of(Double.NaN, Double.NaN);
        }
//        try {
//            return Ratio.of(invoke("getOpenFileDescriptorCount"),
//                    invoke("getMaxFileDescriptorCount"));
//        } catch (NoSuchMethodException e) {
//            return Ratio.of(Double.NaN, Double.NaN);
//        } catch (IllegalAccessException e) {
//            return Ratio.of(Double.NaN, Double.NaN);
//        } catch (InvocationTargetException e) {
//            return Ratio.of(Double.NaN, Double.NaN);
//        }
    }

    private long invoke(String name) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Method method = os.getClass().getDeclaredMethod(name);
        method.setAccessible(true);
        return (Long) method.invoke(os);
    }
}
