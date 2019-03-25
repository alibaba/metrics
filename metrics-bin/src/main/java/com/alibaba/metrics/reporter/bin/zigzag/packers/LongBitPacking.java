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
package com.alibaba.metrics.reporter.bin.zigzag.packers;

import com.alibaba.metrics.reporter.bin.zigzag.LongCodec;
import com.alibaba.metrics.reporter.bin.zigzag.filters.LongFilter;
import com.alibaba.metrics.reporter.bin.zigzag.filters.ThroughLongFilter;
import com.alibaba.metrics.reporter.bin.zigzag.io.LongOutputStream;

import java.nio.LongBuffer;

public class LongBitPacking extends LongCodec
{
    public static final int BLOCK_LEN = 16;
    public static final int BLOCK_NUM = 4;

    private static final long[] MASKS = newMasks();

    private static final LongFilter THROUGH_FILTER = new ThroughLongFilter();

    private boolean debug = false;

    private final int blockLen;

    private final int blockNum;

    private final int[] maxBits;

    private final long[] packBuf;

    public LongBitPacking(int blockLen, int blockNum) {
        this.blockLen = blockLen;
        this.blockNum = blockNum;
        this.maxBits = new int[blockNum];
        this.packBuf = new long[blockLen];
    }

    public LongBitPacking() {
        this(BLOCK_LEN, BLOCK_NUM);
    }

    public LongBitPacking setDebug(boolean value) {
        this.debug = value;
        return this;
    }

    public boolean getDebug() {
        return this.debug;
    }

    public int getBlockLen() {
        return this.blockLen;
    }

    public int getBlockNum() {
        return this.blockNum;
    }

    public int getBlockSize() {
        return this.blockLen * this.blockNum;
    }

    public static long[] newMasks() {
        long[] masks = new long[65];
        long m = 0xffffffffffffffffL;
        for (int i = 64; i >= 0; --i) {
            masks[i] = m;
            m >>>= 1;
        }
        return masks;
    }

    public static int countMaxBits(
            LongBuffer buf,
            int len,
            LongFilter filter)
    {
        long n = 0;
        for (int i = len; i > 0; --i) {
            n |= filter.filterLong(buf.get());
        }
        return 64 - Long.numberOfLeadingZeros(n);
    }

    public static int countMaxBits(LongBuffer buf, int len) {
        return countMaxBits(buf, len, THROUGH_FILTER);
    }

    public void pack(
            LongBuffer src,
            LongOutputStream dst,
            int validBits,
            int len)
    {
        pack(src, dst, validBits, len, THROUGH_FILTER);
    }

    public void pack(
            LongBuffer src,
            LongOutputStream dst,
            int validBits,
            int len,
            LongFilter filter)
    {
        switch (validBits) {
            case 0:
                pack0(src, dst, len);
                break;
            default:
                packAny(src, dst, validBits, len, filter);
                break;
        }
    }

    public void pack0(
            LongBuffer src,
            LongOutputStream dst,
            int len)
    {
        src.position(src.position() + len);
    }

    public void packAny(
            LongBuffer src,
            LongOutputStream dst,
            int validBits,
            int len)
    {
        packAny(src, dst, validBits, len, THROUGH_FILTER);
    }

    public void packAny(
            LongBuffer src,
            LongOutputStream dst,
            int validBits,
            int len,
            LongFilter filter)
    {
        long current = 0;
        int capacity = Long.SIZE;
        long mask = MASKS[validBits];
        int packIndex = 0;
        for (int i = len; i > 0; --i) {
            long n = filter.filterLong(src.get());
            if (capacity >= validBits) {
                current |= (n & mask) << (capacity - validBits);
                capacity -= validBits;
                if (capacity == 0) {
                    packBuf[packIndex++] = current;
                    current = 0;
                    capacity = Long.SIZE;
                }
            } else {
                int remain = validBits - capacity;
                current |= (n >> remain) & MASKS[capacity];
                packBuf[packIndex++] = current;
                capacity = Long.SIZE - remain;
                current = (n & MASKS[remain]) << capacity;
            }
        }
        if (capacity < Long.SIZE) {
            packBuf[packIndex++] = current;
        }
        if (packIndex > 0) {
            dst.write(packBuf, 0, packIndex);
        }
    }

