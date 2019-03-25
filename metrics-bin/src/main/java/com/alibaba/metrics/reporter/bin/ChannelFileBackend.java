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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.nio.ch.DirectBuffer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class ChannelFileBackend extends RandomAccessFileBackend {

    private final static Logger logger = LoggerFactory.getLogger(ChannelFileBackend.class);

    private final static int BUFFER_SIZE = 8192;

    /**
     * This method is only available since Java 9.
     * We use sun.misc.Unsafe#invokeCleaner method to test if we are running on Java 9+ or not.
     * The implementation is inspired by:
     * https://github.com/apache/lucene-solr/blob/master/lucene/core/src/java/org/apache/lucene/store/MMapDirectory.java#L338-L396
     */
    private static Method invokeCleaner;

    /**
     * This is the reference to sun.misc.Unsafe##getUnsafe() object
     */
    private static Object theUnsafeObject;

    static {
        try {
            final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            invokeCleaner = unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);
            Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            theUnsafeObject = theUnsafe.get(null);
        } catch (Throwable throwable) {
            // ignore
        }
    }

    private FileChannel channel = randomAccessFile.getChannel();
    private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    public ChannelFileBackend(String path, boolean readOnly) throws IOException {
        super(path, readOnly);
    }

    @Override
    public synchronized void read(long offset, byte[] b) throws IOException {

        if (b == null) {
            return;
        }

        byteBuffer.clear();

        int bIndex = 0;
        int remaining = b.length;

        int bytesRead = channel.read(byteBuffer, offset);

        while (bytesRead != -1) {

            int length = 0;
            if (remaining > bytesRead) {
                length = bytesRead;
            } else {
                length = remaining;
            }

            byteBuffer.flip();
            byteBuffer.get(b, bIndex, length);
            bIndex = bIndex + length;
            byteBuffer.clear();
            offset = offset + length;

            remaining = remaining - bytesRead;
            if (remaining <= 0) {
                break;
            }

            bytesRead = channel.read(byteBuffer, offset);

        }
    }

    @Override
    public synchronized void write(long offset, byte[] b, int bytesStart, int length) throws IOException {

        if (b == null) {
            return;
        }

        if (length > b.length - bytesStart) {
            length = b.length - bytesStart;
        }

        int bufferRemaining = byteBuffer.remaining();
        int bytesRemaining = length;

        while (bytesRemaining > 0) {

            if (bufferRemaining < bytesRemaining) {
                byteBuffer.put(b, bytesStart, bufferRemaining);
            } else {
                byteBuffer.put(b, bytesStart, bytesRemaining);
            }

            byteBuffer.flip();

            int writeBytes = channel.write(byteBuffer, offset);
            bytesRemaining = bytesRemaining - writeBytes;

            byteBuffer.clear();
            bufferRemaining = byteBuffer.remaining();
            bytesStart = bytesStart + writeBytes;
            offset = offset + writeBytes;

        }

    }

    @Override
    public synchronized void write(long offset, ByteBuffer b) throws IOException {

    }

    @Override
    public synchronized void read(long offset, ByteBuffer b) throws IOException {

    }

    @Override
    public synchronized void sync() throws IOException {
        channel.force(false);
    }

    public void close() throws IOException {
        channel.close();
        clean();
    }

    private void clean() {
        if (!byteBuffer.isDirect()) {
            return;
        }
        if (invokeCleaner != null && theUnsafeObject != null) {
            // we should be running on Java 9 or above
            try {
                invokeCleaner.invoke(theUnsafeObject, byteBuffer);
            } catch (Throwable e) {
                logger.error("Error during clean direct byte buffer: ", e);
            }
        } else {
            // this should work on Java 8 or below
            ((DirectBuffer) byteBuffer).cleaner().clean();
        }
    }
}
