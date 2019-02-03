package com.alibaba.metrics.os.windows;

import com.alibaba.metrics.CachedMetricSet;
import com.alibaba.metrics.Clock;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.PersistentGauge;
import com.alibaba.metrics.RatioGauge;
import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//import org.hyperic.sigar.CpuInfo;
//import org.hyperic.sigar.CpuPerc;
//import org.hyperic.sigar.Sigar;
//import org.hyperic.sigar.SigarException;

public class SystemMemoryGaugeSet extends CachedMetricSet{

    private static final Logger logger = LoggerFactory.getLogger(SystemMemoryGaugeSet.class);

    private static OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private Map<MetricName, Metric> gauges;

    private static final String[] METRICS = {
            "mem.total",      // MemTotal
            "mem.used",       // MemTotal - (MemFree + Buffers + Cached)
            "mem.free",       // MemFree
            "mem.buffers",    // Buffers
            "mem.cached",     // Cached
            "mem.swap.total", // SwapTotal
            "mem.swap.used",  // SwapTotal - SwapFree
            "mem.swap.free",  // SwapFree
    };

    private long[] data;

    public SystemMemoryGaugeSet(long dataTTL, TimeUnit unit, Clock clock){
        super(dataTTL, unit, clock);
        this.data = new long[METRICS.length];
        this.gauges = new HashMap<MetricName, Metric>();
        populateGauges();
    }

    public SystemMemoryGaugeSet(long dataTTL, TimeUnit unit) {
        this(dataTTL, unit, Clock.defaultClock());
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        return gauges;
    }

    @Override
    protected void getValueInternal() {
        data[0] = osmxb.getTotalPhysicalMemorySize() / 1024;
        data[2] = osmxb.getFreePhysicalMemorySize() / 1024;
        data[3] = 0;
        data[4] = 0;
        data[5] = osmxb.getTotalSwapSpaceSize() / 1024;
        data[6] = 0;
        data[7] = osmxb.getFreeSwapSpaceSize() / 1024;
        data[1] = data[0] - data[2];
    }

    private void populateGauges() {
        for (int i = 0; i < METRICS.length; i++) {
            gauges.put(MetricName.build(METRICS[i]), new MemGauge(i));
        }

        final RatioGauge usedRatio = new RatioGauge() {
            @Override
            @SuppressWarnings("unchecked")
            protected Ratio getRatio() {
                Gauge<Long> memUsed = (Gauge<Long>)gauges.get(MetricName.build("mem.used"));
                Gauge<Long> memTotal = (Gauge<Long>)gauges.get(MetricName.build("mem.total"));
                return Ratio.of(memUsed.getValue().doubleValue(), memTotal.getValue().doubleValue());
            }
        };

        gauges.put(MetricName.build("mem.used_ratio"), usedRatio);
    }

    private class MemGauge extends PersistentGauge<Long> {

        private int index;

        public MemGauge(int index) {
            this.index = index;
        }

        @Override
        public Long getValue() {
            try {
                refreshIfNecessary();
                return data[index];
            } catch (Exception e) {
                return 0L;
            }
        }
    }

}
