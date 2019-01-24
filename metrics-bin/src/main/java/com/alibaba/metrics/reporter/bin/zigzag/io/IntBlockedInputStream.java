package com.alibaba.metrics.reporter.bin.zigzag.io;

import java.nio.IntBuffer;

public abstract class IntBlockedInputStream extends IntInputStream {

    public abstract void fetchBlock(IntOutputStream dst);

    private final IntBuffer buffer;

    private final IntBufferOutputStream stream;

    private int blockLen = 0;

    private int blockIndex = 0;

    protected IntBlockedInputStream(int blockSize) {
        this.buffer = IntBuffer.allocate(blockSize);
        this.buffer.flip();
        this.stream = new IntBufferOutputStream(this.buffer);
    }

    public final Integer read() {
        if (this.buffer.remaining() <= 0) {
            this.buffer.clear();
            fetchBlock(this.stream);
            this.buffer.flip();
            if (this.buffer.remaining() <= 0) {
                return null;
            }
        }
        return Integer.valueOf(this.buffer.get());
    }

    protected final void updateBlock(int[] chunk) {
        updateBlock(chunk, 0, chunk.length);
    }

    protected final void updateBlock(int[] chunk, int off, int len) {
        this.buffer.clear();
        this.buffer.put(chunk, off, len);
        this.buffer.flip();
    }

    // FIXME: implement public int read(int[] array, int offset, int length)
}
