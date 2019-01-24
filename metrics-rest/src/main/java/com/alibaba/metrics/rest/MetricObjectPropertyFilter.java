package com.alibaba.metrics.rest;

import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.metrics.common.MetricObject;

/**
 * list接口序列化MetricObject时过滤掉timestamp/value
 */
public class MetricObjectPropertyFilter implements PropertyFilter {

    public boolean apply(Object source, String name, Object value) {
        if (source instanceof MetricObject) {
            boolean ignore = "value".equals(name) || "timestamp".equals(name);
            return !ignore;
        }
        return true;
    }
}
