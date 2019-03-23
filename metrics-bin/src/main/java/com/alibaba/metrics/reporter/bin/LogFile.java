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

import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.alibaba.metrics.utils.FileUtil.MAX_FILE_SIZE;

public class LogFile {

    private static final Logger logger = LoggerFactory.getLogger(LogFile.class);

    private static final long DEFAULT_DATA_EXPAND_SIZE = 2 * 1024 * 1024;

    private AbstractFileBackend backend;

    private MetricLevel level;

    private String path;

    public LogFile(String path, MetricLevel level) {
        this.path = path;
        this.level = level;
    }

    public void init() throws IOException {
        backend = new ChannelFileBackend(path, false);
    }

    public void create() throws IOException {

        File logFile = new File(path);
        logFile.createNewFile();
        init();

    }

    public void write(long metricsTime, byte[] dataBlock) throws IOException {

        long position = backend.getLength();

        if (FileUtil.checkFileSize(position + dataBlock.length)) {
            backend.write(position, dataBlock, 0, dataBlock.length);
        } else {
            logger.error("Exceeded file size {}, path: {}", MAX_FILE_SIZE, path);
        }

    }

    public void flush() throws IOException {

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
