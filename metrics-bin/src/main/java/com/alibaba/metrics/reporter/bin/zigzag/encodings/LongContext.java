package com.alibaba.metrics.reporter.bin.zigzag.encodings;

/**
 * Context for long value.
 */
public class LongContext {
    protected long contextValue;

    protected LongContext(long contextValue) {
        this.contextValue = contextValue;
    }

    public void setContextValue(long contextValue) {
        this.contextValue = contextValue;
    }

    public long getContextValue() {
        return this.contextValue;
    }
}
