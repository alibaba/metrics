package com.alibaba.metrics.common.filter;

import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompositeMetricFilter implements MetricFilter {

    MetricFilter[] filters;

    /**
     * 如果包含 {@link MetricFilter#ALL} 则直接丢弃，减少一次无谓的判断
     * @param filters
     */
    public CompositeMetricFilter(MetricFilter... filters) {
        List<MetricFilter> filterList = new ArrayList<MetricFilter>(Arrays.asList(filters));
        filterList.remove(MetricFilter.ALL);
        if (!filterList.isEmpty()) {
            this.filters = filterList.toArray(new MetricFilter[filterList.size()]);
        }
    }

    @Override
    public boolean matches(MetricName name, Metric metric) {
        if (filters != null) {
            for (MetricFilter filter : filters) {
                if (!filter.matches(name, metric)) {
                    return false;
                }
            }
        }
        return true;
    }


}
