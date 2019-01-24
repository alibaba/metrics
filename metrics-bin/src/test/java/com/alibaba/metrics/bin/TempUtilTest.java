package com.alibaba.metrics.bin;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.alibaba.metrics.utils.Constants;
import com.alibaba.metrics.utils.FigureUtil;

public class TempUtilTest {

    @Test
    public void splitRangeByDayTest(){

        //starttime > endtime

        long startTime0 = 1498132895000L;
        long endTime0 = 1498122885000L;

        List<Long> result0 = FigureUtil.splitRangeByDay(startTime0, endTime0);
        assert result0.size() == 0;

        //两个点在同一天
        long startTime1 = 1498122885000L;
        long endTime1 = 1498132895000L;

        List<Long> result1 = FigureUtil.splitRangeByDay(startTime1, endTime1);
        assert result1.size() == 2;
        assert result1.get(0) == 1498122885000L;
        assert result1.get(1) == 1498132895000L;

        //两点跨一天
        long startTime2 = 1498011885000L;
        long endTime2 = 1498132895000L;

        List<Long> result2 = FigureUtil.splitRangeByDay(startTime2, endTime2);

//        for(Long l : result2){
//            System.out.println(l);
//        }

        assert result2.size() == 4;
        assert result2.get(0) == 1498011885000L;
        assert result2.get(1) == 1498060799000L;
        assert result2.get(2) == 1498060800000L;
        assert result2.get(3) == 1498132895000L;

        //两点跨多天
        long startTime3 = 1497832895000L;
        long endTime3 = 1498132895000L;

        List<Long> result3 = FigureUtil.splitRangeByDay(startTime3, endTime3);

        assert result3.size() == 8;
        assert result3.get(0) == 1497832895000L;
        assert result3.get(1) == 1497887999000L;
        assert result3.get(2) == 1497888000000L;
        assert result3.get(3) == 1497974399000L;
        assert result3.get(4) == 1497974400000L;
        assert result3.get(5) == 1498060799000L;
        assert result3.get(6) == 1498060800000L;
        assert result3.get(7) == 1498132895000L;

    }

    @Test
    public void longAndDoubleConvert(){

        double d = 7.471568368156572;
        long l = FigureUtil.doubleRoundToLong(d);
        assert l == 74716;

        double t = FigureUtil.longToDouble(l);
        assert t == 7.4716;

        double d0 = 0.0000001;
        long l0 = FigureUtil.doubleRoundToLong(d0);
        assert l0 == 0;

        double t0 = FigureUtil.longToDouble(l0);
        assert t0 == 0;

        double d1 = 0.000991801110817244;
        long l1 = FigureUtil.doubleRoundToLong(d1);
        assert l1 == 10;

        double t1 = FigureUtil.longToDouble(l1);
        assert t1 == 0.001;

    }

    @Test
    public void getStartTimestampTest() {

        long[] l = {1498838434040L, 1498858407999L, 1498878987000L, 1498891230000L, 1498918120000L, 1498924799999L};

        for(long timestamp : l){
            assert FigureUtil.getTodayStartTimestamp(timestamp) == 1498838400000L;
            assert FigureUtil.getNextDayStartTimestamp(timestamp) == 1498924800000L;
        }

    }

    @Test
    public void checkZero(){

        Long l1 = 1L;
        Integer i1 = 1;
        Double d1 = 1.0;
        Float f1 = 1.0f;

        Long l0 = 0L;
        Integer i0 = 0;
        Double d0 = 0.0;
        Float f0 = 0.0f;

        assert FigureUtil.checkZero(l1) == false;
        assert FigureUtil.checkZero(i1) == false;
        assert FigureUtil.checkZero(d1) == false;
        assert FigureUtil.checkZero(f1) == false;

        assert FigureUtil.checkZero(l0) == true;
        assert FigureUtil.checkZero(i0) == true;
        assert FigureUtil.checkZero(d0) == true;
        assert FigureUtil.checkZero(f0) == true;

    }
}
