package com.alibaba.metrics.common.filter;

import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import org.junit.Assert;
import org.junit.Test;

public class CompositeMetricFilterTest {

    @Test
    public void testNoFilters() {

        CompositeMetricFilter filter = new CompositeMetricFilter(MetricFilter.ALL);

        Assert.assertArrayEquals(null, filter.filters);

        MetricName name = MetricName.build("test");

        Assert.assertTrue(filter.matches(name, MetricManager.getCounter("ttt", name)));

    }


    @Test
    public void testOneFilter() {

        MetricFilter filter2 = new MetricFilter() {
            @Override
            public boolean matches(MetricName name, Metric metric) {
                return name.getKey().equals("test");
            }
        };

        CompositeMetricFilter filter = new CompositeMetricFilter(MetricFilter.ALL, filter2);

        Assert.assertArrayEquals(new MetricFilter[]{filter2}, filter.filters);

        MetricName name = MetricName.build("test");

        Assert.assertTrue(filter.matches(name, MetricManager.getCounter("ttt", name)));

        MetricName name2 = MetricName.build("test2");

        Assert.assertFalse(filter.matches(name2, MetricManager.getCounter("ttt", name2)));

    }
}
