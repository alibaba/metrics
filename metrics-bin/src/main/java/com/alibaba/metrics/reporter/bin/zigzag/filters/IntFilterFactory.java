package com.alibaba.metrics.reporter.bin.zigzag.filters;

public interface IntFilterFactory {
    IntFilter newFilter(int firstValue);
}
