package com.alibaba.metrics.jvm;

import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricSet;
import com.alibaba.metrics.PersistentGauge;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * A set of gauges for JVM classloader usage.
 */
public class ClassLoadingGaugeSet implements MetricSet {

    private final ClassLoadingMXBean mxBean;

    public ClassLoadingGaugeSet() {
        this(ManagementFactory.getClassLoadingMXBean());
    }

    public ClassLoadingGaugeSet(ClassLoadingMXBean mxBean) {
        this.mxBean = mxBean;
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        final Map<MetricName, Metric> gauges = new HashMap<MetricName, Metric>();

        gauges.put(MetricName.build("loaded"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getTotalLoadedClassCount();
            }
        });

        gauges.put(MetricName.build("loaded_current"), new PersistentGauge<Integer>() {
            @Override
            public Integer getValue() {
                return mxBean.getLoadedClassCount();
            }
        });

        gauges.put(MetricName.build("unloaded"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getUnloadedClassCount();
            }
        });

        return gauges;
    }

    @Override
    public long lastUpdateTime() {
        return System.currentTimeMillis();
    }
}
