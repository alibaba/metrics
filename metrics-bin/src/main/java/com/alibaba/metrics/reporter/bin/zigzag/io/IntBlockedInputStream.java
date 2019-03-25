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

import java.nio.IntBuffer;

public abstract class IntBlockedInputStream extends IntInputStream {

    public abstract void fetchBlock(IntOutputStream dst);

    private final IntBuffer buffer;

    private final IntBufferOutputStream stream;

    private int blockLen = 0;

    private int blockIndex = 0;

    protected IntBlockedInputStream(int blockSize) {
        this.buffer = IntBuffer.allocate(blockSize);
        this.buffer.flip();
        this.stream = new IntBufferOutputStream(this.buffer);
    }

    public final Integer read() {
        if (this.buffer.remaining() <= 0) {
            this.buffer.clear();
            fetchBlock(this.stream);
            this.buffer.flip();
            if (this.buffer.remaining() <= 0) {
                return null;
            }
        }
        return Integer.valueOf(this.buffer.get());
    }

    protected final void updateBlock(int[] chunk) {
        updateBlock(chunk, 0, chunk.length);
    }

    protected final void updateBlock(int[] chunk, int off, int len) {
        this.buffer.clear();
        this.buffer.put(chunk, off, len);
        this.buffer.flip();
    }

    // FIXME: implement public int read(int[] array, int offset, int length)
}
