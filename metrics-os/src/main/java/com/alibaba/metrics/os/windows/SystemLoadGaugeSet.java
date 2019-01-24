package com.alibaba.metrics.os.windows;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import org.hyperic.sigar.SigarException;

import com.alibaba.metrics.CachedMetricSet;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.PersistentGauge;
import static com.alibaba.metrics.os.utils.SystemInfoUtils.*;

public class SystemLoadGaugeSet extends CachedMetricSet {

    private Map<MetricName, Metric> gauges;

    private enum LoadAvg {
        ONE_MIN, FIVE_MIN, FIFTEEN_MIN
    }

    private float[] loadAvg;

    public SystemLoadGaugeSet() {
        this(DEFAULT_DATA_TTL, TimeUnit.MILLISECONDS);
    }

    public SystemLoadGaugeSet(long dataTTL, TimeUnit unit) {
        super(dataTTL, unit);
        loadAvg = new float[LoadAvg.values().length];
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        final Map<MetricName, Metric> gauges = new HashMap<MetricName, Metric>();

        gauges.put(MetricName.build("load.1min"), new PersistentGauge<Float>() {
            @Override
            public Float getValue() {
                refreshIfNecessary();
                return loadAvg[LoadAvg.ONE_MIN.ordinal()];
            }
        });

        gauges.put(MetricName.build("load.5min"), new PersistentGauge<Float>() {
            @Override
            public Float getValue() {
                refreshIfNecessary();
                return loadAvg[LoadAvg.FIVE_MIN.ordinal()];
            }
        });

        gauges.put(MetricName.build("load.15min"), new PersistentGauge<Float>() {
            @Override
            public Float getValue() {
                refreshIfNecessary();
                return loadAvg[LoadAvg.FIFTEEN_MIN.ordinal()];
            }
        });

        return gauges;
    }

    @Override
    protected void getValueInternal() {

        double[] load = null;
        try {
            load = sigar.getLoadAverage();
        } catch (Throwable e) {
        }

        if (load != null){

            if (load.length > 0){
                loadAvg[LoadAvg.ONE_MIN.ordinal()] = (float) load[0];
            }

            if (load.length > 1){
                loadAvg[LoadAvg.FIVE_MIN.ordinal()] = (float) load[1];
            }

            if (load.length > 2){
                loadAvg[LoadAvg.FIFTEEN_MIN.ordinal()] = (float) load[2];
            } else {
                load = new double[3];
            }
        }

    }

}
