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

import org.junit.Test;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThreadDeadlockDetectorTest {
    private final ThreadMXBean threads = mock(ThreadMXBean.class);
    private final ThreadDeadlockDetector detector = new ThreadDeadlockDetector(threads);

    @Test
    public void returnsAnEmptySetIfNoThreadsAreDeadlocked() throws Exception {
        when(threads.findDeadlockedThreads()).thenReturn(null);

        assertThat(detector.getDeadlockedThreads())
                .isEmpty();
    }

    @Test
    public void returnsASetOfThreadsIfAnyAreDeadlocked() throws Exception {
        final ThreadInfo thread1 = mock(ThreadInfo.class);
        when(thread1.getThreadName()).thenReturn("thread1");
        when(thread1.getLockName()).thenReturn("lock2");
        when(thread1.getLockOwnerName()).thenReturn("thread2");
        when(thread1.getStackTrace()).thenReturn(new StackTraceElement[]{
                new StackTraceElement("Blah", "bloo", "Blah.java", 150),
                new StackTraceElement("Blah", "blee", "Blah.java", 100)
        });

        final ThreadInfo thread2 = mock(ThreadInfo.class);
        when(thread2.getThreadName()).thenReturn("thread2");
        when(thread2.getLockName()).thenReturn("lock1");
        when(thread2.getLockOwnerName()).thenReturn("thread1");
        when(thread2.getStackTrace()).thenReturn(new StackTraceElement[]{
                new StackTraceElement("Blah", "blee", "Blah.java", 100),
                new StackTraceElement("Blah", "bloo", "Blah.java", 150)
        });

        final long[] ids = { 1, 2 };
        when(threads.findDeadlockedThreads()).thenReturn(ids);
        when(threads.getThreadInfo(eq(ids), anyInt()))
                .thenReturn(new ThreadInfo[]{ thread1, thread2 });

        assertThat(detector.getDeadlockedThreads())
                .containsOnly(String.format(Locale.US,
                                            "thread1 locked on lock2 (owned by thread2):%n" +
                                                    "\t at Blah.bloo(Blah.java:150)%n" +
                                                    "\t at Blah.blee(Blah.java:100)%n"),
                              String.format(Locale.US,
                                            "thread2 locked on lock1 (owned by thread1):%n" +
                                                    "\t at Blah.blee(Blah.java:100)%n" +
                                                    "\t at Blah.bloo(Blah.java:150)%n"));
    }

    @Test
    public void autoDiscoversTheThreadMXBean() throws Exception {
        assertThat(new ThreadDeadlockDetector().getDeadlockedThreads())
                .isNotNull();
    }
}
