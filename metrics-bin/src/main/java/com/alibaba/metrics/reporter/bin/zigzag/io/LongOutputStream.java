package com.alibaba.metrics.reporter.bin.zigzag.io;

/**
 * Output stream for long values..
 */
public abstract class LongOutputStream
{
    public abstract void write(long n);

    public void write(long[] array) {
        write(array, 0, array.length);
    }

    public void write(long[] array, int offset, int length) {
        for (int i = offset, end = offset + length; i < end; ++i) {
            write(array[i]);
        }
    }
}
