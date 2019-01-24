package com.alibaba.metrics.reporter.bin;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import sun.nio.ch.DirectBuffer;

public class ChannelFileBackend extends RandomAccessFileBackend {

    private final static int BUFFER_SIZE = 8192;

    private FileChannel channel = randomAccessFile.getChannel();
    private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    public ChannelFileBackend(String path, boolean readOnly) throws IOException {
        super(path, readOnly);
    }

    @Override
    public synchronized void read(long offset, byte[] b) throws IOException {

        if (b == null) {
            return;
        }

        byteBuffer.clear();

        int bIndex = 0;
        int remaining = b.length;

        int bytesRead = channel.read(byteBuffer, offset);

        while (bytesRead != -1) {

            int length = 0;
            if (remaining > bytesRead) {
                length = bytesRead;
            } else {
                length = remaining;
            }

            byteBuffer.flip();
            byteBuffer.get(b, bIndex, length);
            bIndex = bIndex + length;
            byteBuffer.clear();
            offset = offset + length;

            remaining = remaining - bytesRead;
            if (remaining <= 0) {
                break;
            }

            bytesRead = channel.read(byteBuffer, offset);

        }
    }

    @Override
    public synchronized void write(long offset, byte[] b, int bytesStart, int length) throws IOException {

        if (b == null) {
            return;
        }

        if (length > b.length - bytesStart) {
            length = b.length - bytesStart;
        }

        int bufferRemaining = byteBuffer.remaining();
        int bytesRemaining = length;

        while (bytesRemaining > 0) {

            if (bufferRemaining < bytesRemaining) {
                byteBuffer.put(b, bytesStart, bufferRemaining);
            } else {
                byteBuffer.put(b, bytesStart, bytesRemaining);
            }

            byteBuffer.flip();

            int writeBytes = channel.write(byteBuffer, offset);
            bytesRemaining = bytesRemaining - writeBytes;

            byteBuffer.clear();
            bufferRemaining = byteBuffer.remaining();
            bytesStart = bytesStart + writeBytes;
            offset = offset + writeBytes;

        }

    }

    @Override
    public synchronized void write(long offset, ByteBuffer b) throws IOException {

    }

    @Override
    public synchronized void read(long offset, ByteBuffer b) throws IOException {

    }

    @Override
    public synchronized void sync() throws IOException {
        channel.force(false);
    }

    public void close() throws IOException {
        channel.close();
        clean();
    }

    private void clean() {
        if (byteBuffer.isDirect()) {
            ((DirectBuffer) byteBuffer).cleaner().clean();
        }
    }
}
