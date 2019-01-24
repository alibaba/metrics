package com.alibaba.metrics.reporter.bin.zigzag.encodings;

/**
 * Context for int value.
 */
public class IntContext {

    protected int contextValue;

    protected IntContext(int contextValue) {
        this.contextValue = contextValue;
    }

    public void setContextValue(int contextValue) {
        this.contextValue = contextValue;
    }

    public int getContextValue() {
        return this.contextValue;
    }
}
