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
package com.alibaba.metrics.integrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class wraps necessary information for a scraper to know how and what to collect
 * if it does not want to know how and what to collect.
 * The information includes the port, url, and basic metrics including jvm and middleware metrics.
 */
public class ScrapeConfig {

    private static final String[] metricsToScrape = {
        "middleware.tomcat.thread.busy_count",
        "middleware.tomcat.thread.total_count",
        "middleware.tomcat.http.request_count",
        "middleware.tomcat.http.request.error_count",
        "middleware.tomcat.http.request.bytes_received",
        "middleware.tomcat.http.request.bytes_sent",
        "jvm.mem.heap.max",
        "jvm.mem.heap.used",
        "jvm.mem.heap.usage",
        "jvm.mem.non_heap.max",
        "jvm.mem.non_heap.used",
        "jvm.mem.non_heap.usage",
        "jvm.buffer_pool.mapped.capacity",
        "jvm.buffer_pool.mapped.used",
        "jvm.buffer_pool.direct.capacity",
        "jvm.buffer_pool.direct.used",
        "jvm.mem.pools.metaspace.usage",
        "jvm.mem.pools.code_cache.usage",
        "jvm.file_descriptor.open_ratio",
        "jvm.thread.count",
        "jvm.thread.daemon.count",
        "jvm.thread.deadlock.count",
        "jvm.thread.runnable.count",
        "jvm.gc.parnew.count",
        "jvm.gc.parnew.count",
        "jvm.gc.concurrentmarksweep.count",
        "jvm.gc.concurrentmarksweep.time",
        "jvm.coroutine.runningStates",
        "jvm.coroutine.switchCount",
        "jvm.coroutine.waitTimeTotal",
        "jvm.coroutine.runningTimeTotal",
        "jvm.coroutine.completeTaskCount",
        "jvm.coroutine.createTaskCount",
        "jvm.coroutine.parkCount",
        "jvm.coroutine.unparkCount",
        "jvm.coroutine.lazyUnparkCount",
        "jvm.coroutine.unparkInterruptSelectorCount",
        "jvm.coroutine.selectableIOCount",
        "jvm.coroutine.timeOutCount",
        "jvm.coroutine.eventLoopCount",
        "jvm.coroutine.queueLength",
    };

    private int port;
    private String url;
    private List<Map<String, Object>> queries;

    public ScrapeConfig() {

    }

    public ScrapeConfig(int port, String url) {
        this.port = port;
        this.url = url;
    }

    public int getPort() {
        return port;
    }

    public String getUrl() {
        return url;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setQueries(List<Map<String, Object>> queries) {
        this.queries = queries;
    }

    public List<Map<String, Object>> getQueries() {
        return queries;
    }

    public void build() {
        queries = new ArrayList<Map<String, Object>>(metricsToScrape.length);
        for (String metric: metricsToScrape) {
            Map<String, Object> query = new HashMap<String, Object>();
            query.put("key", metric);
            queries.add(query);
        }
    }
}
