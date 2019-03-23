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
package com.alibaba.metrics.tomcat;


import com.alibaba.metrics.CachedMetricSet;
import com.alibaba.metrics.Clock;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.PersistentGauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ThreadGaugeSet extends CachedMetricSet {

    private static final Logger logger = LoggerFactory.getLogger(ThreadGaugeSet.class);

    private enum ThreadMetrics {
        BUSY_COUNT, TOTAL_COUNT, MIN_POOL_SIZE, MAX_POOL_SIZE, THREAD_POOL_QUEUE_SIZE
    }

    private static final String[] metricNames = {
            "busy_count",
            "total_count",
            "min_pool_size",
            "max_pool_size",
            "thread_pool_queue_size"
    };

    private int[] threadMetrics;

    private MBeanServer mbeanServer;

    private ObjectName threadPool;

    public ThreadGaugeSet() {
        this(DEFAULT_DATA_TTL, TimeUnit.MILLISECONDS, Clock.defaultClock());
    }

    public ThreadGaugeSet(long dataTTL, TimeUnit unit) {
        this(dataTTL, unit, Clock.defaultClock());
    }

    public ThreadGaugeSet(long dataTTL, TimeUnit unit, Clock clock) {
        super(dataTTL, unit, clock);
        threadMetrics = new int[ThreadMetrics.values().length];
        threadPool = JMXUtils.createObjectName("*:type=ThreadPool,*");
        mbeanServer = JMXUtils.getMBeanServer();
    }

    @Override
    protected void getValueInternal() {
        // collect thread metrics
        ObjectName[] connectorNames = JMXUtils.getObjectNames(threadPool);

        for (ObjectName connectorName : connectorNames) {
            // the name can be used as tag later, but right now it is not used.
            String name = ObjectName.unquote(connectorName.getKeyProperty("name"));
            try {
                threadMetrics[ThreadMetrics.BUSY_COUNT.ordinal()] =
                        (Integer)mbeanServer.getAttribute(connectorName, "currentThreadsBusy");
                threadMetrics[ThreadMetrics.TOTAL_COUNT.ordinal()] =
                        (Integer)mbeanServer.getAttribute(connectorName, "currentThreadCount");
                threadMetrics[ThreadMetrics.MIN_POOL_SIZE.ordinal()] =
                        (Integer)mbeanServer.getAttribute(connectorName, "minSpareThreads");
                threadMetrics[ThreadMetrics.MAX_POOL_SIZE.ordinal()] =
                        (Integer)mbeanServer.getAttribute(connectorName, "maxThreads");
                // should be only one stat object there
                break;
            } catch (Exception e) {
                logger.error("Exception occur when getting connector global stats: ", e);
            }
            try {
                threadMetrics[ThreadMetrics.THREAD_POOL_QUEUE_SIZE.ordinal()] =
                        (Integer)mbeanServer.getAttribute(connectorName, "threadPoolTaskQueueSize");
            } catch (Exception e) {
                logger.error("Exception occur when getting connector global stats: ", e);
            }
        }
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        final Map<MetricName, Metric> gauges = new HashMap<MetricName, Metric>();

        for (ThreadMetrics metric: ThreadMetrics.values()) {
            gauges.put(MetricName.build(metricNames[metric.ordinal()]), new ThreadStatGauge(metric.ordinal()));
        }

        return gauges;
    }

    private class ThreadStatGauge extends PersistentGauge<Integer> {

        private int index;

        public ThreadStatGauge(int index) {
            this.index = index;
        }

        @Override
        public Integer getValue() {
            refreshIfNecessary();
            return threadMetrics[index];
        }
    }
}
