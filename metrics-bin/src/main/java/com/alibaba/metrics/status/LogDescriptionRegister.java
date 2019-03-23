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
package com.alibaba.metrics.status;

import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.StringUtils;
import com.alibaba.metrics.bean.MetricSearch;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.reporter.bin.DataSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LogDescriptionRegister {

    private long visitTime = Long.MAX_VALUE;

    private int currentOffset = 0;

    private int currentOrder = 0;

    private Map<String, Map<MetricObject, DataSource>> dataSources = new HashMap<String, Map<MetricObject, DataSource>>();

    public void addDataSources(MetricObject metricObject, DataSource dataSource) {

        String metricName = metricObject.getMetric();
        Map<MetricObject, DataSource> dataSourcesByKey = dataSources.get(metricName);
        if (dataSourcesByKey == null) {
            dataSourcesByKey = new HashMap<MetricObject, DataSource>();
            dataSources.put(metricName, dataSourcesByKey);
        }
        dataSourcesByKey.put(dataSource.getMetricObject(), dataSource);
    }

    public void addDataSources(Map<MetricObject, DataSource> dataSources) {

        if (dataSources != null) {
            for (Entry<MetricObject, DataSource> entry : dataSources.entrySet()) {
                addDataSources(entry.getKey(), entry.getValue());
            }
        }

    }

    public DataSource getDataSource(MetricObject metricObject) {

        String metricName = metricObject.getMetric();
        if (StringUtils.isNotBlank(metricName)) {
            Map<MetricObject, DataSource> dataSourcesByKey = dataSources.get(metricName);
            if (dataSourcesByKey == null) {
                return null;
            } else {
                return dataSourcesByKey.get(metricObject);
            }
        } else {
            return null;
        }
    }

    public Map<MetricLevel, Set<DataSource>> getMetricNames(List<MetricSearch> metricNames) {

        if (metricNames == null || metricNames.size() == 0) {
            return null;
        }

        Map<MetricLevel, Set<DataSource>> result = new HashMap<MetricLevel, Set<DataSource>>();

        for (MetricSearch simpleMetric : metricNames) {

            String key = simpleMetric.getKey();
            Map<String, String> tags = simpleMetric.getTags();

            Map<MetricObject, DataSource> dataSourcesByKey = dataSources.get(key);
            if (dataSourcesByKey == null){
                continue;
            }

            for (Entry<MetricObject, DataSource> entry0 : dataSourcesByKey.entrySet()) {

                DataSource dataSource = entry0.getValue();
                String metricObjectName = dataSource.getMetricName();

                boolean keysEqual = true;
                boolean tagsEqual = true;

//                if (!StringUtils.equals(key, metricObjectName)) {
//                    keysEqual = false;
//                    continue;
//                }

                if (tags != null && tags.size() > 0) {

                    Map<String, String> dataSourceTag = dataSource.getTags();

                    if (dataSourceTag == null || dataSourceTag.size() == 0) {
                        tagsEqual = false;
                    } else {
                        for (Entry<String, String> entry : tags.entrySet()) {

                            String entryKey = entry.getKey();
                            String entryValue = entry.getValue();

                            if (dataSourceTag.containsKey(entryKey)) {

                                // *号代表这个包含这个tagname的tag都匹配
                                if ("*".equals(entryValue)) {
                                    continue;
                                } else {

                                    if (entryValue == null) {
                                        continue;
                                    }
                                    String[] metricTagValues = entryValue.split("\\|");
                                    String dataSourceValue = dataSourceTag.get(entryKey);

                                    boolean orMatch = false;

                                    for (String tag : metricTagValues) {

                                        if (StringUtils.equals(tag, dataSourceValue)) {
                                            orMatch = true;
                                            break;
                                        }

                                    }

                                    if (!orMatch) {
                                        tagsEqual = false;
                                    }
                                }

                            } else {
                                tagsEqual = false;
                                break;
                            }

                        }

                    }

                }

                if (keysEqual && tagsEqual) {
                    addSearchResult(dataSource, result);
                }

            }
        }
        return result;
    }

    private void addSearchResult(DataSource dataSource, Map<MetricLevel, Set<DataSource>> result) {

        MetricLevel level = dataSource.getLevel();

        Set<DataSource> dataSourceSet = result.get(level);

        if (dataSourceSet == null) {
            dataSourceSet = result.put(level, new HashSet<DataSource>());
            dataSourceSet = result.get(level);
        }

        dataSourceSet.add(dataSource);

    }

    public void clear() {
        // this.enable = false;
        dataSources = new HashMap<String, Map<MetricObject, DataSource>>();
    }

    public int getDataSourceNum() {
        return dataSources.size();
    }

    public int getCurrentOffset() {
        return currentOffset;
    }

    public void setCurrentOffset(int currentOffset) {
        this.currentOffset = currentOffset;
    }

    public int getCurrentOrder() {
        return currentOrder;
    }

    public long getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(long visitTime) {
        this.visitTime = visitTime;
    }

    public void setCurrentOrder(int currentOrder) {
        this.currentOrder = currentOrder;
    }

}
