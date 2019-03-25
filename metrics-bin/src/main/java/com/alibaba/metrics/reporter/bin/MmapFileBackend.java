/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
