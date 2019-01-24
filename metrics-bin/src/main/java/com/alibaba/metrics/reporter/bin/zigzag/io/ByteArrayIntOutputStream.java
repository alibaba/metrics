package com.alibaba.metrics.reporter.bin.zigzag.io;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public final class ByteArrayIntOutputStream extends IntOutputStream
{
    private final ByteArrayOutputStream byteStream;

    private final ByteBuffer byteBuffer;

    private final IntBuffer intBuffer;

    public ByteArrayIntOutputStream(ByteArrayOutputStream s, int bufSize) {
        this.byteStream = s;
        this.byteBuffer = ByteBuffer.allocate(bufSize * 4);
        this.intBuffer = this.byteBuffer.asIntBuffer();
    }

    public ByteArrayIntOutputStream(int size, int bufSize) {
        this(new ByteArrayOutputStream(size), bufSize);
    }

    public ByteArrayIntOutputStream() {
        this(new ByteArrayOutputStream(), 1024);
    }

    public void write(int n) {
        if (this.intBuffer.remaining() == 0) {
            flush();
        }
        this.intBuffer.put(n);
    }

    @Override
    public void write(int[] array) {
        write(array, 0, array.length);
    }

    @Override
    public void write(int[] array, int offset, int length) {
        while (length > 0) {
            int outlen = this.intBuffer.remaining();
            if (outlen == 0) {
                flush();
                outlen = this.intBuffer.remaining();
            }
            if (outlen > length) {
                outlen = length;
            }
            this.intBuffer.put(array, offset, outlen);
            offset += outlen;
            length -= outlen;
        }
    }

    private void flush() {
        this.byteStream.write(this.byteBuffer.array(), 0,
                this.intBuffer.position() * 4);
        this.intBuffer.rewind();
    }

    public byte[] toByteArray() {
        flush();
        return this.byteStream.toByteArray();
    }
}
