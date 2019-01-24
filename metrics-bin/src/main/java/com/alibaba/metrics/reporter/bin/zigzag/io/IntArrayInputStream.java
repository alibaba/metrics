package com.alibaba.metrics.reporter.bin.zigzag.io;

import java.nio.IntBuffer;

public class IntArrayInputStream extends IntInputStream
{
    protected final IntBuffer buffer;

    public IntArrayInputStream(int[] array, int off, int len) {
        this.buffer = IntBuffer.wrap(array, off, len);
    }

    public IntArrayInputStream(int[] array) {
        this(array, 0, array.length);
    }

    public Integer read() {
        return this.buffer.hasRemaining()
            ? Integer.valueOf(this.buffer.get()) : null;
    }
}
