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
package com.alibaba.metrics.reporter.bin.zigzag;

import com.alibaba.metrics.reporter.bin.zigzag.io.LongOutputStream;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public class LongJustCopy extends LongCodec
{
    // @Implemnets: LongCodec
    public void compress(LongBuffer src, LongOutputStream dst) {
        copy(src, dst);
    }

    // @Implemnets: LongCodec
    public void decompress(LongBuffer src, LongOutputStream dst) {
        copy(src, dst);
    }

    private void copy(LongBuffer src, LongOutputStream dst) {
        long[] buf = new long[1024];
        int len;
        while ((len = src.remaining()) > 0) {
            if (len > buf.length) {
                len = buf.length;
            }
            src.get(buf, 0, len);
            dst.write(buf, 0, len);
        }
    }

    @Override
    public byte[] compress(long[] src) {
        ByteBuffer outbuf = ByteBuffer.allocate(src.length * 8);
        LongBuffer midbuf = outbuf.asLongBuffer();
        midbuf.put(src);
        return outbuf.array();
    }

    @Override
    public long[] decompress(byte[] src) {
        long[] array = new long[src.length / 8];
        LongBuffer outbuf = ByteBuffer.wrap(src).asLongBuffer();
        outbuf.get(array);
        return array;
    }

    public static byte[] toBytes(long[] src) {
        return (new LongJustCopy()).compress(src);
    }

    public static long[] fromBytes(byte[] src) {
        return (new LongJustCopy()).decompress(src);
    }
}
