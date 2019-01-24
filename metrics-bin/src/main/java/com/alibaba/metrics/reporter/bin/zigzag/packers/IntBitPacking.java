package com.alibaba.metrics.reporter.bin.zigzag.packers;

import static com.alibaba.metrics.reporter.bin.zigzag.utils.IntBitPackingPacks.*;
import static com.alibaba.metrics.reporter.bin.zigzag.utils.IntBitPackingUnpacks.*;

import java.nio.IntBuffer;
import java.util.Arrays;

import com.alibaba.metrics.reporter.bin.zigzag.IntCodec;
import com.alibaba.metrics.reporter.bin.zigzag.filters.IntFilter;
import com.alibaba.metrics.reporter.bin.zigzag.filters.ThroughIntFilter;
import com.alibaba.metrics.reporter.bin.zigzag.io.IntOutputStream;

public class IntBitPacking extends IntCodec
{
    public static final int BLOCK_LEN = 32;
    public static final int BLOCK_NUM = 4;

    private static final int[] MASKS = newMasks();

    private static final IntFilter THROUGH_FILTER = new ThroughIntFilter();

    private boolean debug = false;

    private final int blockLen;

    private final int blockNum;

    private final int[] maxBits;

    private final int[] packBuf;

    private final int[] unpackBuf;

    public IntBitPacking(int blockLen, int blockNum) {
        this.blockLen = blockLen;
        this.blockNum = blockNum;
        this.maxBits = new int[blockNum];
        this.packBuf = new int[blockLen];
        this.unpackBuf = new int[blockLen];
    }

    public IntBitPacking() {
        this(BLOCK_LEN, BLOCK_NUM);
    }

