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
