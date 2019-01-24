package com.alibaba.metrics.reporter.bin.zigzag.io;

import java.nio.IntBuffer;

public final class IntBufferOutputStream extends IntOutputStream
{
    private final IntBuffer buffer;

    public IntBufferOutputStream(IntBuffer buffer) {
        this.buffer = buffer;
    }

    public void write(int n) {
        this.buffer.put(n);
    }

    @Override
    public void write(int[] array) {
        this.buffer.put(array);
    }

    @Override
    public void write(int[] array, int offset, int length) {
        this.buffer.put(array, offset, length);
    }
}
