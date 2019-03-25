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
package com.alibaba.metrics.annotation;

import java.util.HashMap;
import java.util.Map;

/**
 * 公共工具类
 *
 */
public class MetricsAnnotationUtil {

    /**
     * 解析tag字符串
     *
     * @param tags
     * @return
     */
    public static Map<String, String> parseTagMap(String tags) {
        HashMap<String, String> tagMap = new HashMap<String, String>(0);
        if (tags != null && tags.length() > 0) {
            String[] kvs = tags.split(",");
            for (String kv : kvs) {
                if (kv != null && kv.length() > 0) {
                    String[] pair = kv.split(":");
                    if (pair.length != 2) {
                        throw new IllegalArgumentException("pair:" + getPairStr(pair) + ", 数组的长度不是2");
                    } else {
                        tagMap.put(pair[0], pair[1]);
                    }
                }
            }
        }
        return tagMap;
    }

    private static String getPairStr(String[] pair) {
        if (pair == null || pair.length == 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : pair) {
            stringBuilder.append(str).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
