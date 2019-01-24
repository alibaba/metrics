package com.alibaba.metrics.reporter.file;

import com.alibaba.metrics.common.MetricObject;

public abstract class MetricFormat {
    public abstract String format(MetricObject metric);
    public abstract byte[] formatToBytes(MetricObject metric);
}
