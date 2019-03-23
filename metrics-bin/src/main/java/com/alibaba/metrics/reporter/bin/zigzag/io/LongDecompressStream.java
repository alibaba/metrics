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

import com.alibaba.metrics.reporter.bin.zigzag.filters.LongFilter;
import com.alibaba.metrics.reporter.bin.zigzag.filters.LongFilterFactory;
import com.alibaba.metrics.reporter.bin.zigzag.packers.LongBitPacking;

import java.nio.BufferUnderflowException;
import java.nio.LongBuffer;

public class LongDecompressStream extends LongBlockedInputStream {

    private final LongBuffer source;

    private final LongFilter filter;

    private final LongBitPacking packer;

    private final int chunkSize;

    private int availableLen = 0;

    public LongDecompressStream(
            LongBuffer source,
            LongFilterFactory factory,
            LongBitPacking packer)
    {
        super(packer.getBlockSize());
        this.source = source;
        int outLen = this.source.remaining();
        if (outLen == 0) {
            this.filter = factory.newFilter(0);
        } else {
            this.availableLen = (int)this.source.get() - 1;
            long first = this.source.get();
            updateBlock(new long[]{ first });
            this.filter = factory.newFilter(first);
        }
        this.packer = packer;
        this.chunkSize = this.packer.getBlockSize();
    }

    public void fetchBlock(LongOutputStream dst) {
        if (this.availableLen >= this.chunkSize) {
            try {
                this.packer.decompress(this.source, dst, this.filter, 1);
                this.availableLen -= this.chunkSize;
            } catch (BufferUnderflowException e) {
                // FIXME: adjust availableLen.
            }
        } else if (this.availableLen > 0) {
            long[] last = new long[this.chunkSize];
            LongBuffer buf = LongBuffer.wrap(last);
            try {
                packer.decompress(this.source, new LongBufferOutputStream(buf),
                        filter, 1);
                dst.write(last, 0, this.availableLen);
                this.availableLen = 0;
            } catch (BufferUnderflowException e) {
                // FIXME: adjust availableLen.
            }
        }
    }
}
