package com.alibaba.metrics.prometheus.samplebuilder;

import com.alibaba.metrics.MetricName;
import io.prometheus.client.Collector;

import java.util.List;

public interface SampleBuilder {
    Collector.MetricFamilySamples.Sample createSample(MetricName metricName, String suffix, List<String> labelNames,
                                                      List<String> labelValues, double value);
}
