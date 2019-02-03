package com.alibaba.metrics.reporter.bin.zigzag.io;

import com.alibaba.metrics.reporter.bin.zigzag.utils.Reader;

public abstract class IntInputStream implements Reader<Integer>
{
    public abstract Integer read();

    public int read(int[] array) {
        return read(array, 0, array.length);
    }

    public int read(int[] array, int offset, int length) {
        int i;
        int end;
        for (i = offset, end = offset + length; i < end; ++i) {
            Integer n = read();
            if (n == null) {
                break;
            }
            array[i] = n;
        }
        return i - offset;
    }
}
