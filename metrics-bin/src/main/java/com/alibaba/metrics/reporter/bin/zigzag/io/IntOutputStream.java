package com.alibaba.metrics.reporter.bin.zigzag.io;

/**
 * Output stream for int values..
 */
public abstract class IntOutputStream
{
    public abstract void write(int n);

    public void write(int[] array) {
        write(array, 0, array.length);
    }

    public void write(int[] array, int offset, int length) {
        for (int i = offset, end = offset + length; i < end; ++i) {
            write(array[i]);
        }
    }
}
