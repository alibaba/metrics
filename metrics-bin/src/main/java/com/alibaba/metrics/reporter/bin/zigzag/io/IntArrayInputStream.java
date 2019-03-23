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

public class IntArrayInputStream extends IntInputStream
{
    protected final IntBuffer buffer;

    public IntArrayInputStream(int[] array, int off, int len) {
        this.buffer = IntBuffer.wrap(array, off, len);
    }

    public IntArrayInputStream(int[] array) {
        this(array, 0, array.length);
    }

    public Integer read() {
        return this.buffer.hasRemaining()
            ? Integer.valueOf(this.buffer.get()) : null;
    }
}