    public IntBitPacking setDebug(boolean value) {
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

    public static int[] newMasks() {
        int[] masks = new int[Integer.SIZE + 1];
        int m = 0xffffffff;
        for (int i = Integer.SIZE; i >= 0; --i) {
            masks[i] = m;
            m >>>= 1;
        }
        return masks;
    }

    public static int countMaxBits(
            IntBuffer buf,
            int len,
            IntFilter filter)
    {
        int n = 0;
        for (int i = len; i > 0; --i) {
            n |= filter.filterInt(buf.get());
        }
        return Integer.SIZE - Integer.numberOfLeadingZeros(n);
    }

    public static int countMaxBits(IntBuffer buf, int len) {
        return countMaxBits(buf, len, THROUGH_FILTER);
    }

    public void pack(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len)
    {
        pack(src, dst, validBits, len, THROUGH_FILTER);
    }

    public void pack(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len,
            IntFilter filter)
    {
        switch (validBits) {
            case 0: pack0(src, dst, len); break;
            case 1: pack1(this.packBuf, src, dst, filter); break;
            case 2: pack2(this.packBuf, src, dst, filter); break;
            case 3: pack3(this.packBuf, src, dst, filter); break;
            case 4: pack4(this.packBuf, src, dst, filter); break;
            case 5: pack5(this.packBuf, src, dst, filter); break;
            case 6: pack6(this.packBuf, src, dst, filter); break;
            case 7: pack7(this.packBuf, src, dst, filter); break;
            case 8: pack8(this.packBuf, src, dst, filter); break;
            case 9: pack9(this.packBuf, src, dst, filter); break;
            case 10: pack10(this.packBuf, src, dst, filter); break;
            case 11: pack11(this.packBuf, src, dst, filter); break;
            case 12: pack12(this.packBuf, src, dst, filter); break;
            case 13: pack13(this.packBuf, src, dst, filter); break;
            case 14: pack14(this.packBuf, src, dst, filter); break;
            case 15: pack15(this.packBuf, src, dst, filter); break;
            case 16: pack16(this.packBuf, src, dst, filter); break;
            case 17: pack17(this.packBuf, src, dst, filter); break;
            case 18: pack18(this.packBuf, src, dst, filter); break;
            case 19: pack19(this.packBuf, src, dst, filter); break;
            case 20: pack20(this.packBuf, src, dst, filter); break;
            case 21: pack21(this.packBuf, src, dst, filter); break;
            case 22: pack22(this.packBuf, src, dst, filter); break;
            case 23: pack23(this.packBuf, src, dst, filter); break;
            case 24: pack24(this.packBuf, src, dst, filter); break;
            case 25: pack25(this.packBuf, src, dst, filter); break;
            case 26: pack26(this.packBuf, src, dst, filter); break;
            case 27: pack27(this.packBuf, src, dst, filter); break;
            case 28: pack28(this.packBuf, src, dst, filter); break;
            case 29: pack29(this.packBuf, src, dst, filter); break;
            case 30: pack30(this.packBuf, src, dst, filter); break;
            case 31: pack31(this.packBuf, src, dst, filter); break;
            case 32: pack32(this.packBuf, src, dst, filter); break;
            default:
                throw new RuntimeException("Invalid bits: " + validBits);
        }
    }

    public void pack0(
            IntBuffer src,
            IntOutputStream dst,
            int len)
    {
        // FIXME: update filter state by filterInt().
        src.position(src.position() + len);
    }

    public void packAny(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len)
    {
        packAny(src, dst, validBits, len, THROUGH_FILTER);
    }

    public void packAny(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len,
            IntFilter filter)
    {
        int current = 0;
        int capacity = Integer.SIZE;
        int mask = MASKS[validBits];
        int packIndex = 0;
        for (int i = len; i > 0; --i) {
            int n = filter.filterInt(src.get());
            if (capacity >= validBits) {
                current |= (n & mask) << (capacity - validBits);
                capacity -= validBits;
                if (capacity == 0) {
                    this.packBuf[packIndex++] = current;
                    current = 0;
                    capacity = Integer.SIZE;
                }
            } else {
                int remain = validBits - capacity;
                current |= (n >> remain) & MASKS[capacity];
                this.packBuf[packIndex++] = current;
                capacity = Integer.SIZE - remain;
                current = (n & MASKS[remain]) << capacity;
            }
        }
        if (capacity < Integer.SIZE) {
            this.packBuf[packIndex++] = current;
        }
        if (packIndex > 0) {
            dst.write(this.packBuf, 0, packIndex);
        }
    }

    public void compress(
            IntBuffer src,
            IntOutputStream dst, 
            IntFilter filter)
    {
        while (src.remaining() >= this.blockLen * this.blockNum) {
            compressChunk(src, dst, filter);
        }
        return;
    }

    public void compressChunk(
            IntBuffer src,
            IntOutputStream dst, 
            IntFilter filter)
    {
        src.mark();
        filter.saveContext();
        int head = 0;
        for (int i = 0; i < this.blockNum; ++i) {
            int n = this.maxBits[i] = countMaxBits(src, this.blockLen, filter);
            head = (head << 8) | n;
        }
        filter.restoreContext();
        src.reset();

        dst.write(head);
        for (int i = 0; i < this.blockNum; ++i) {
            pack(src, dst, this.maxBits[i], this.blockLen, filter);
        }
    }

    // @Implemnets: IntCodec
    public void compress(IntBuffer src, IntOutputStream dst) {
        compress(src, dst, THROUGH_FILTER);
    }

    public void unpack(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len)
    {
        unpack(src, dst, validBits, len, THROUGH_FILTER);
    }

    public void unpack(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len,
            IntFilter filter)
    {
        switch (validBits) {
            case 0: unpack0(dst, filter); break;
            case 1: unpack1(this.unpackBuf, src, dst, filter); break;
            case 2: unpack2(this.unpackBuf, src, dst, filter); break;
            case 3: unpack3(this.unpackBuf, src, dst, filter); break;
            case 4: unpack4(this.unpackBuf, src, dst, filter); break;
            case 5: unpack5(this.unpackBuf, src, dst, filter); break;
            case 6: unpack6(this.unpackBuf, src, dst, filter); break;
            case 7: unpack7(this.unpackBuf, src, dst, filter); break;
            case 8: unpack8(this.unpackBuf, src, dst, filter); break;
            case 9: unpack9(this.unpackBuf, src, dst, filter); break;
            case 10: unpack10(this.unpackBuf, src, dst, filter); break;
            case 11: unpack11(this.unpackBuf, src, dst, filter); break;
            case 12: unpack12(this.unpackBuf, src, dst, filter); break;
            case 13: unpack13(this.unpackBuf, src, dst, filter); break;
            case 14: unpack14(this.unpackBuf, src, dst, filter); break;
            case 15: unpack15(this.unpackBuf, src, dst, filter); break;
            case 16: unpack16(this.unpackBuf, src, dst, filter); break;
            case 17: unpack17(this.unpackBuf, src, dst, filter); break;
            case 18: unpack18(this.unpackBuf, src, dst, filter); break;
            case 19: unpack19(this.unpackBuf, src, dst, filter); break;
            case 20: unpack20(this.unpackBuf, src, dst, filter); break;
            case 21: unpack21(this.unpackBuf, src, dst, filter); break;
            case 22: unpack22(this.unpackBuf, src, dst, filter); break;
            case 23: unpack23(this.unpackBuf, src, dst, filter); break;
            case 24: unpack24(this.unpackBuf, src, dst, filter); break;
            case 25: unpack25(this.unpackBuf, src, dst, filter); break;
            case 26: unpack26(this.unpackBuf, src, dst, filter); break;
            case 27: unpack27(this.unpackBuf, src, dst, filter); break;
            case 28: unpack28(this.unpackBuf, src, dst, filter); break;
            case 29: unpack29(this.unpackBuf, src, dst, filter); break;
            case 30: unpack30(this.unpackBuf, src, dst, filter); break;
            case 31: unpack31(this.unpackBuf, src, dst, filter); break;
            case 32: unpack32(this.unpackBuf, src, dst, filter); break;
            default:
                throw new RuntimeException("Invalid bits: " + validBits);
        }
    }

    private void unpack0(IntOutputStream dst, IntFilter filter) {
        for (int i = 0; i < this.blockLen; ++i) {
            this.unpackBuf[i] = filter.filterInt(0);
        }
        dst.write(this.unpackBuf);
    }

    public void unpackAny(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len)
    {
        unpackAny(src, dst, validBits, len, THROUGH_FILTER);
    }

    public void unpackAny(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len,
            IntFilter filter)
    {
        int fetchedData = 0;
        int fetchedBits = 0;
        int mask = MASKS[validBits];
        for (int i = 0; i < len; ++i) {
            int n;
            if (fetchedBits < validBits) {
                int n0 = fetchedBits > 0 ?
                    fetchedData << (validBits - fetchedBits) : 0;
                fetchedData = src.get();
                fetchedBits += Integer.SIZE - validBits;
                n = (n0 | (fetchedData >>> fetchedBits)) & mask;
            } else {
                fetchedBits -= validBits;
                n = (fetchedData >>> fetchedBits) & mask;
            }
            this.unpackBuf[i] = filter.filterInt(n);
        }
        dst.write(this.unpackBuf, 0, len);
    }

    public void decompress(
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter,
            int numOfChunks)
    {
        for (int i = numOfChunks; i > 0; --i) {
            int head = src.get();
            for (int j = (this.blockNum - 1) * 8; j >= 0; j -= 8) {
                int validBits = (int)((head >> j) & 0xff);
                unpack(src, dst, validBits, this.blockLen, filter);
            }
        }
        return;
    }

    protected void decompress(
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        while (src.hasRemaining()) {
            int head = src.get();
            for (int i = (this.blockNum - 1) * 8; i >= 0; i -= 8) {
                int validBits = (int)((head >> i) & 0xff);
                unpack(src, dst, validBits, this.blockLen, filter);
            }
        }
        return;
    }

    // @Implemnets: IntCodec
    public void decompress(IntBuffer src, IntOutputStream dst) {
        decompress(src, dst, THROUGH_FILTER);
    }

    public static byte[] toBytes(int[] src) {
        return (new IntBitPacking()).compress(src);
    }

    public static int[] fromBytes(byte[] src) {
        return (new IntBitPacking()).decompress(src);
    }
}
