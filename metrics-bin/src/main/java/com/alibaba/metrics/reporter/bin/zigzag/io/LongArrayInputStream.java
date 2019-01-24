package com.alibaba.metrics.reporter.bin.zigzag.io;

import java.nio.LongBuffer;

public class LongArrayInputStream extends LongInputStream
{
    protected final LongBuffer buffer;

    public LongArrayInputStream(long[] array, int off, int len) {
        this.buffer = LongBuffer.wrap(array, off, len);
    }

    public LongArrayInputStream(long[] array) {
        this(array, 0, array.length);
    }

    public Long read() {
        return this.buffer.hasRemaining()
            ? Long.valueOf(this.buffer.get()) : null;
    }
}
