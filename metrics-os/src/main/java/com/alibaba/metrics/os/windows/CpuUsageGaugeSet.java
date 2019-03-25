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
package com.alibaba.metrics.os.windows;

import com.alibaba.metrics.CachedMetricSet;
import com.alibaba.metrics.Clock;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.PersistentGauge;
import com.alibaba.metrics.os.utils.FormatUtils;
import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.alibaba.metrics.os.utils.SystemInfoUtils.sigar;

public class CpuUsageGaugeSet extends CachedMetricSet {

    private static final Logger logger = LoggerFactory.getLogger(CpuUsageGaugeSet.class);

    private float[] cpuUsage;

    private CpuInfo lastCollectedCpuInfo;

    private enum CpuUsage {
        USER, NICE, SYSTEM, IDLE, IOWAIT, IRQ, SOFTIRQ, STEAL, GUEST
    }

    /**
     * The "procs_running" line gives the number of processes currently running on CPUs.
     */
    private long processRunning;

    /**
     * The "procs_blocked" line gives the number of processes currently blocked, waiting for I/O to complete.
     */
    private long processBlocked;

    /**
     * The "intr" line gives counts of interrupts serviced since boot time,
     * for each of the possible system interrupts.
     * The first column is the total of all interrupts serviced;
     * each subsequent column is the total for that particular interrupt.
     */
    private long totalInterrupts;

    /**
     * The number of interrupts per second between collection
     * This is a equivalent implementation of 'in' in vmstat
     * https://www.thomas-krenn.com/en/wiki/Linux_Performance_Measurements_using_vmstat
     */
    private double interruptsRate;

    /**
     * The "ctxt" line gives the total number of context switches across all CPUs.
     */
    private long totalContextSwitches;

    /**
     * The number of context switches per second between collection
     * This is a equivalent implementation of 'cs' in vmstat
     * https://www.thomas-krenn.com/en/wiki/Linux_Performance_Measurements_using_vmstat
     */
    private double contextSwitchesRate;

    private Map<MetricName, Metric> gauges = new HashMap<MetricName, Metric>();

    public CpuUsageGaugeSet(long dataTTL, TimeUnit unit, Clock clock){
        super(dataTTL, unit, clock);
        cpuUsage = new float[CpuUsage.values().length];
        lastCollectedCpuInfo = new CpuInfo();
        populateMetrics();
    }

    public CpuUsageGaugeSet(long dataTTL, TimeUnit unit) {
        this(dataTTL, unit, Clock.defaultClock());
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        return gauges;
    }

    private float getUsage(long current, long last, CpuInfo curInfo, CpuInfo lastInfo) {
        try {
            float f = 100.0f * (current - last) / (curInfo.totalTime - lastInfo.totalTime);
            return FormatUtils.formatFloat(f);
        } catch (Exception e) {
            return 0.0f;
        }
    }

    private CpuInfo collectCpuInfo() {

        //CpuPerc cpuList[] = sigar.getCpuPercList();

        Cpu cpu = null;
        try {
            cpu = sigar.getCpu();
        } catch (SigarException e) {

        }

        if (cpu == null){
            return new CpuInfo();
        }

        CpuInfo info = new CpuInfo();
        info.userTime = cpu.getUser();
        info.niceTime = cpu.getNice();
        info.systemTime = cpu.getSys();
        info.idleTime = cpu.getIdle();
        info.iowaitTime = cpu.getWait();
        info.irqTime = cpu.getIrq();
        info.softirqTime = cpu.getSoftIrq();
        info.stealTime = cpu.getStolen();

        info.totalTime = cpu.getTotal();

        return info;
    }

