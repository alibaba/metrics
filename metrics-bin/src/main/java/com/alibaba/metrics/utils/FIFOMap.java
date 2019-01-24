package com.alibaba.metrics.utils;

import java.util.Comparator;
import java.util.LinkedHashMap;
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
