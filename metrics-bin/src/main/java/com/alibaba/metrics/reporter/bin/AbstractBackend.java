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
