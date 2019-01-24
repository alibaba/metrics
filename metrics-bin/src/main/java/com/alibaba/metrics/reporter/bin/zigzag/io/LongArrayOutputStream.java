package com.alibaba.metrics.reporter.bin.zigzag.io;

public class LongArrayOutputStream extends LongOutputStream
{
    private long[] array;

    private int writtenCount;

    public LongArrayOutputStream(int capacity) {
        this.array = new long[capacity];
        this.writtenCount = 0;
    }

    public LongArrayOutputStream() {
        this(512);
    }

    static int calcNewSize(int curr, int ext, int max) {
        // Determine required size.
        int required = curr + ext;
        if (max == 0){
            return max;
        }
        if (required < 0) {
            throw new RuntimeException("Required length was minus");
        } else if (required <= max) {
            return max;
        }

        // Determine new size.
        int newSize = max;
        while (required > newSize) {
            newSize *= 2;
            if (newSize < 0) {
                throw new RuntimeException("Buffer overflow");
            }
        }
        return newSize;
    }

    /**
     * Extend buffer by len.
     */
    private void extend(int len) {
        // Alloacte a new buffer, replace old by it.
        int newSize = calcNewSize(this.writtenCount, len, this.array.length);
        if (newSize <= this.array.length) {
            return;
        }
        long[] newArray = new long[newSize];
        System.arraycopy(this.array, 0, newArray, 0, this.writtenCount);
        this.array = newArray;
    }

    public void write(long n) {
        extend(1);
        this.array[this.writtenCount++] = n;
    }

    @Override
    public void write(long[] array, int offset, int length) {
        if (length == 0){
            return;
        }
        extend(length);
        System.arraycopy(array, offset, this.array, this.writtenCount, length);
        this.writtenCount += length;
    }

    /**
     * Get count of written long values.
     */
    public int count() {
        return this.writtenCount;
    }

    /**
     * Get long array.
     */
    public long[] toLongArray() {
        if (this.array.length == this.writtenCount) {
            return this.array;
        } else {
            long[] newArray = new long[this.writtenCount];
            System.arraycopy(this.array, 0, newArray, 0, this.writtenCount);
            return newArray;
        }
    }
}
