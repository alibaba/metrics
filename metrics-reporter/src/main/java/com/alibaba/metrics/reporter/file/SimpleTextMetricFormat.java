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
package com.alibaba.metrics.reporter.file;

import com.alibaba.metrics.common.MetricObject;

import java.nio.charset.Charset;
import java.util.Map.Entry;

/**
 * <p>
 * metric timestamp value tagk=tagv tagk=tagv
 * </p>
 *
 *
 */
public class SimpleTextMetricFormat extends MetricFormat {
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    public String format(MetricObject metric) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(metric.getMetric()).append(' ').append(metric.getTimestamp()).append(' ').append(metric.getValue());

        for (Entry<String, String> entry : metric.getTags().entrySet()) {
            sb.append(' ').append(entry.getKey()).append('=').append(entry.getValue());
        }
        return sb.toString();
    }

    @Override
    public byte[] formatToBytes(MetricObject metric) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(metric.getMetric()).append(' ').append(metric.getTimestamp()).append(' ').append(metric.getValue());

        for (Entry<String, String> entry : metric.getTags().entrySet()) {
            sb.append(' ').append(entry.getKey()).append('=').append(entry.getValue());
        }
        return sb.toString().getBytes(UTF_8);
    }
}
