package com.alibaba.metrics;

/**
 * @author wangtao 2019-01-16 10:55
 */

public interface ClusterHistogramBuilder<T> extends MetricBuilder {

    /**
     * Create a <T extends Metrics> instance with given name and buckets
     * @param name the name of the metric
     * @param buckets an array of long values
     * @return a metric implementation
     */
    T newMetric(MetricName name, long[] buckets);
}