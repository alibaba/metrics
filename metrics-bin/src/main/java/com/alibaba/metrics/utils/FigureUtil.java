package com.alibaba.metrics.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.alibaba.metrics.common.MetricObject;

import static com.alibaba.metrics.common.MetricObject.*;
import static com.alibaba.metrics.utils.Constants.*;

public class FigureUtil {

    public static long getLong(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7) {

        int high = getInt(b0, b1, b2, b3);
        int low = getInt(b4, b5, b6, b7);

        return ((long) (high) << 32) + (low & 0xFFFFFFFFL);

    }

    public static int getInt(byte b0, byte b1, byte b2, byte b3) {

        return ((b0 << 24) & 0xFF000000) + ((b1 << 16) & 0x00FF0000) + ((b2 << 8) & 0x0000FF00)
                + ((b3 << 0) & 0x000000FF);
    }

    public static long getLong(byte[] b, int start) {

        int high = getInt(b, start);
        int low = getInt(b, start + 4);

        return ((long) (high) << 32) + (low & 0xFFFFFFFFL);
    }

    public static int getInt(byte b[], int start) {

        return ((b[start] << 24) & 0xFF000000) + ((b[start + 1] << 16) & 0x00FF0000)
                + ((b[start + 2] << 8) & 0x0000FF00) + ((b[start + 3] << 0) & 0x000000FF);
    }

    public static String getString(byte[] b, int start, int length) {

        return new String(b, start, length);
    }

    public static double getDouble(byte b[], int start) {
        return Double.longBitsToDouble(getLong(b, start));
    }

    // public static String getCombineName(MetricObject metrics){
    // if (metrics == null){
    // return null;
    // }
    //
    // String metric = metrics.getMetric();
    // Map<String, String> tags = metrics.getTags();
    //
    // if (mapIsEmpty(tags)){
    // return metric;
    // }else{
    // StringBuilder sb = new StringBuilder(50);
    // boolean first = true;
    //
    // sb.append(metric);
    // sb.append(METRICS_KEY_SEPARATOR);
    //
    // for (Map.Entry<String, String> entry : tags.entrySet()) {
    // if (!first){
    // sb.append(TAGS_SEPARATOR);
    // }
    // sb.append(entry.getKey());
    // sb.append(TAG_KV_SEPARATOR);
    // sb.append(entry.getValue());
    // first = false;
    // }
    //
    // return sb.toString();
    // }
    //
    // }

    public static boolean mapIsEmpty(Map<?, ?> map) {

        if (map == null) {
            return true;
        } else if (map.size() == 0) {
            return true;
        } else {
            return false;
        }

    }

    public static int getValueLength(Object o) {

        if (o instanceof Integer) {
            return INT_LENGTH;
        }

        if (o instanceof Long) {
            return LONG_LENGTH;
        }

        if (o instanceof Double) {
            return DOUBLE_LENGTH;
        }

        if (o instanceof Float) {
            return FLOAT_LENGTH;
        }

        return LONG_LENGTH;

    }

    public static int getValueType(Object o) {

        if (o instanceof Integer) {
            return TYPE_INT;
        }

        if (o instanceof Long) {
            return TYPE_LONG;
        }

        if (o instanceof Double) {
            return TYPE_DOUBLE;
        }

        if (o instanceof Float){
            return TYPE_FLOAT;
        }

        return TYPE_LONG;
    }

    public static Object getValueByType(int fieldType, long value){

        Object o = null;

        switch(fieldType){
        case TYPE_INT:
            o = (int) value;
            break;
        case TYPE_DOUBLE:
            o = longToDouble(value);
            break;
        case TYPE_LONG:
            o = value;
            break;
        case TYPE_FLOAT:
            o = longToFloat(value);
            break;
        default:
            o = value;
        }

        return o;
    }