    public void compress(
            LongBuffer src,
            LongOutputStream dst,
            LongFilter filter)
    {
        int srclen = src.limit() - src.position();
        while (src.remaining() >= this.blockLen * this.blockNum) {
            compressChunk(src, dst, filter);
        }
        return;
    }

    public void compressChunk(
            LongBuffer src,
            LongOutputStream dst,
            LongFilter filter)
    {
        src.mark();
        filter.saveContext();
        long head = 0;
        for (int i = 0; i < this.blockNum; ++i) {
            long n = this.maxBits[i] = countMaxBits(src, this.blockLen, filter);
            head = (head << 8) | n;
        }
        filter.restoreContext();
        src.reset();

        dst.write(head);
        for (int i = 0; i < this.blockNum; ++i) {
            pack(src, dst, this.maxBits[i], this.blockLen, filter);
        }
    }

    // @Implemnets: LongCodec
    public void compress(LongBuffer src, LongOutputStream dst) {
        compress(src, dst, THROUGH_FILTER);
    }

    public static void unpack(
            LongBuffer src,
            LongOutputStream dst,
            int validBits,
            int len)
    {
        unpack(src, dst, validBits, len, THROUGH_FILTER);
    }

    public static void unpack(
            LongBuffer src,
            LongOutputStream dst,
            int validBits,
            int len,
            LongFilter filter)
    {
        long fetchedData = 0;
        int fetchedBits = 0;
        long mask = MASKS[validBits];
        for (int i = len; i > 0; --i) {
            long n;
            if (fetchedBits < validBits) {
                long n0 = fetchedBits > 0 ?
                    fetchedData << (validBits - fetchedBits) : 0;
                fetchedData = src.get();
                fetchedBits += 64 - validBits;
                n = (n0 | (fetchedData >>> fetchedBits)) & mask;
            } else {
                fetchedBits -= validBits;
                n = (fetchedData >>> fetchedBits) & mask;
            }
            dst.write(filter.filterLong(n));
        }
    }

    public void decompress(
            LongBuffer src,
            LongOutputStream dst,
            LongFilter filter,
            int numOfChunks)
    {
        int[] maxBits = new int[this.blockNum];
        for (int i = numOfChunks; i > 0; --i) {
            long head = src.get();
            for (int j = (this.blockNum - 1) * 8; j >= 0; j -= 8) {
                int validBits = (int)((head >> j) & 0xff);
                unpack(src, dst, validBits, this.blockLen, filter);
            }
        }
        return;
    }

    protected void decompress(
            LongBuffer src,
            LongOutputStream dst,
            LongFilter filter)
    {
        int[] maxBits = new int[this.blockNum];
        while (src.hasRemaining()) {
            long head = src.get();
            for (int i = (this.blockNum - 1) * 8; i >= 0; i -= 8) {
                int validBits = (int)((head >> i) & 0xff);
                unpack(src, dst, validBits, this.blockLen, filter);
            }
        }
        return;
    }

    // @Implemnets: LongCodec
    public void decompress(LongBuffer src, LongOutputStream dst) {
        decompress(src, dst, THROUGH_FILTER);
    }

    public static byte[] toBytes(long[] src) {
        return (new LongBitPacking()).compress(src);
    }

    public static byte[] toBytes(long[] src, int offset, int length) {
        return (new LongBitPacking()).compress(src, offset, length);
    }

    public static long[] fromBytes(byte[] src) {
        return (new LongBitPacking()).decompress(src);
    }

}
