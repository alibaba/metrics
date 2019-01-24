package com.alibaba.metrics.reporter.file;

import com.alibaba.metrics.common.MetricObject;

import java.nio.charset.Charset;
import java.util.Map.Entry;

/**
 * <p>
 * metric timestamp value tagk=tagv tagk=tagv
 * </p>
 *
 *
 */
public class SimpleTextMetricFormat extends MetricFormat {
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    public String format(MetricObject metric) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(metric.getMetric()).append(' ').append(metric.getTimestamp()).append(' ').append(metric.getValue());

        for (Entry<String, String> entry : metric.getTags().entrySet()) {
            sb.append(' ').append(entry.getKey()).append('=').append(entry.getValue());
        }
        return sb.toString();
    }

    @Override
    public byte[] formatToBytes(MetricObject metric) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(metric.getMetric()).append(' ').append(metric.getTimestamp()).append(' ').append(metric.getValue());

        for (Entry<String, String> entry : metric.getTags().entrySet()) {
            sb.append(' ').append(entry.getKey()).append('=').append(entry.getValue());
        }
        return sb.toString().getBytes(UTF_8);
    }
}
