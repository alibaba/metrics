package com.alibaba.metrics.reporter;

import com.alibaba.metrics.MetricName;

import javax.management.ObjectName;

public interface ObjectNameFactory {

	ObjectName createName(String type, String domain, MetricName name);
}
