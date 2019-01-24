package com.alibaba.metrics.reporter.bin.zigzag.io;

import java.nio.LongBuffer;

public final class LongBufferOutputStream extends LongOutputStream
{
    private final LongBuffer buffer;

    public LongBufferOutputStream(LongBuffer buffer) {
        this.buffer = buffer;
    }

    public void write(long n) {
        this.buffer.put(n);
    }

    @Override
    public void write(long[] array) {
        this.buffer.put(array);
    }

    @Override
    public void write(long[] array, int offset, int length) {
        this.buffer.put(array, offset, length);
    }
}
