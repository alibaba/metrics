package com.alibaba.metrics.reporter.bin.zigzag.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ReaderIterator<E, R extends Reader<E>> implements Iterator<E> {
    final Reader<E> reader;
    E next = null;

    public ReaderIterator(Reader<E> r) {
        this.reader = r;
    }

    private boolean readNext() {
        if (this.next != null) {
            return true;
        }
        this.next = this.reader.read();
        return this.next != null;
    }

    public E next() {
        if (!readNext()) {
            throw new NoSuchElementException();
        }
        E retval = this.next;
        this.next = null;
        return retval;
    }

    public boolean hasNext() {
        return readNext();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
