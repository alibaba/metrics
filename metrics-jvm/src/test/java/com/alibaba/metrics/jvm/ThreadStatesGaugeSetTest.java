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

import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.ManualClock;
import com.alibaba.metrics.MetricName;
import org.junit.Before;
import org.junit.Test;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThreadStatesGaugeSetTest {
    private final ThreadMXBean threads = mock(ThreadMXBean.class);
    private final ManualClock clock = new ManualClock();
    private final ThreadStatesGaugeSet gauges = new ThreadStatesGaugeSet(threads, 1, TimeUnit.MINUTES, clock);
    private final long[] ids = new long[]{ 1, 2, 3 };

    private final ThreadInfo newThread = mock(ThreadInfo.class);
    private final ThreadInfo runnableThread = mock(ThreadInfo.class);
    private final ThreadInfo blockedThread = mock(ThreadInfo.class);
    private final ThreadInfo waitingThread = mock(ThreadInfo.class);
    private final ThreadInfo timedWaitingThread = mock(ThreadInfo.class);
    private final ThreadInfo terminatedThread = mock(ThreadInfo.class);

    private final Set<String> deadlocks = new HashSet<String>();

    private static final MetricName TERMINATED_COUNT = MetricName.build("terminated.count");
    private static final MetricName NEW_COUNT = MetricName.build("new.count");
    private static final MetricName COUNT = MetricName.build("count");
    private static final MetricName TIMED_WAITING_COUNT = MetricName.build("timed_waiting.count");
    private static final MetricName BLOCKED_COUNT = MetricName.build("blocked.count");
    private static final MetricName WAITING_COUNT = MetricName.build("waiting.count");
    private static final MetricName DAEMON_COUNT = MetricName.build("daemon.count");
    private static final MetricName RUNNABLE_COUNT = MetricName.build("runnable.count");
    private static final MetricName DEADLOCK_COUNT = MetricName.build("deadlock.count");

    @Before
    public void setUp() throws Exception {
        deadlocks.add("yay");

        when(newThread.getThreadState()).thenReturn(Thread.State.NEW);
        when(runnableThread.getThreadState()).thenReturn(Thread.State.RUNNABLE);
        when(blockedThread.getThreadState()).thenReturn(Thread.State.BLOCKED);
        when(waitingThread.getThreadState()).thenReturn(Thread.State.WAITING);
        when(timedWaitingThread.getThreadState()).thenReturn(Thread.State.TIMED_WAITING);
        when(terminatedThread.getThreadState()).thenReturn(Thread.State.TERMINATED);

        when(threads.getAllThreadIds()).thenReturn(ids);
        when(threads.getThreadInfo(ids, 0)).thenReturn(new ThreadInfo[]{
                newThread, runnableThread, blockedThread,
                waitingThread, timedWaitingThread, terminatedThread
        });

        when(threads.getThreadCount()).thenReturn(12);
        when(threads.getDaemonThreadCount()).thenReturn(13);
        when(threads.findDeadlockedThreads()).thenReturn(new long[]{1L});
    }

    @Test
    public void hasASetOfGauges() throws Exception {
        assertThat(gauges.getMetrics().keySet())
                .containsOnly(TERMINATED_COUNT,
                              NEW_COUNT,
                              COUNT,
                              TIMED_WAITING_COUNT,
                              BLOCKED_COUNT,
                              WAITING_COUNT,
                              DAEMON_COUNT,
                              RUNNABLE_COUNT,
                              DEADLOCK_COUNT);
    }

    @Test
    public void hasAGaugeForEachThreadState() throws Exception {
        clock.addSeconds(61);
        assertThat(((Gauge) gauges.getMetrics().get(NEW_COUNT)).getValue())
                .isEqualTo(1);

        assertThat(((Gauge) gauges.getMetrics().get(RUNNABLE_COUNT)).getValue())
                .isEqualTo(1);

        assertThat(((Gauge) gauges.getMetrics().get(BLOCKED_COUNT)).getValue())
                .isEqualTo(1);

        assertThat(((Gauge) gauges.getMetrics().get(WAITING_COUNT)).getValue())
                .isEqualTo(1);

        assertThat(((Gauge) gauges.getMetrics().get(TIMED_WAITING_COUNT)).getValue())
                .isEqualTo(1);

        assertThat(((Gauge) gauges.getMetrics().get(TERMINATED_COUNT)).getValue())
                .isEqualTo(1);
    }

    @Test
    public void hasAGaugeForTheNumberOfThreads() throws Exception {
        clock.addSeconds(61);
        assertThat(((Gauge) gauges.getMetrics().get(COUNT)).getValue())
                .isEqualTo(12);
    }

    @Test
    public void hasAGaugeForTheNumberOfDaemonThreads() throws Exception {
        clock.addSeconds(61);
        assertThat(((Gauge) gauges.getMetrics().get(DAEMON_COUNT)).getValue())
                .isEqualTo(13);
    }

    @Test
    public void hasAGaugeForAnyDeadlockCount() throws Exception {
        clock.addSeconds(61);
        assertThat(((Gauge) gauges.getMetrics().get(DEADLOCK_COUNT)).getValue())
                .isEqualTo(1);
    }

    @Test
    public void autoDiscoversTheMXBeans() throws Exception {
        final ThreadStatesGaugeSet set = new ThreadStatesGaugeSet();
        assertThat(((Gauge) set.getMetrics().get(COUNT)).getValue()).isNotNull();
    }
}
