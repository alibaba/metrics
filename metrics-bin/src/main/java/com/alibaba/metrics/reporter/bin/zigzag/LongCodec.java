package com.alibaba.metrics.reporter.bin.zigzag;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.metrics.reporter.bin.zigzag.io.ByteArrayLongOutputStream;
import com.alibaba.metrics.reporter.bin.zigzag.io.LongArrayOutputStream;
import com.alibaba.metrics.reporter.bin.zigzag.io.LongInputStream;
import com.alibaba.metrics.reporter.bin.zigzag.io.LongOutputStream;

/**
 * LongCodec interface.
 *
 * Support compress and decopress methods.
 */
public abstract class LongCodec {

    private static final Logger logger = LoggerFactory.getLogger(LongDZBP.class);
    
    public abstract void compress(LongBuffer src, LongOutputStream dst);

    public abstract void decompress(LongBuffer src, LongOutputStream dst);

    public LongInputStream newCompressStream(LongBuffer src) {
        throw new UnsupportedOperationException();
    }

    public LongInputStream newDecompressStream(LongBuffer src) {
        throw new UnsupportedOperationException();
    }

    protected int decompressLength(LongBuffer src) {
        return -1;
    }

    public byte[] compress(long[] src) {
        ByteArrayLongOutputStream dst = new ByteArrayLongOutputStream();
        compress(LongBuffer.wrap(src), dst);
        return dst.toByteArray();
    }
    
    public byte[] compress(long[] src, int offset, int length) {
        ByteArrayLongOutputStream dst = new ByteArrayLongOutputStream();
        compress(LongBuffer.wrap(src, offset, length), dst);
        return dst.toByteArray();
    }

    public long[] decompress(byte[] src) {
        logger.info(Arrays.toString(src));
        LongBuffer srcBuf = ByteBuffer.wrap(src).asLongBuffer();
        logger.info(Arrays.toString(srcBuf.array()));
        int len = decompressLength(srcBuf);
        LongArrayOutputStream dst = (len < 0)
            ? new LongArrayOutputStream() : new LongArrayOutputStream(len);
        decompress(srcBuf, dst);
        return dst.toLongArray();
    }

    public static int decodeLength(byte[] src) {
        LongBuffer srcBuf = ByteBuffer.wrap(src).asLongBuffer();
        return (int)srcBuf.get();
    }

    public static long decodeFirstValue(byte[] src) {
        LongBuffer srcBuf = ByteBuffer.wrap(src).asLongBuffer();
        srcBuf.get();
        return srcBuf.get();
    }
}
