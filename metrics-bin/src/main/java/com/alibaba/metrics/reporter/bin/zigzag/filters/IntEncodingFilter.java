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
package com.alibaba.metrics.reporter.bin.zigzag.filters;

import com.alibaba.metrics.reporter.bin.zigzag.encodings.IntEncoder;

public class IntEncodingFilter implements IntFilter {

    public static class Factory implements IntFilterFactory {

        private final IntEncoder encoder;

        public Factory(IntEncoder encoder) {
            this.encoder = encoder;
        }

        public IntFilter newFilter(int firstValue) {
            IntEncoder e = getIntEncoder(firstValue);
            return new IntEncodingFilter(e);
        }

        private IntEncoder getIntEncoder(int firstValue) {
            // FIXME: create a new encoder.
            this.encoder.setContextValue(firstValue);
            return this.encoder;
        }
    }

    private final IntEncoder encoder;

    private int savedContext = 0;

    IntEncodingFilter(IntEncoder encoder) {
        this.encoder = encoder;
    }

    public int filterInt(int value) {
        return this.encoder.encodeInt(value);
    }

    public void saveContext() {
        this.savedContext = this.encoder.getContextValue();
    }

    public void restoreContext() {
        this.encoder.setContextValue(this.savedContext);
    }

    public void resetContext() {
        this.savedContext = 0;
    }
}
