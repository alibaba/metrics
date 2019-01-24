package com.alibaba.metrics.reporter.bin.zigzag.io;

public class IntArrayOutputStream extends IntOutputStream
{
    private int[] array;

    private int writtenCount;

    public IntArrayOutputStream(int capacity) {
        this.array = new int[capacity];
        this.writtenCount = 0;
    }

    public IntArrayOutputStream() {
        this(512);
    }

    static int calcNewSize(int curr, int ext, int max) {
        // Determine required size.
        int required = curr + ext;
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
        int[] newArray = new int[newSize];
        System.arraycopy(this.array, 0, newArray, 0, this.writtenCount);
        this.array = newArray;
    }

    public void write(int n) {
        extend(1);
        this.array[this.writtenCount++] = n;
    }

    @Override
    public void write(int[] array, int offset, int length) {
        extend(length);
        System.arraycopy(array, offset, this.array, this.writtenCount, length);
        this.writtenCount += length;
    }

    /**
     * Get count of written int values.
     */
    public int count() {
        return this.writtenCount;
    }

    /**
     * Get int array.
     */
    public int[] toIntArray() {
        if (this.array.length == this.writtenCount) {
            return this.array;
        } else {
            int[] newArray = new int[this.writtenCount];
            System.arraycopy(this.array, 0, newArray, 0, this.writtenCount);
            return newArray;
        }
    }
}