    @Override
    protected void getValueInternal() {

        CpuInfo current = collectCpuInfo();

        cpuUsage[CpuUsage.USER.ordinal()] =
                getUsage(current.userTime, lastCollectedCpuInfo.userTime, current, lastCollectedCpuInfo);
        cpuUsage[CpuUsage.NICE.ordinal()] =
                getUsage(current.niceTime, lastCollectedCpuInfo.niceTime, current, lastCollectedCpuInfo);
        cpuUsage[CpuUsage.SYSTEM.ordinal()] =
                getUsage(current.systemTime, lastCollectedCpuInfo.systemTime, current, lastCollectedCpuInfo);
        cpuUsage[CpuUsage.IDLE.ordinal()] =
                getUsage(current.idleTime, lastCollectedCpuInfo.idleTime, current, lastCollectedCpuInfo);
        cpuUsage[CpuUsage.IOWAIT.ordinal()] =
                getUsage(current.iowaitTime, lastCollectedCpuInfo.iowaitTime, current, lastCollectedCpuInfo);
        cpuUsage[CpuUsage.IRQ.ordinal()] =
                getUsage(current.irqTime, lastCollectedCpuInfo.irqTime, current, lastCollectedCpuInfo);
        cpuUsage[CpuUsage.SOFTIRQ.ordinal()] =
                getUsage(current.softirqTime, lastCollectedCpuInfo.softirqTime, current, lastCollectedCpuInfo);
        cpuUsage[CpuUsage.STEAL.ordinal()] =
                getUsage(current.stealTime, lastCollectedCpuInfo.stealTime, current, lastCollectedCpuInfo);
        cpuUsage[CpuUsage.GUEST.ordinal()] =
                getUsage(current.guestTIme, lastCollectedCpuInfo.guestTIme, current, lastCollectedCpuInfo);

        lastCollectedCpuInfo = current;

    }


    private void populateMetrics() {

        gauges.put(MetricName.build("cpu.user"), new CpuGauge(CpuUsage.USER.ordinal()));
        gauges.put(MetricName.build("cpu.nice"), new CpuGauge(CpuUsage.NICE.ordinal()));
        gauges.put(MetricName.build("cpu.system"), new CpuGauge(CpuUsage.SYSTEM.ordinal()));
        gauges.put(MetricName.build("cpu.idle"), new CpuGauge(CpuUsage.IDLE.ordinal()));
        gauges.put(MetricName.build("cpu.iowait"), new CpuGauge(CpuUsage.IOWAIT.ordinal()));
        gauges.put(MetricName.build("cpu.irq"), new CpuGauge(CpuUsage.IRQ.ordinal()));
        gauges.put(MetricName.build("cpu.softirq"), new CpuGauge(CpuUsage.SOFTIRQ.ordinal()));
        gauges.put(MetricName.build("cpu.steal"), new CpuGauge(CpuUsage.STEAL.ordinal()));
        gauges.put(MetricName.build("cpu.guest"), new CpuGauge(CpuUsage.GUEST.ordinal()));

        gauges.put(MetricName.build("interrupts"), new PersistentGauge<Double>() {
            @Override
            public Double getValue() {
                refreshIfNecessary();
                return interruptsRate;
            }
        });

        gauges.put(MetricName.build("context_switches"), new PersistentGauge<Double>() {
            @Override
            public Double getValue() {
                refreshIfNecessary();
                return contextSwitchesRate;
            }
        });

        gauges.put(MetricName.build("process.running"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                refreshIfNecessary();
                return processRunning;
            }
        });

        gauges.put(MetricName.build("process.blocked"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                refreshIfNecessary();
                return processBlocked;
            }
        });
    }

    private class CpuGauge extends PersistentGauge<Float> {

        private int index;

        public CpuGauge(int index) {
            this.index = index;
        }

        @Override
        public Float getValue() {
            try {
                refreshIfNecessary();
                return cpuUsage[index];
            } catch (Exception e) {
                return 0.0f;
            }
        }
    }

    private class CpuInfo {
        /**
         * user: normal processes executing in user mode
         */
        long userTime;
        /**
         * nice: niced processes executing in user mode
         */
        long niceTime;
        /**
         * system: processes executing in kernel mode
         */
        long systemTime;
        /**
         * idle: twiddling thumbs
         */
        long idleTime;
        /**
         * iowait: In a word, iowait stands for waiting for I/O to complete. But there
             are several problems:
             1. Cpu will not wait for I/O to complete, iowait is the time that a task is
             waiting for I/O to complete. When cpu goes into idle state for
             outstanding task io, another task will be scheduled on this CPU.
             2. In a multi-core CPU, the task waiting for I/O to complete is not running
             on any CPU, so the iowait of each CPU is difficult to calculate.
             3. The value of iowait field in /proc/stat will decrease in certain
             conditions.
             So, the iowait is not reliable by reading from /proc/stat.
         */
        long iowaitTime;
        /**
         * irq: servicing interrupts
         */
        long irqTime;
        /**
         * softirq: servicing softirqs
         */
        long softirqTime;
        /**
         * steal: involuntary wait, since 2.6.11
         */
        long stealTime;
        /**
         * guest: running a normal guest, since 2.6.24
         */
        long guestTIme;
        /**
         * Total time
         */
        long totalTime;
    }

}
