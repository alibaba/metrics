package com.alibaba.metrics.reporter.bin.zigzag.encodings;

public abstract class IntEncoder extends IntContext {

    protected IntEncoder(int contextValue) {
        super(contextValue);
    }

    public abstract int encodeInt(int value);

    public int[] encodeArray(int[] src, int srcoff, int length,
            int[] dst, int dstoff)
    {
        for (int i = 0; i < length; ++i) {
            dst[dstoff + i] = encodeInt(src[srcoff + i]);
        }
        return dst;
    }

    public int[] encodeArray(int[] src) {
        return encodeArray(src, 0, src.length, new int[src.length], 0);
    }
}
