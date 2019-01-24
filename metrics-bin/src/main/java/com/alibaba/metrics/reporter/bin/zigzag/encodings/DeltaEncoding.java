package com.alibaba.metrics.reporter.bin.zigzag.encodings;

public final class DeltaEncoding {

    public static class IntAscendEncoder extends IntEncoder {
        public IntAscendEncoder(int contextValue) {
            super(contextValue);
        }

        public IntAscendEncoder() {
            this(0);
        }

        public int encodeInt(int value) {
            int n = value - this.contextValue;
            if (n < 0) {
                throw new IllegalArgumentException(
                        String.format(
                            "input:%1$d must be greater than or equals %2$d",
                            value, this.contextValue));
            }
            this.contextValue = value;
            return n;
        }
    }

    public static class IntAscendDecoder extends IntEncoder {
        public IntAscendDecoder(int contextValue) {
            super(contextValue);
        }

        public IntAscendDecoder() {
            this(0);
        }

        public int encodeInt(int value) {
            if (value < 0) {
                throw new IllegalArgumentException(
                        String.format(
                            "input:%1$d must be greater than or equal zero",
                            value));
            }
            return this.contextValue += value;
        }
    }

    /*
    public static class IntDescendEncoder extends IntEncoder {
        public IntDescendEncoder(int contextValue) {
            super(contextValue);
        }

        public IntDescendEncoder() {
            this(0);
        }

        public int encodeInt(int value) {
            int n = this.contextValue - value;
            if (n < 0) {
                throw new IllegalArgumentException(
                        String.format(
                            "input:%1$d must be smaller than or equals %2$d",
                            value, n));
            }
            this.contextValue = value;
            return n;
        }
    }
    */

    public static class LongAscendEncoder extends LongEncoder {
        public LongAscendEncoder(long contextValue) {
            super(contextValue);
        }

        public LongAscendEncoder() {
            this(0);
        }

        public long encodeLong(long value) {
            long n = value - this.contextValue;
            if (n < 0) {
                throw new IllegalArgumentException(
                        String.format(
                            "input:%1$d must be greater than or equals %2$d",
                            value, this.contextValue));
            }
            this.contextValue = value;
            return n;
        }
    }

    public static class LongAscendDecoder extends LongEncoder {
        public LongAscendDecoder(long contextValue) {
            super(contextValue);
        }

        public LongAscendDecoder() {
            this(0);
        }

        public long encodeLong(long value) {
            if (value < 0) {
                throw new IllegalArgumentException(
                        String.format(
                            "input:%1$d must be greater than or equal zero",
                            value));
            }
            return this.contextValue += value;
        }
    }

    /*
    public static class LongDescendEncoder extends LongEncoder {
        public LongDescendEncoder(long contextValue) {
            super(contextValue);
        }

        public LongDescendEncoder() {
            this(0);
        }

        public long dencodeLong(long value) {
            long n = this.contextValue - value;
            if (n < 0) {
                throw new IllegalArgumentException(
                        String.format(
                            "input:%1$d must be smaller than or equals %2$d",
                            value, n));
            }
            this.contextValue = value;
            return n;
        }
    }
    */
}
