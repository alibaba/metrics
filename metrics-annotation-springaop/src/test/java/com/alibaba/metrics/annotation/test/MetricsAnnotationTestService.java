package com.alibaba.metrics.annotation.test;

import com.alibaba.metrics.annotation.EnableCompass;
import com.alibaba.metrics.annotation.EnableCounter;
import com.alibaba.metrics.annotation.EnableFastCompass;
import com.alibaba.metrics.annotation.EnableGauge;
import com.alibaba.metrics.annotation.EnableHistogram;
import com.alibaba.metrics.annotation.EnableMeter;
import com.alibaba.metrics.annotation.EnableTimer;

public class MetricsAnnotationTestService {

    @EnableCounter(group = "test", key = "ascp.upcp-scitem.metrics-annotation.counter.test1",
        tags = "from:pc,type:dingtalk")
    public void testCounter1() {}

    @EnableCounter(group = "test", key = "ascp.upcp-scitem.metrics-annotation.counter.test2", inc = -3)
    public void testCounter2() {}

    @EnableMeter(group = "test", key = "ascp.upcp-scitem.metrics-annotation.meter.test1", tags = "purpose:test")
    public void testMeter1() {}

    @EnableMeter(group = "test", key = "ascp.upcp-scitem.metrics-annotation.meter.test2", num = 4)
    public void testMeter2() {}

    @EnableHistogram(group = "test", key = "ascp.upcp-scitem.metrics-annotation.histogram.test1", tags = "purpose:test")
    public int testHistogram1() {
        return 1;
    }

    @EnableHistogram(group = "test", key = "ascp.upcp-scitem.metrics-annotation.histogram.test1", tags = "purpose:test")
    public Integer testHistogram2() {
        return 2;
    }

    @EnableHistogram(group = "test", key = "ascp.upcp-scitem.metrics-annotation.histogram.test1", tags = "purpose:test")
    public long testHistogram3() {
        return 3L;
    }

    @EnableHistogram(group = "test", key = "ascp.upcp-scitem.metrics-annotation.histogram.test1", tags = "purpose:test")
    public Long testHistogram4() {
        return 4L;
    }

    @EnableHistogram(group = "test", key = "ascp.upcp-scitem.metrics-annotation.histogram.test1", tags = "purpose:test")
    public Long testHistogram5() {
        return null;
    }

    @EnableTimer(group = "test", key = "ascp.upcp-scitem.metrics-annotation.timer.test1", tags = "purpose:test")
    public void testTimer1() {}

    @EnableFastCompass(group = "test", key = "ascp.upcp-scitem.metrics-annotation.fastCompass.test1",
        tags = "purpose:test")
    public void testFastCompass1() {
        System.out.println();
    }

    @EnableFastCompass(group = "test", key = "ascp.upcp-scitem.metrics-annotation.fastCompass.test1",
        tags = "purpose:test")
    public void testFastCompass2() {
        throw new RuntimeException("");
    }

    @EnableCompass(group = "test", key = "ascp.upcp-scitem.metrics-annotation.compass.test1", tags = "purpose:test")
    public void testCompass1() {}

    @EnableCompass(group = "test", key = "ascp.upcp-scitem.metrics-annotation.compass.test1", tags = "purpose:test")
    public void testCompass2() {
        throw new RuntimeException("");
    }

    @EnableGauge(group = "test", key = "ascp.upcp-scitem.metrics-annotation.gauge.test1", tags = "purpose:test")
    public Long testGauge1() {
        return 3L;
    }

    @EnableGauge(group = "test", key = "ascp.upcp-scitem.metrics-annotation.gauge.test2", tags = "purpose:test")
    public long testGauge2() {
        return 3L;
    }

    @EnableGauge(group = "test", key = "ascp.upcp-scitem.metrics-annotation.gauge.test3", tags = "purpose:test")
    public Integer testGauge3() {
        return 3;
    }

    @EnableGauge(group = "test", key = "ascp.upcp-scitem.metrics-annotation.gauge.test4", tags = "purpose:test")
    public int testGauge4() {
        return 3;
    }

    @EnableGauge(group = "test", key = "ascp.upcp-scitem.metrics-annotation.gauge.test5", tags = "purpose:test")
    public Double testGauge5() {
        return 3.9;
    }

    @EnableGauge(group = "test", key = "ascp.upcp-scitem.metrics-annotation.gauge.test6", tags = "purpose:test")
    public double testGauge6() {
        return 3.9;
    }

    @EnableGauge(group = "test", key = "ascp.upcp-scitem.metrics-annotation.gauge.test7", tags = "purpose:test")
    public Float testGauge7() {
        return 3.9f;
    }

    @EnableGauge(group = "test", key = "ascp.upcp-scitem.metrics-annotation.gauge.test8", tags = "purpose:test")
    public float testGauge8() {
        return 3.9f;
    }

    @EnableGauge(group = "test", key = "ascp.upcp-scitem.metrics-annotation.gauge.test9", tags = "purpose:test")
    public Integer testGauge9() {
        return null;
    }
}