    public static long convertToLong(Object o){

        long value = 0;

        if (o instanceof Long) {
            value = (Long) o;
        }

        if (o instanceof Integer) {
            value = ((Integer) o).longValue();
        }

        if (o instanceof Double) {
            value = FigureUtil.doubleRoundToLong((Double) o);
            //value = Double.doubleToLongBits((Double) o);
        }

        if (o instanceof Float){
            value = FigureUtil.floatRoundToLong((Float) o);
        }


        return value;
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static long getTodayStartTimestamp(long timestamp) {
        // Date currentDate = new Date(timestamp);
        // Date baseDate = new Date(currentDate.getYear(),
        // currentDate.getMonth(), currentDate.getDate());
        // long baseTimestamp = baseDate.getTime();
        //
        // return baseTimestamp;
        return (timestamp + Constants.UTC_PLUS_8_ADJUST) / Constants.DAY_MILLISECONDS * Constants.DAY_MILLISECONDS - Constants.UTC_PLUS_8_ADJUST;
    }

    public static long getNextDayStartTimestamp(long timestamp) {
        // Date currentDate = new Date(timestamp);
        // Date baseDate = new Date(currentDate.getYear(),
        // currentDate.getMonth(), currentDate.getDate() + 1);
        // long nextTimestamp = baseDate.getTime();
        //
        // return nextTimestamp;
        return ((timestamp + Constants.UTC_PLUS_8_ADJUST) / Constants.DAY_MILLISECONDS + 1) * Constants.DAY_MILLISECONDS - Constants.UTC_PLUS_8_ADJUST;
    }

    public static byte[] getLongBytes(long value) {
        byte[] b = new byte[8];
        b[0] = (byte) ((int) (value >>> 56) & 0xFF);
        b[1] = (byte) ((int) (value >>> 48) & 0xFF);
        b[2] = (byte) ((int) (value >>> 40) & 0xFF);
        b[3] = (byte) ((int) (value >>> 32) & 0xFF);
        b[4] = (byte) ((int) (value >>> 24) & 0xFF);
        b[5] = (byte) ((int) (value >>> 16) & 0xFF);
        b[6] = (byte) ((int) (value >>> 8) & 0xFF);
        b[7] = (byte) ((int) (value >>> 0) & 0xFF);
        return b;
    }

    public static List<Long> splitRangeByDay(long startTime, long endTime) {

        List<Long> result = new ArrayList<Long>();

        if (startTime > endTime) {
            return result;
        }

        long baseStartTime = FigureUtil.getNextDayStartTimestamp(startTime);
        long baseEndTime = FigureUtil.getTodayStartTimestamp(endTime);

        if (baseStartTime > baseEndTime) {
            result.add(startTime);
            result.add(endTime);
        } else {
            int crossingDay = (int) ((baseEndTime - baseStartTime) / Constants.DAY_MILLISECONDS);
            result.add(startTime);

            for (int i = 0; i <= crossingDay; i++) {
                result.add((long) (baseStartTime + i * Constants.DAY_MILLISECONDS - 1000));
                result.add((long) (baseStartTime + i * Constants.DAY_MILLISECONDS));
            }

            result.add(endTime);
        }

        return result;
    }

    public static long doubleRoundToLong(double d) {
        return Math.round(d * 10000);
    }

    public static long floatRoundToLong(float f){
        return Math.round(f * 10000);
    }

    public static double longToDouble(long l) {
        return (double) l / 10000;
    }

    public static float longToFloat(long l) {
        return (float) l / 10000;
    }

    public static boolean checkZero(Object o){

        if (o instanceof Long && ((Long) o ) == 0) {
            return true;
        }

        if (o instanceof Integer && ((Integer) o ) == 0) {
            return true;
        }

        if (o instanceof Double && ((Double) o ) == 0) {
            return true;
        }

        if (o instanceof Float && ((Float) o ) == 0){
            return true;
        }

        return false;
    }

    public static void main(String[] args) {
        System.out.println(floatRoundToLong(97.99F));
    }

}
