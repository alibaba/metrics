package com.alibaba.metrics.reporter.bin.zigzag.filters;

import com.alibaba.metrics.reporter.bin.zigzag.encodings.IntEncoder;

public class IntEncodingFilter implements IntFilter {

    public static class Factory implements IntFilterFactory {

        private final IntEncoder encoder;

        public Factory(IntEncoder encoder) {
            this.encoder = encoder;
        }

        public IntFilter newFilter(int firstValue) {
            IntEncoder e = getIntEncoder(firstValue);
            return new IntEncodingFilter(e);
        }

        private IntEncoder getIntEncoder(int firstValue) {
            // FIXME: create a new encoder.
            this.encoder.setContextValue(firstValue);
            return this.encoder;
        }
    }

    private final IntEncoder encoder;

    private int savedContext = 0;

    IntEncodingFilter(IntEncoder encoder) {
        this.encoder = encoder;
    }

    public int filterInt(int value) {
        return this.encoder.encodeInt(value);
    }

    public void saveContext() {
        this.savedContext = this.encoder.getContextValue();
    }

    public void restoreContext() {
        this.encoder.setContextValue(this.savedContext);
    }

    public void resetContext() {
        this.savedContext = 0;
    }
}
