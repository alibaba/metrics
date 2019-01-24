package com.alibaba.metrics.reporter.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.metrics.common.MetricObject;

public class JsonMetricFormat extends MetricFormat {

    PropertyFilter filter = new PropertyFilter() {

        public boolean apply(Object source, String name, Object value) {
            if ("metricLevel".equals(name)) {
                return false;
            } else {
                return true;
            }
        }
    };

    @Override
    public String format(MetricObject metric) {
        return JSON.toJSONString(metric, filter, SerializerFeature.DisableCircularReferenceDetect);
    }

    @Override
    public byte[] formatToBytes(MetricObject metric) {
        return JSON.toJSONBytes(metric, SerializerFeature.DisableCircularReferenceDetect);
    }

}
