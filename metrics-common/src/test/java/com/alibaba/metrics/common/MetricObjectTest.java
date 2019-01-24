package com.alibaba.metrics.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MetricObjectTest {

    @Deprecated
    @Test
    public void testRemoveIllegalOpentsdbChars() {
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("error", "aaa:bbb");
        MetricObject obj = MetricObject.named("test").withTags(tags).build();
        Assert.assertEquals("aaa:bbb should be replaced to aaa:bbb", "aaa:bbb", obj.getTags().get("error"));
    }
}
