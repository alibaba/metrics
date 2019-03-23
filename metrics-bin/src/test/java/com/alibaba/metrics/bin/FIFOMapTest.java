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
package com.alibaba.metrics.bin;

import com.alibaba.metrics.utils.FIFOMap;
import org.junit.Test;

public class FIFOMapTest {

    @Test
    public void FIFOMapTest() {
        FIFOMap<Long, String> cacheMap = new FIFOMap<Long, String>(10);
        for (int i = 1; i <= 45; i++) {
            cacheMap.put((long) i, "value" + i);
        }

        assert cacheMap.firstKey() == 36;
        assert "36:value36 37:value37 38:value38 39:value39 40:value40 41:value41 42:value42 43:value43 44:value44 45:value45 "
                .equals(cacheMap.toString());
    }
}
