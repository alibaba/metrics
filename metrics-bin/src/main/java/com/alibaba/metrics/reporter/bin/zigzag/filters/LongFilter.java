package com.alibaba.metrics.reporter.bin.zigzag.filters;

/**
 * Long filter.
 */
public interface LongFilter {

    long filterLong(long value);

    void saveContext();

    void restoreContext();

    void resetContext();

}
