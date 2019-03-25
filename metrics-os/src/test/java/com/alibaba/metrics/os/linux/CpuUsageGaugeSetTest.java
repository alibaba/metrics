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
package com.alibaba.metrics.os.linux;

import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.ManualClock;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.os.utils.FormatUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.alibaba.metrics.Constants.NOT_AVAILABLE;

public class CpuUsageGaugeSetTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testCpuUsageMetrics() {

        ManualClock clock = new ManualClock();

        CpuUsageGaugeSet cpuUsageGaugeSet = new CpuUsageGaugeSet(
                5, TimeUnit.SECONDS, "src/test/resources/proc_stat", clock);

        Map<MetricName, Metric> metrics = cpuUsageGaugeSet.getMetrics();

        Assert.assertEquals(13, metrics.keySet().size());

        clock.addSeconds(6);

        Gauge<Float> user = (Gauge)metrics.get(MetricName.build("cpu.user"));
        Gauge<Float> nice = (Gauge)metrics.get(MetricName.build("cpu.nice"));
        Gauge<Float> system = (Gauge)metrics.get(MetricName.build("cpu.system"));
        Gauge<Float> idle = (Gauge)metrics.get(MetricName.build("cpu.idle"));
        Gauge<Float> iowait = (Gauge)metrics.get(MetricName.build("cpu.iowait"));
        Gauge<Float> irq = (Gauge)metrics.get(MetricName.build("cpu.irq"));
        Gauge<Float> softirq = (Gauge)metrics.get(MetricName.build("cpu.softirq"));
        Gauge<Float> steal = (Gauge)metrics.get(MetricName.build("cpu.steal"));
        Gauge<Float> guest = (Gauge)metrics.get(MetricName.build("cpu.guest"));
        Gauge<Double> intr = (Gauge)metrics.get(MetricName.build("interrupts"));
        Gauge<Double> ctxt = (Gauge)metrics.get(MetricName.build("context_switches"));
        Gauge<Long> procRunning = (Gauge)metrics.get(MetricName.build("process.running"));
        Gauge<Long> procBlocked = (Gauge)metrics.get(MetricName.build("process.blocked"));

        long[] init = new long[9];
        long[] first = new long[]{161458220L, 18100L, 123669465L, 24676619894L, 11864776L, 2275215L, 3576999L, 12366444L, 22366444L,};
        long[] delta = new long[9];
        long total = 0L;

        for (int i=0; i < delta.length; i++) {
            delta[i] = first[i] - init[i];
            total += delta[i];
        }

        /**
         * At the very first time before collection, the init cpuInfo is all set to 0
         */
        Assert.assertEquals(getUsage(delta[0], total), user.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[1], total), nice.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[2], total), system.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[3], total), idle.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[4], total), iowait.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[5], total), irq.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[6], total), softirq.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[7], total), steal.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[8], total), guest.getValue(), 0.0001f);

        Assert.assertEquals(0, intr.getValue().longValue());
        Assert.assertEquals(0, ctxt.getValue().longValue());
        Assert.assertEquals(1, procRunning.getValue().longValue());
        Assert.assertEquals(0, procBlocked.getValue().longValue());


        long[] second = new long[]{161464658L, 18100L, 123674792L, 24676879507L, 11865415L, 2275298L, 3577096L, 15366444L, 23366444L};
        total = 0L;

        for (int i=0; i < delta.length; i++) {
            delta[i] = second[i] - first[i];
            total += delta[i];
        }

        clock.addSeconds(6);

        cpuUsageGaugeSet.setFilePath("src/test/resources/proc_stat_2");

        Assert.assertEquals(getUsage(delta[0], total), user.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[1], total), nice.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[2], total), system.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[3], total), idle.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[4], total), iowait.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[5], total), irq.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[6], total), softirq.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[7], total), steal.getValue(), 0.0001f);
        Assert.assertEquals(getUsage(delta[8], total), guest.getValue(), 0.0001f);

        Assert.assertEquals((19388023564L - 19385871162L) / 6.0d, intr.getValue().doubleValue(), 0.0001f);
        Assert.assertEquals((96097443242L - 96093945295L) / 6.0d, ctxt.getValue().doubleValue(), 0.0001f);
        Assert.assertEquals(10, procRunning.getValue().intValue());
        Assert.assertEquals(2, procBlocked.getValue().intValue());
    }

    @Test
    public void testSubstring() {
        String data = "intr 19385871162 204 7 0 0 0 0 2 0 0 0 0 0 102 0 0 0 314 125889916 3547347777 0 0 0 0 22 0";
        Assert.assertEquals("19385871162",
                data.substring("intr ".length(), data.indexOf(' ', "intr ".length())));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInvalidData() {
        ManualClock clock = new ManualClock();

        CpuUsageGaugeSet cpuUsageGaugeSet = new CpuUsageGaugeSet(
                5, TimeUnit.SECONDS, "src/test/resources/proc_stat", clock);

        Map<MetricName, Metric> metrics = cpuUsageGaugeSet.getMetrics();
        Gauge<Double> intr = (Gauge)metrics.get(MetricName.build("interrupts"));
        Gauge<Double> ctxt = (Gauge)metrics.get(MetricName.build("context_switches"));

        clock.addSeconds(6);

        Assert.assertEquals(0.0d, intr.getValue().doubleValue(), 0.0001f);
        Assert.assertEquals(0.0d, ctxt.getValue().doubleValue(), 0.0001f);

        clock.addSeconds(6);

        cpuUsageGaugeSet.setFilePath("src/test/resources/proc_stat_invalid_data");


        Assert.assertEquals(NOT_AVAILABLE, intr.getValue().doubleValue(), 0.0001f);
        Assert.assertEquals(NOT_AVAILABLE, ctxt.getValue().doubleValue(), 0.0001f);
    }

    private float getUsage(long delta, long total) {
        return FormatUtils.formatFloat(100.0f * delta / total);
    }
}
