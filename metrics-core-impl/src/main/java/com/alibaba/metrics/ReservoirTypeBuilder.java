package com.alibaba.metrics;

public interface ReservoirTypeBuilder<T> extends MetricBuilder {

    /**
     * Create a <T extends Metrics> instance with given reservoir type
     * @param name the name of the metric
     * @param type the type of reservoir type specified in {@link ReservoirType}
     * @return a metric implementation
     */
    T newMetric(MetricName name, ReservoirType type);
}
