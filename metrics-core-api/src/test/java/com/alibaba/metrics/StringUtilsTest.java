package com.alibaba.metrics;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testRemoveIllegalOpentsdbChars(){
        Assert.assertNull(StringUtils.removeIllegalOpentsdbChars(null));
    }
}
