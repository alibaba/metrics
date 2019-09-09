package com.alibaba.metrics.prometheus.samplebuilder;

import com.alibaba.metrics.MetricName;
import io.prometheus.client.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultSampleBuilder implements SampleBuilder{

    @Override
    public Collector.MetricFamilySamples.Sample createSample(MetricName metricName, String suffix, List<String> labelNames, List<String> labelValues, double value) {
        String name = metricName.getKey();
        String nameSuffix = suffix == null ? "" : suffix;
        Map<String, String> tags = metricName.getTags();
        List<String> tagNames = labelNames == null ? new ArrayList<String>() : labelNames;
        List<String> tagValues = labelValues == null ? new ArrayList<String>() : labelValues;
        if (tags != null && tags.size() > 0) {
            for (Map.Entry<String, String> entry : tags.entrySet()) {
                tagNames.add(entry.getKey());
                tagValues.add(entry.getValue());
            }
        }
        return new Collector.MetricFamilySamples.Sample(
                Collector.sanitizeMetricName(name + nameSuffix),
                tagNames,
                tagValues,
                value);
    }
}
