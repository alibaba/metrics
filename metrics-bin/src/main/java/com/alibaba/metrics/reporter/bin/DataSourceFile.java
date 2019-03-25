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
package com.alibaba.metrics.reporter.bin;

import com.alibaba.fastjson.JSON;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.StringUtils;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.status.LogDescriptionRegister;
import com.alibaba.metrics.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static com.alibaba.metrics.utils.Constants.LINE_FEED_SEPARATOR;
import static com.alibaba.metrics.utils.Constants.LONG_LENGTH;
import static com.alibaba.metrics.utils.FileUtil.MAX_FILE_SIZE;

public class DataSourceFile {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceFile.class);

    private AbstractFileBackend backend;

    private MetricLevel level;

    private int currentOffset;

    private int currentOrder;

    private String path;

    public DataSourceFile(String path, MetricLevel level) {
        this.path = path;
        this.level = level;
    }

    public void init() throws IOException {
        init(false);
    }

    public void init(boolean readOnly) throws IOException{
        backend = new ChannelFileBackend(path, readOnly);
    }

    public void create() throws IOException {

        File dataSourceFile = new File(path);
        dataSourceFile.createNewFile();
        init();
    }

    public void read(LogDescriptionRegister register) throws IOException {

        logger.info("Read datasource file {}...", path);

        byte[] dataSourceBytes = backend.readAll();

        String[] dataSources = new String(dataSourceBytes).split(LINE_FEED_SEPARATOR);

        if (dataSources == null || dataSources.length == 0) {
            return;
        }

        int maxOffset = 0;
        int maxOrder = 0;
        int dataSourceNum = 0;

        for (String s : dataSources) {
            try {

                if (StringUtils.isBlank(s)) {
                    continue;
                }

                DataSource dataSource = null;

                try {
                    dataSource = (DataSource) JSON.parseObject(s, DataSource.class);
                } catch (Exception e) {
                    logger.error("Error datasource json {} !", s, e);
                }

                if (dataSource == null) {
                    continue;
                }

                dataSource.addMetricObject();

                if (dataSource.getOrder() > maxOrder) {
                    maxOrder = dataSource.getOrder();
                }

                if (dataSource.getOffset() >= maxOffset) {
                    maxOffset = dataSource.getOffset();
                }

                dataSourceNum = dataSourceNum + 1;

                register.addDataSources(dataSource.getMetricObject(), dataSource);

            } catch (Exception e) {
                logger.error("Error file path {}, !", path, e);
            }

        }

        if (dataSourceNum > 0) {
            currentOffset = maxOffset + LONG_LENGTH;
            currentOrder = maxOrder + 1;
        }

    }

    public void write(long metricsTime, byte[] dataSourceBlock, Map<MetricObject, DataSource> dataSourceObjects,
            int length, int lastBlockLength) throws IOException {

        long position = backend.getLength();

        if (FileUtil.checkFileSize(position + length)) {
            backend.write(position, dataSourceBlock, 0, length);
        } else {
            logger.error("Exceeded file size {}, path: {}", MAX_FILE_SIZE, path);
        }

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

    public void setCurrentOrder(int currentOrder) {
        this.currentOrder = currentOrder;
    }

    public String getPath() {
        return path;
    }

    public MetricLevel getLevel() {
        return level;
    }

    public long getPosition() {
        return backend.getLength();
    }

    public void close() throws IOException {
        backend.sync();
        backend.close();
    }
}
