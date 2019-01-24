package com.alibaba.metrics.reporter.file;

import java.io.IOException;

public interface FileAppender {

    public void append(String message) throws IOException;

    public void append(byte[] data) throws IOException;

    public void flush() throws IOException;
}
