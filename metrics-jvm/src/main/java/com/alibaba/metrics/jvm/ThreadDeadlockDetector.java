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
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A utility class for detecting deadlocked threads.
 */
public class ThreadDeadlockDetector {
    private static final int MAX_STACK_TRACE_DEPTH = 100;

    private final ThreadMXBean threads;

    /**
     * Creates a new detector.
     */
    public ThreadDeadlockDetector() {
        this(ManagementFactory.getThreadMXBean());
    }

    /**
     * Creates a new detector using the given {@link ThreadMXBean}.
     *
     * @param threads    a {@link ThreadMXBean}
     */
    public ThreadDeadlockDetector(ThreadMXBean threads) {
        this.threads = threads;
    }

    /**
     * Returns a set of diagnostic stack traces for any deadlocked threads. If no threads are
     * deadlocked, returns an empty set.
     *
     * @return stack traces for deadlocked threads or an empty set
     */
    public Set<String> getDeadlockedThreads() {
        final long[] ids = threads.findDeadlockedThreads();
        if (ids != null) {
            final Set<String> deadlocks = new HashSet<String>();
            for (ThreadInfo info : threads.getThreadInfo(ids, MAX_STACK_TRACE_DEPTH)) {
                final StringBuilder stackTrace = new StringBuilder();
                for (StackTraceElement element : info.getStackTrace()) {
                    stackTrace.append("\t at ")
                              .append(element.toString())
                              .append(String.format("%n"));
                }

                deadlocks.add(
                        String.format("%s locked on %s (owned by %s):%n%s",
                                      info.getThreadName(),
                                      info.getLockName(),
                                      info.getLockOwnerName(),
                                      stackTrace.toString()
                        )
                );
            }
            return Collections.unmodifiableSet(deadlocks);
        }
        return Collections.emptySet();
    }

    public int getDeadlockedThreadCount() {
        return threads.findDeadlockedThreads() == null ? 0 : threads.findDeadlockedThreads().length;
    }
}
