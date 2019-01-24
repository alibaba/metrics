package com.alibaba.metrics.rest;

import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricName;

public class MetricLevelFilter implements MetricFilter {

    private boolean matchLevelAbove;

    private MetricLevel level;

    public MetricLevelFilter(MetricLevel level, boolean above) {
        this.level = level;
        this.matchLevelAbove = above;
    }

    public boolean matches(MetricName name, Metric metric) {
        if (matchLevelAbove) {
            return (level != null) && level.compareTo(name.getMetricLevel()) <= 0;
        } else {
            return (level != null) && level.toString().equalsIgnoreCase(name.getMetricLevel().toString());
        }
    }
}
