package com.alibaba.metrics.reporter.bin.zigzag.utils;

import com.alibaba.metrics.reporter.bin.zigzag.filters.IntFilter;
import com.alibaba.metrics.reporter.bin.zigzag.filters.IntFilterFactory;
import com.alibaba.metrics.reporter.bin.zigzag.filters.LongFilter;
import com.alibaba.metrics.reporter.bin.zigzag.filters.LongFilterFactory;
import com.alibaba.metrics.reporter.bin.zigzag.io.IntBufferOutputStream;
import com.alibaba.metrics.reporter.bin.zigzag.io.IntOutputStream;
import com.alibaba.metrics.reporter.bin.zigzag.io.LongBufferOutputStream;
import com.alibaba.metrics.reporter.bin.zigzag.io.LongOutputStream;
import com.alibaba.metrics.reporter.bin.zigzag.packers.IntBitPacking;
import com.alibaba.metrics.reporter.bin.zigzag.packers.LongBitPacking;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;

public final class CodecUtils {

    public static void encodeBlockPack(
            IntBuffer src,
            IntFilterFactory filterFactory,
            IntBitPacking packer,
            IntOutputStream dst)
    {
        // Output length of original array.  When input array is empty, make
        // empty output for memory efficiency.
        final int srcLen = src.remaining();
        if (srcLen == 0) {
            return;
        }
        dst.write(srcLen);

        // Output first int, and set it as delta's initial context.
        final int first = src.get();
        dst.write(first);
        IntFilter filter = filterFactory.newFilter(first);

        // Compress intermediate blocks.
        final int chunkSize = packer.getBlockSize();
        final int chunkRemain = src.remaining() % chunkSize;
        IntBuffer window = src.slice();
        window.limit(window.limit() - chunkRemain);
        packer.compress(window, dst, filter);
        src.position(src.position() + window.position());

        // Compress last block.
        if (chunkRemain > 0) {
            int[] last = new int[chunkSize];
            src.get(last, 0, chunkRemain);
            // Padding extended area by last value.  It make delta zigzag
            // efficient.
            Arrays.fill(last, chunkRemain, last.length,
                    last[chunkRemain - 1]);
            packer.compress(IntBuffer.wrap(last), dst, filter);
        }
    }

    public static void encodeBlockPack(
            LongBuffer src,
            LongFilterFactory filterFactory,
            LongBitPacking packer,
            LongOutputStream dst)
    {
        // Output length of original array.  When input array is empty, make
        // empty output for memory efficiency.
        final int srcLen = src.remaining();
        if (srcLen == 0) {
            return;
        }
        dst.write(srcLen);

        // Output first int, and set it as delta's initial context.
        final long first = src.get();
        dst.write(first);
        LongFilter filter = filterFactory.newFilter(first);

        // Compress intermediate blocks.
        final int chunkSize = packer.getBlockSize();
        final int chunkRemain = src.remaining() % chunkSize;
        LongBuffer window = src.slice();
        window.limit(window.limit() - chunkRemain);
        packer.compress(window, dst, filter);
        src.position(src.position() + window.position());

        // Compress last block.
        if (chunkRemain > 0) {
            long[] last = new long[chunkSize];
            src.get(last, 0, chunkRemain);
            // Padding extended area by last value.  It make delta zigzag
            // efficient.
            Arrays.fill(last, chunkRemain, last.length,
                    last[chunkRemain - 1]);
            packer.compress(LongBuffer.wrap(last), dst, filter);
        }
    }

    public static void decodeBlockPack(
            IntBuffer src,
            IntFilterFactory filterFactory,
            IntBitPacking packer,
            IntOutputStream dst)
    {
        // Fetch length of original array.
        if (!src.hasRemaining()) {
            return;
        }
        final int outLen = (int)src.get() - 1;

        // Fetch and output first int, and set it as delta's initial context.
        final int first = src.get();
        dst.write(first);
        IntFilter filter = filterFactory.newFilter(first);

        // Decompress intermediate blocks.
        final int chunkSize = packer.getBlockSize();
        final int chunkNum = outLen / chunkSize;
        if (chunkNum > 0) {
            packer.decompress(src, dst, filter, chunkNum);
        }

        // Decompress last block.
        final int chunkRemain = outLen % chunkSize;
        if (chunkRemain > 0) {
            int[] last = new int[chunkSize];
            IntBuffer buf = IntBuffer.wrap(last);
            packer.decompress(src, new IntBufferOutputStream(buf),
                    filter, 1);
            dst.write(last, 0, chunkRemain);
        }
    }

    public static void decodeBlockPack(
            LongBuffer src,
            LongFilterFactory filterFactory,
            LongBitPacking packer,
            LongOutputStream dst)
    {
        // Fetch length of original array.
        if (!src.hasRemaining()) {
            return;
        }
        final int outLen = (int)src.get() - 1;

        // Fetch and output first int, and set it as delta's initial context.
        final long first = src.get();
        dst.write(first);
        LongFilter filter = filterFactory.newFilter(first);

        // Decompress intermediate blocks.
        final int chunkSize = packer.getBlockSize();
        final int chunkNum = outLen / chunkSize;
        if (chunkNum > 0) {
            packer.decompress(src, dst, filter, chunkNum);
        }

        // Decompress last block.
        final int chunkRemain = outLen % chunkSize;
        if (chunkRemain > 0) {
            long[] last = new long[chunkSize];
            LongBuffer buf = LongBuffer.wrap(last);
            packer.decompress(src, new LongBufferOutputStream(buf),
                    filter, 1);
            dst.write(last, 0, chunkRemain);
        }
    }

}
