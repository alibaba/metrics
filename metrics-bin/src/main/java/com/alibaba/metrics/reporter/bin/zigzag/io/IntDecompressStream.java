package com.alibaba.metrics.reporter.bin.zigzag.io;

import com.alibaba.metrics.reporter.bin.zigzag.filters.IntFilter;
import com.alibaba.metrics.reporter.bin.zigzag.filters.IntFilterFactory;
import com.alibaba.metrics.reporter.bin.zigzag.packers.IntBitPacking;

import java.nio.BufferUnderflowException;
import java.nio.IntBuffer;

public class IntDecompressStream extends IntBlockedInputStream {

    private final IntBuffer source;

    private final IntFilter filter;

    private final IntBitPacking packer;

    private final int chunkSize;

    private int availableLen = 0;

    public IntDecompressStream(
            IntBuffer source,
            IntFilterFactory factory,
            IntBitPacking packer)
    {
        super(packer.getBlockSize());
        this.source = source;
        int outLen = this.source.remaining();
        if (outLen == 0) {
            this.filter = factory.newFilter(0);
        } else {
            this.availableLen = this.source.get() - 1;
            int first = this.source.get();
            updateBlock(new int[]{ first });
            this.filter = factory.newFilter(first);
        }
        this.packer = packer;
        this.chunkSize = this.packer.getBlockSize();
    }

    public void fetchBlock(IntOutputStream dst) {
        int remain = this.source.remaining();
        if (this.availableLen >= this.chunkSize) {
            try {
                this.packer.decompress(this.source, dst, this.filter, 1);
                this.availableLen -= this.chunkSize;
            } catch (BufferUnderflowException e) {
                // FIXME: adjust availableLen.
            }
        } else if (this.availableLen > 0) {
            int[] last = new int[this.chunkSize];
            IntBuffer buf = IntBuffer.wrap(last);
            try {
                packer.decompress(this.source, new IntBufferOutputStream(buf),
                        filter, 1);
                dst.write(last, 0, this.availableLen);
                this.availableLen = 0;
            } catch (BufferUnderflowException e) {
                // FIXME: adjust availableLen.
            }
        }
    }
}
