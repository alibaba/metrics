package com.alibaba.metrics.utils;

public class ByteArrayUtil {

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

    public static byte[] getDoubleBytes(double value) {
        byte[] bytes = getLongBytes(Double.doubleToLongBits(value));
        return bytes;
    }

}
