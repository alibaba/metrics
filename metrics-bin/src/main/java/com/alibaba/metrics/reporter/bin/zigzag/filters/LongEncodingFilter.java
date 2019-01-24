package com.alibaba.metrics.reporter.bin.zigzag.filters;

import com.alibaba.metrics.reporter.bin.zigzag.encodings.LongEncoder;

public class LongEncodingFilter implements LongFilter {

    public static class Factory implements LongFilterFactory {

        private final LongEncoder encoder;

        public Factory(LongEncoder encoder) {
            this.encoder = encoder;
        }

        public LongFilter newFilter(long firstValue) {
            LongEncoder e = getLongEncoder(firstValue);
            return new LongEncodingFilter(e);
        }

        private LongEncoder getLongEncoder(long firstValue) {
            // FIXME: create a new encoder.
            this.encoder.setContextValue(firstValue);
            return this.encoder;
        }
    }

    private final LongEncoder encoder;

    private long savedContext = 0;

    public LongEncodingFilter(LongEncoder encoder) {
        this.encoder = encoder;
    }

    public long filterLong(long value) {
        return this.encoder.encodeLong(value);
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
