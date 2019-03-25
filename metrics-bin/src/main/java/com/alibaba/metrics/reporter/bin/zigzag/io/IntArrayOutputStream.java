/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
