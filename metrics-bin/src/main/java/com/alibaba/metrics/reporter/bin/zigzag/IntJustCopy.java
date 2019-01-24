package com.alibaba.metrics.reporter.bin.zigzag;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.alibaba.metrics.reporter.bin.zigzag.io.IntOutputStream;

public class IntJustCopy extends IntCodec
{
    // @Implemnets: IntCodec
    public void compress(IntBuffer src, IntOutputStream dst) {
        copy(src, dst);
    }

    // @Implemnets: IntCodec
    public void decompress(IntBuffer src, IntOutputStream dst) {
        copy(src, dst);
    }

    private void copy(IntBuffer src, IntOutputStream dst) {
        int[] buf = new int[1024];
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
    public byte[] compress(int[] src) {
        ByteBuffer outbuf = ByteBuffer.allocate(src.length * 4);
        IntBuffer midbuf = outbuf.asIntBuffer();
        midbuf.put(src);
        return outbuf.array();
    }

    @Override
    public int[] decompress(byte[] src) {
        int[] array = new int[src.length / 4];
        IntBuffer outbuf = ByteBuffer.wrap(src).asIntBuffer();
        outbuf.get(array);
        return array;
    }

    public static byte[] toBytes(int[] src) {
        return (new IntJustCopy()).compress(src);
    }

    public static int[] fromBytes(byte[] src) {
        return (new IntJustCopy()).decompress(src);
    }
}
