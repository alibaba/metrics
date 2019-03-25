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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.SyncFailedException;
import java.nio.ByteBuffer;

public class RandomAccessFileBackend extends AbstractFileBackend{

	protected final RandomAccessFile randomAccessFile;

    public RandomAccessFileBackend(String path, boolean readOnly) throws IOException {
        super(path, readOnly);
        this.randomAccessFile = new RandomAccessFile(path, readOnly ? "r" : "rw");
    }

    @Override
	public void init() throws IOException {

	}

	public void close() throws IOException {
		randomAccessFile.close();
	}

	@Override
	public synchronized void read(long offset, byte[] b) throws IOException {
		if (b == null){
			return;
		}
		randomAccessFile.read(b, (int)offset, b.length);
	}

	@Override
	public synchronized void write(long offset, byte[] b, int bytesStart, int length) throws IOException {
		if (b == null){
			return;
		}
		randomAccessFile.seek(offset);
		randomAccessFile.write(b, bytesStart, b.length);
	}

	public synchronized void setLength(long length) throws IOException {

		if (length > Integer.MAX_VALUE){
			throw new ArrayIndexOutOfBoundsException("length above max integer");
		}

		randomAccessFile.setLength(length);
	}

	@Override
	public synchronized void sync() throws IOException {
		try {
			randomAccessFile.getFD().sync();
		} catch (SyncFailedException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}

    @Override
    public void read(long offset, ByteBuffer b) throws IOException {

    }

    @Override
    public void write(long offset, ByteBuffer b) throws IOException {

    }

}
