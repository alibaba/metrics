package com.alibaba.metrics.reporter.bin;

import java.io.IOException;

public abstract class AbstractMetaVariable {

	protected AbstractBackend backend;

    protected long offset;

	public abstract void write() throws IOException;

	public abstract void read() throws IOException;

}
