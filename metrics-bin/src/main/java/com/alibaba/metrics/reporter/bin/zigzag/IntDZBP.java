package com.alibaba.metrics.reporter.bin.zigzag;

import com.alibaba.metrics.reporter.bin.zigzag.encodings.DeltaZigzagEncoding;
import com.alibaba.metrics.reporter.bin.zigzag.filters.IntFilter;
import com.alibaba.metrics.reporter.bin.zigzag.filters.IntFilterFactory;
import com.alibaba.metrics.reporter.bin.zigzag.io.IntOutputStream;
import com.alibaba.metrics.reporter.bin.zigzag.packers.IntBitPacking;
import com.alibaba.metrics.reporter.bin.zigzag.utils.CodecUtils;

import java.nio.IntBuffer;

/**
 * Int Delta Zigzag Encoded Bit Packing.
 */
public class IntDZBP extends IntCodec
{
    public static class DZEncodeFilter
        extends DeltaZigzagEncoding.IntEncoder
        implements IntFilter
    {
        private int savedContext = 0;

        public DZEncodeFilter(int contextValue) {
            super(contextValue);
        }

        public DZEncodeFilter() {
            this(0);
        }

        public int filterInt(int value) {
            return encodeInt(value);
        }

        public void saveContext() {
            this.savedContext = this.contextValue;
        }

        public void restoreContext() {
            this.contextValue = this.savedContext;
        }

        public void resetContext() {
            this.contextValue = 0;
            this.savedContext = 0;
        }
    }

    public static class DZEncodeFilterFactory implements IntFilterFactory {
        public IntFilter newFilter(int firstValue) {
            return new DZEncodeFilter(firstValue);
        }
    }

    public static class DZDecodeFilter
        extends DeltaZigzagEncoding.IntDecoder
        implements IntFilter
    {
        private int savedContext = 0;

        public DZDecodeFilter(int contextValue) {
            super(contextValue);
        }

        public DZDecodeFilter() {
            this(0);
        }

        public int filterInt(int value) {
            return decodeInt(value);
        }

        public void saveContext() {
            this.savedContext = this.contextValue;
        }

        public void restoreContext() {
            this.contextValue = this.savedContext;
        }

        public void resetContext() {
            this.contextValue = 0;
            this.savedContext = 0;
        }
    }

    public static class DZDecodeFilterFactory implements IntFilterFactory {
        public IntFilter newFilter(int firstValue) {
            return new DZDecodeFilter(firstValue);
        }
    }

    private final IntBitPacking bitPack;

    private final IntFilterFactory encodeFilterFactory;

    private final IntFilterFactory decodeFilterFactory;

    public IntDZBP setDebug(boolean value) {
        this.bitPack.setDebug(value);
        return this;
    }

    public boolean getDebug() {
        return this.bitPack.getDebug();
    }

    public IntDZBP(IntBitPacking bitPack) {
        this.bitPack = bitPack;
        this.encodeFilterFactory = new DZEncodeFilterFactory();
        this.decodeFilterFactory = new DZDecodeFilterFactory();
    }

    public IntDZBP() {
        this(new IntBitPacking());
    }

    public IntBitPacking getBitPacking() {
        return this.bitPack;
    }

    // @Implemnets: IntCodec
    public void compress(IntBuffer src, IntOutputStream dst) {
        CodecUtils.encodeBlockPack(src, this.encodeFilterFactory,
                this.bitPack, dst);
    }

    // @Implemnets: IntCodec
    public void decompress(IntBuffer src, IntOutputStream dst) {
        CodecUtils.decodeBlockPack(src, this.decodeFilterFactory,
                this.bitPack, dst);
    }

    @Override
    protected int decompressLength(IntBuffer src) {
        src.mark();
        final int outLen = (int)src.get();
        src.reset();
        return outLen;
    }

    public static byte[] toBytes(int[] src) {
        return (new IntDZBP()).compress(src);
    }

    public static int[] fromBytes(byte[] src) {
        return (new IntDZBP()).decompress(src);
    }
}
