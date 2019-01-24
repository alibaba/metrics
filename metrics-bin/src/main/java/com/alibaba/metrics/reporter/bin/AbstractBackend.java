package com.alibaba.metrics.reporter.bin;


import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class AbstractBackend{
	
    private static boolean instanceCreated = false;
    private final String path;

    protected AbstractBackend(String path) {
        this.path = path;
        instanceCreated = true;
    }
    
    public String getPath() {
        return path;
    }
    
    public abstract void read(long offset, byte[] b) throws IOException;

    public abstract void write(long offset, byte[] b, int bytesStart, int length) throws IOException;
    
    public abstract void read(long offset, ByteBuffer b) throws IOException;
    
    public abstract void write(long offset, ByteBuffer b) throws IOException;
    
    public abstract long getLength() throws IOException;

    protected abstract void setLength(long length) throws IOException;
    
    protected abstract void close() throws Exception;
    
    public abstract void sync() throws IOException;
    
    public final byte[] readAll() throws IOException {
        byte[] b = new byte[(int) getLength()];
        read(0, b);
        return b;
    }
    
    public void write(long offset, byte[] b) throws IOException{
    	write(offset, b, 0, b.length);
    }
    
	static boolean isInstanceCreated() {
		return instanceCreated;
	}
	
	

}
