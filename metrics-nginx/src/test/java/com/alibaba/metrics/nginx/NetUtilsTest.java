package com.alibaba.metrics.nginx;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class NetUtilsTest {

    @Ignore
    @Test
    public void testNetUtils() {
        NetUtils.Response response = NetUtils.request("127.0.0.1", 80, "/nginx_status", "127.0.0.1");
        System.out.println(response.getContent());
    }

    @Test
    public void testNginxNotExist() {
        NetUtils.Response response = NetUtils.request("192.168.0.1", 80, "/nginx_status", "127.0.0.1");
        Assert.assertFalse(response.isSuccess());
    }
}
