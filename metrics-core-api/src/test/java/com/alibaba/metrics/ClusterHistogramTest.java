package com.alibaba.metrics;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author wangtao 2019-01-15 16:44
 */
public class ClusterHistogramTest {

    @Test
    public void testClusterHistogram() {
        ClusterHistogram ch = MetricManager.getClusterHistogram("test",
                MetricName.build("cluster.histogram"), new long[]{1, 10, 100});
        ch.update(100);
        Assert.assertTrue(ch.getClass().getName().startsWith("com.alibaba.metrics.NOPMetricManager"));
    }

}
