package com.alibaba.metrics.reporter.bin.zigzag.io;

import java.nio.LongBuffer;

public abstract class LongBlockedInputStream extends LongInputStream {

    public abstract void fetchBlock(LongOutputStream dst);

    private final LongBuffer buffer;

    private final LongBufferOutputStream stream;

    private int blockLen = 0;

    private int blockIndex = 0;

    protected LongBlockedInputStream(int blockSize) {
        this.buffer = LongBuffer.allocate(blockSize);
        this.buffer.flip();
        this.stream = new LongBufferOutputStream(this.buffer);
    }

    public final Long read() {
        if (this.buffer.remaining() <= 0) {
            this.buffer.clear();
            fetchBlock(this.stream);
            this.buffer.flip();
            if (this.buffer.remaining() <= 0) {
                return null;
            }
        }
        return Long.valueOf(this.buffer.get());
    }

    protected final void updateBlock(long[] chunk) {
        updateBlock(chunk, 0, chunk.length);
    }

    protected final void updateBlock(long[] chunk, int off, int len) {
        this.buffer.clear();
        this.buffer.put(chunk, off, len);
        this.buffer.flip();
    }

    // FIXME: implement public int read(long[] array, int offset, int length)
}
