package com.alibaba.metrics.reporter.bin.zigzag.filters;

public interface LongFilterFactory {
    LongFilter newFilter(long firstValue);
}
