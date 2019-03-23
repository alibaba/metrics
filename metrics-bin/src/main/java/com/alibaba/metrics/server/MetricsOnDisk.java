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
package com.alibaba.metrics.server;

import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.bean.MetricResult;
import com.alibaba.metrics.reporter.bin.AbstractFileBackend;
import com.alibaba.metrics.reporter.bin.ChannelFileBackend;
import com.alibaba.metrics.reporter.bin.DataSource;
import com.alibaba.metrics.reporter.bin.IndexData;
import com.alibaba.metrics.reporter.bin.zigzag.LongDZBP;
import com.alibaba.metrics.utils.Constants;
import com.alibaba.metrics.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class MetricsOnDisk extends MetricsDataStore {

    private static final Logger logger = LoggerFactory.getLogger(MetricsOnDisk.class);
    private static String dataPath;

    public MetricsOnDisk(String dataPath) {
        this.dataPath = dataPath;
    }

    public Map<MetricLevel, SortedMap<Long, List<MetricResult>>> getDataFromDisk(int precision, MetricLevel level,
            Set<DataSource> dataSources, Map<Long, IndexData> indexs,
            Map<MetricLevel, SortedMap<Long, List<MetricResult>>> results) {

        // 直接把缓存查询过后的结果传入，根据里面数据的填充情况来判断
        SortedMap<Long, List<MetricResult>> result = results.get(level);

        if (result == null) {
            result = new TreeMap<Long, List<MetricResult>>();
            results.put(level, result);
        }

        boolean dataBlockExceeded = false;

        logger.info("MetricLevel:" + level);
        for (Entry<Long, IndexData> indexEntry : indexs.entrySet()) {

            long timestamp = indexEntry.getKey();
            long indexStart = indexEntry.getValue().getIndexStart();
            long indexEnd = indexEntry.getValue().getIndexEnd();
            logger.info("indexStart:" + indexStart);
            logger.info("indexEnd:" + indexEnd);
            List<MetricResult> currentResult = result.get(timestamp);

            int length = (int) (indexEnd - indexStart);

            if (length > Constants.DEFAULT_MAX_DATA_BLOCK_SIZE) {
                dataBlockExceeded = true;
                continue;
            }

            byte[] b = new byte[length];
            logger.info("byte[] b:" + length);
            AbstractFileBackend backend = getFileBackend(timestamp, level);

            try {
                backend.read(indexStart, b);
            } catch (IOException e) {
                logger.error("Read file error!", e);
            } finally {
                if (backend != null) {
                    try {
                        backend.close();
                    } catch (IOException e) {
                        logger.error("Close file error!", e);
                    }
                }
            }

            long[] dataArray = null;

            try {
                dataArray = LongDZBP.fromBytes(b);
            } catch (Exception e) {
                logger.error("LongDZBP data error!");
                continue;
            }

            currentResult = createMetricsResult(dataSources, dataArray, timestamp, level, precision, currentResult);
            result.put(timestamp, currentResult);
        }

        if (dataBlockExceeded) {
            logger.warn("Data block exceeded!");
        }

        results.put(level, result);
        return results;
    }

    private AbstractFileBackend getFileBackend(long timestamp, MetricLevel level) {

        String filePath = FileUtil.getLogFileName(timestamp, dataPath, level);
        AbstractFileBackend backend = null;

        try {
            backend = new ChannelFileBackend(filePath, true);
        } catch (IOException e) {
            logger.error("Open file error {}", filePath);
        }

        return backend;

    }

}
