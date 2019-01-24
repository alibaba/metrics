package com.alibaba.metrics.reporter.bin.zigzag.utils;

import java.util.Iterator;

public class IteratorReader<T> {

    private Iterator<T> iter;
    private T value;

    public IteratorReader(Iterator<T> iter) {
        this.iter = iter;
        next();
    }

    public T current() {
        return this.value;
    }

    public T next() {
        return this.value = this.iter.hasNext() ? this.iter.next() : null;
    }

    public boolean hasCurrent() {
        return this.value != null;
    }
}
