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
package com.alibaba.metrics.reporter.bin.zigzag.encodings;

public abstract class IntEncoder extends IntContext {

    protected IntEncoder(int contextValue) {
        super(contextValue);
    }

    public abstract int encodeInt(int value);

    public int[] encodeArray(int[] src, int srcoff, int length,
            int[] dst, int dstoff)
    {
        for (int i = 0; i < length; ++i) {
            dst[dstoff + i] = encodeInt(src[srcoff + i]);
        }
        return dst;
    }

    public int[] encodeArray(int[] src) {
        return encodeArray(src, 0, src.length, new int[src.length], 0);
    }
}
