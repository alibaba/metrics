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
package com.alibaba.metrics.utils;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class FIFOMap<K, V> extends TreeMap<K, V> implements NavigableMap<K, V>{

    private final int cacheSize;

    private K maxKey;

    private K minKey;

    public FIFOMap(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    @Override
    public V put(K key, V data){

        super.put(key, data);

        if (super.size() > cacheSize){
            return super.remove(super.firstKey());
        }

        return data;
    }

    @Override
    public NavigableMap<K, V> subMap(K fromKey, K toKey) {
        return super.subMap(fromKey, true, toKey, true);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<K, V> entry : entrySet()) {
            sb.append(String.format("%s:%s ", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }

}
