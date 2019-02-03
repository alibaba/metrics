package com.alibaba.metrics.reporter.bin.zigzag;

import com.alibaba.metrics.reporter.bin.zigzag.io.LongOutputStream;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public class LongJustCopy extends LongCodec
{
    // @Implemnets: LongCodec
    public void compress(LongBuffer src, LongOutputStream dst) {
        copy(src, dst);
    }

    // @Implemnets: LongCodec
    public void decompress(LongBuffer src, LongOutputStream dst) {
        copy(src, dst);
    }

    private void copy(LongBuffer src, LongOutputStream dst) {
        long[] buf = new long[1024];
        int len;
        while ((len = src.remaining()) > 0) {
            if (len > buf.length) {
                len = buf.length;
            }
            src.get(buf, 0, len);
            dst.write(buf, 0, len);
        }
    }

    @Override
    public byte[] compress(long[] src) {
        ByteBuffer outbuf = ByteBuffer.allocate(src.length * 8);
        LongBuffer midbuf = outbuf.asLongBuffer();
        midbuf.put(src);
        return outbuf.array();
    }

    @Override
    public long[] decompress(byte[] src) {
        long[] array = new long[src.length / 8];
        LongBuffer outbuf = ByteBuffer.wrap(src).asLongBuffer();
        outbuf.get(array);
        return array;
    }

    public static byte[] toBytes(long[] src) {
        return (new LongJustCopy()).compress(src);
    }

    public static long[] fromBytes(byte[] src) {
        return (new LongJustCopy()).decompress(src);
    }
}
