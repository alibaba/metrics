package com.alibaba.metrics.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.common.MetricObject;
import org.junit.Assert;
import org.junit.Test;

public class MetricObjectSerializationTest {

    @Test
    public void testPartiallySerialize() {
        PropertyFilter filter = new PropertyFilter() {

            public boolean apply(Object source, String name, Object value) {
                boolean ignore = "value".equals(name) || "timestamp".equals(name);
                return !ignore;
            }
        };

        MetricObject obj = MetricObject.named("test").withLevel(MetricLevel.CRITICAL)
                .withType(MetricObject.MetricType.GAUGE).build();
        String json = JSON.toJSONString(obj, filter); // 序列化的时候传入filter
        JSONObject des = JSON.parseObject(json);
        Assert.assertTrue(des.containsKey("metric"));
        Assert.assertEquals("test", des.getString("metric"));
        Assert.assertTrue(des.containsKey("metricLevel"));
        Assert.assertEquals("CRITICAL", des.getString("metricLevel"));
        Assert.assertTrue(des.containsKey("metricType"));
        Assert.assertEquals("GAUGE", des.getString("metricType"));
    }
}
