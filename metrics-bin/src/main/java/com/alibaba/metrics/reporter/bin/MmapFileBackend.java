package com.alibaba.metrics.reporter.bin;

import sun.nio.ch.DirectBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MmapFileBackend extends RandomAccessFileBackend {

	private MappedByteBuffer byteBuffer;
	private int size;

	protected MmapFileBackend(String path, boolean readOnly, int length)
			throws IOException {
		super(path, readOnly);
		this.size = (int) getLength();
	}

	@Override
	public void init() throws IOException{
		try {
			mapFile(size);
		} catch (IOException ioe) {
			throw ioe;
		} catch (RuntimeException rte) {
			throw rte;
		}
	}

	protected MmapFileBackend(String path, boolean readOnly)
			throws IOException {
		super(path, readOnly);
	}

	public void mapFile() throws IOException {
		int length = (int) getLength();
		mapFile(length);
	}

	public void mapFile(int length) throws IOException {
		if (length > 0) {
			FileChannel.MapMode mapMode = readOnly ? FileChannel.MapMode.READ_ONLY : FileChannel.MapMode.READ_WRITE;
			byteBuffer = randomAccessFile.getChannel().map(mapMode, 0, length);
		}
	}


	private void unmapFile() {
		if (byteBuffer != null) {
			if (byteBuffer instanceof DirectBuffer) {
				((DirectBuffer) byteBuffer).cleaner().clean();
			}
			byteBuffer = null;
		}
	}

	public synchronized void sync() {
		if (byteBuffer != null) {
			byteBuffer.force();
		}
	}

	@Override
    public synchronized void write(long offset, byte[] b, int bytesStart, int length) throws IOException {
        if (byteBuffer != null) {
            byteBuffer.position((int) offset);
            byteBuffer.put(b, bytesStart, length);
        }else {
            throw new IOException("Write failed, file " + getPath() + " not mapped for I/O");
        }
    }

	@Override
    public synchronized void read(long offset, byte[] b) throws IOException {
        if (byteBuffer != null) {
            byteBuffer.position((int) offset);
            byteBuffer.get(b);
        }else {
            throw new IOException("Read failed, file " + getPath() + " not mapped for I/O");
        }
    }

	/**
	 * 设置文件的大小，磁盘上文件的大小在这里会改变
	 *
	 * @param newLength
	 * @return
	 */

	@Override
	public synchronized void setLength(long newLength) throws IOException {
		unmapFile();
		super.setLength(newLength);
		mapFile();
	}

	@Override
	public synchronized void close() throws IOException {
		try {
			if (!readOnly) {
				sync();
			}
			unmapFile();
		} finally {
			super.close();
		}
	}

}
