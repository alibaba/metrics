package com.alibaba.metrics.reporter.bin;

import java.io.File;
import java.io.IOException;

public abstract class AbstractFileBackend extends AbstractBackend{

    protected final boolean readOnly;
    protected final File file;
   
    protected final static int MAX_FIELD_LENGTH = 4096;
	
	protected AbstractFileBackend(String path, boolean readOnly) {
		super(path);
        this.readOnly = readOnly;
        this.file = new File(path);
	}
	
    public long getLength() {
        return file.length();
    }
    
    public abstract void init() throws IOException;
	
    public abstract void close() throws IOException;
    
    public abstract void setLength(long length) throws IOException;
    
}
