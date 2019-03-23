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
package com.alibaba.metrics.reporter.bin.zigzag.io;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public final class ByteArrayIntOutputStream extends IntOutputStream
{
    private final ByteArrayOutputStream byteStream;

    private final ByteBuffer byteBuffer;

    private final IntBuffer intBuffer;

    public ByteArrayIntOutputStream(ByteArrayOutputStream s, int bufSize) {
        this.byteStream = s;
        this.byteBuffer = ByteBuffer.allocate(bufSize * 4);
        this.intBuffer = this.byteBuffer.asIntBuffer();
    }

    public ByteArrayIntOutputStream(int size, int bufSize) {
        this(new ByteArrayOutputStream(size), bufSize);
    }

    public ByteArrayIntOutputStream() {
        this(new ByteArrayOutputStream(), 1024);
    }

    public void write(int n) {
        if (this.intBuffer.remaining() == 0) {
            flush();
        }
        this.intBuffer.put(n);
    }

    @Override
    public void write(int[] array) {
        write(array, 0, array.length);
    }

    @Override
    public void write(int[] array, int offset, int length) {
        while (length > 0) {
            int outlen = this.intBuffer.remaining();
            if (outlen == 0) {
                flush();
                outlen = this.intBuffer.remaining();
            }
            if (outlen > length) {
                outlen = length;
            }
            this.intBuffer.put(array, offset, outlen);
            offset += outlen;
            length -= outlen;
        }
    }

    private void flush() {
        this.byteStream.write(this.byteBuffer.array(), 0,
                this.intBuffer.position() * 4);
        this.intBuffer.rewind();
    }

    public byte[] toByteArray() {
        flush();
        return this.byteStream.toByteArray();
    }
}
