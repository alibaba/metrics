package com.alibaba.metrics.reporter.bin.zigzag.encodings;

public abstract class LongEncoder extends LongContext {

    protected LongEncoder(long contextValue) {
        super(contextValue);
    }

    public abstract long encodeLong(long value);

    public long[] encodeArray(long[] src, int srcoff, int length,
            long[] dst, int dstoff)
    {
        for (int i = 0; i < length; ++i) {
            dst[dstoff + i] = encodeLong(src[srcoff + i]);
        }
        return dst;
    }

    public long[] encodeArray(long[] src) {
        return encodeArray(src, 0, src.length, new long[src.length], 0);
    }
}
