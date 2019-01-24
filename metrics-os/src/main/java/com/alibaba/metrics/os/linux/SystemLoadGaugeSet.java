package com.alibaba.metrics.os.linux;

import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.CachedMetricSet;
import com.alibaba.metrics.PersistentGauge;
import com.alibaba.metrics.os.utils.FileUtils;
import com.alibaba.metrics.os.utils.FormatUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemLoadGaugeSet extends CachedMetricSet {

    private static final String DEFAULT_FILE_PATH = "/proc/loadavg";

    // Pattern.DOTALL to match pattern across multiple lines
    private static final Pattern loadPattern =
            Pattern.compile("^([\\d\\.]+)\\s+([\\d\\.]+)\\s+([\\d\\.]+)\\s+[\\d]+/[\\d]+\\s+([\\d]+).*$",
                    Pattern.DOTALL);

    private enum LoadAvg {
        ONE_MIN, FIVE_MIN, FIFTEEN_MIN
    }

    // store the system load average, in the order of 1min, 5min, 15min
    private float[] loadAvg;

    private String filePath;

    public SystemLoadGaugeSet() {
        this(DEFAULT_DATA_TTL, TimeUnit.MILLISECONDS, DEFAULT_FILE_PATH);
    }

    public SystemLoadGaugeSet(long dataTTL, TimeUnit unit) {
        this(dataTTL, unit, DEFAULT_FILE_PATH);
    }

    public SystemLoadGaugeSet(long dataTTL, TimeUnit unit, String filePath) {
        super(dataTTL, unit);
        loadAvg = new float[LoadAvg.values().length];
        this.filePath = filePath;
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
        String loadResult = FileUtils.readFile(filePath);
        Matcher loadMatcher = loadPattern.matcher(loadResult);

        if (loadMatcher.matches()) {
            loadAvg[LoadAvg.ONE_MIN.ordinal()] = FormatUtils.formatFloat(loadMatcher.group(1));
            loadAvg[LoadAvg.FIVE_MIN.ordinal()] = FormatUtils.formatFloat(loadMatcher.group(2));
            loadAvg[LoadAvg.FIFTEEN_MIN.ordinal()] = FormatUtils.formatFloat(loadMatcher.group(3));
        }
    }
}
