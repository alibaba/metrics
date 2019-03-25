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

import com.alibaba.metrics.reporter.bin.zigzag.encodings.LongEncoder;

public class LongEncodingFilter implements LongFilter {

    public static class Factory implements LongFilterFactory {

        private final LongEncoder encoder;

        public Factory(LongEncoder encoder) {
            this.encoder = encoder;
        }

        public LongFilter newFilter(long firstValue) {
            LongEncoder e = getLongEncoder(firstValue);
            return new LongEncodingFilter(e);
        }

        private LongEncoder getLongEncoder(long firstValue) {
            // FIXME: create a new encoder.
            this.encoder.setContextValue(firstValue);
            return this.encoder;
        }
    }

    private final LongEncoder encoder;

    private long savedContext = 0;

    public LongEncodingFilter(LongEncoder encoder) {
        this.encoder = encoder;
    }

    public long filterLong(long value) {
        return this.encoder.encodeLong(value);
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
