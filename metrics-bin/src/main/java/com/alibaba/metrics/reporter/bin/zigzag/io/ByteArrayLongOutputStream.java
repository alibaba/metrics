package com.alibaba.metrics.reporter.bin.zigzag.io;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public final class ByteArrayLongOutputStream extends LongOutputStream
{
    private final ByteArrayOutputStream byteStream;

    private final ByteBuffer byteBuffer;

    private final LongBuffer longBuffer;

    public ByteArrayLongOutputStream(ByteArrayOutputStream s, int bufSize) {
        this.byteStream = s;
        this.byteBuffer = ByteBuffer.allocate(bufSize * 8);
        this.longBuffer = this.byteBuffer.asLongBuffer();
    }

    public ByteArrayLongOutputStream(int size, int bufSize) {
        this(new ByteArrayOutputStream(size), bufSize);
    }

    public ByteArrayLongOutputStream() {
        this(new ByteArrayOutputStream(), 512);
    }

    public void write(long n) {
        if (this.longBuffer.remaining() == 0) {
            flush();
        }
        this.longBuffer.put(n);
    }

    @Override
    public void write(long[] array) {
        write(array, 0, array.length);
    }

    @Override
    public void write(long[] array, int offset, int length) {
        while (length > 0) {
            int outlen = this.longBuffer.remaining();
            if (outlen == 0) {
                flush();
                outlen = this.longBuffer.remaining();
            }
            if (outlen > length) {
                outlen = length;
            }
            this.longBuffer.put(array, offset, outlen);
            offset += outlen;
            length -= outlen;
        }
    }

    private void flush() {
        this.byteStream.write(this.byteBuffer.array(), 0,
                this.longBuffer.position() * 8);
        this.longBuffer.rewind();
    }

    public byte[] toByteArray() {
        flush();
        return this.byteStream.toByteArray();
    }
}
