package com.alibaba.metrics.bin;

import org.junit.Test;

import com.alibaba.metrics.utils.FIFOMap;

public class FIFOMapTest {

    @Test
    public void FIFOMapTest() {
        FIFOMap<Long, String> cacheMap = new FIFOMap<Long, String>(10);
        for (int i = 1; i <= 45; i++) {
            cacheMap.put((long) i, "value" + i);
        }

        assert cacheMap.firstKey() == 36;
        assert "36:value36 37:value37 38:value38 39:value39 40:value40 41:value41 42:value42 43:value43 44:value44 45:value45 "
                .equals(cacheMap.toString());
    }
}
