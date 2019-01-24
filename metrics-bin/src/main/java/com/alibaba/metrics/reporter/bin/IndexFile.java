package com.alibaba.metrics.reporter.bin;

import static com.alibaba.metrics.utils.Constants.INDEX_SEPARATOR;
import static com.alibaba.metrics.utils.Constants.LINE_FEED;
import static com.alibaba.metrics.utils.FileUtil.MAX_FILE_SIZE;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.utils.Constants;
import com.alibaba.metrics.utils.FigureUtil;
import com.alibaba.metrics.utils.FileUtil;

public class IndexFile {

    private static final Logger logger = LoggerFactory.getLogger(IndexFile.class);

    private String path;
    private AbstractFileBackend backend;

    private MetricLevel level;

    private static MetricsCollectPeriodConfig metricsCollectPeriodConfig = new MetricsCollectPeriodConfig();
    public static final int POINT_SIZE = 16;

    public IndexFile(String path, MetricLevel level) {
        this.path = path;
        this.level = level;
    }

    public void init() throws IOException {
        init(false);
    }

    public void init(boolean readOnly) throws IOException{
        backend = new ChannelFileBackend(path, readOnly);
        setLength();
    }

    public void create() throws IOException {
        File indexFile = new File(path);
        indexFile.createNewFile();
        init();
    }

    public void setLength() throws IOException {
        long indexFileLength = Constants.DAY_SECONDS / metricsCollectPeriodConfig.period(level) * 16;
        if (backend.getLength() != indexFileLength) {
            backend.setLength(0);
            backend.setLength(indexFileLength);
        }
    }


    public void write(long metricsTime, long indexStart, long indexEnd) throws IOException {

        long baseTimestamp = FigureUtil.getTodayStartTimestamp(metricsTime);
        long interval = metricsCollectPeriodConfig.period(level) * 1000;
        long ordinal = (metricsTime - baseTimestamp) / interval;

        long position = ordinal * POINT_SIZE;

        byte[] b = getPositionBytes(indexStart, indexEnd);

        backend.write(position, b, 0, POINT_SIZE);

    }
    public void write(long metricsTime, byte[] indexBlock, int length) throws IOException {

        long position = backend.getLength();
        backend.write(position, indexBlock, 0, length);

    }

    public Map<Long, IndexData> read(long startTime, long endTime) {

        if (endTime < startTime){
            logger.warn("endtime {} < starttime {} when searching", endTime, startTime);
            return null;
        }

        Map<Long, IndexData> result = new HashMap<Long, IndexData>(0);
        int milliInterval = metricsCollectPeriodConfig.period(level) * 1000;

        byte[] indexBytes = null;

        startTime = startTime / milliInterval * milliInterval;
        endTime = endTime / milliInterval * milliInterval;

        long baseTimestamp = FigureUtil.getTodayStartTimestamp(startTime);
        int span = (int) ((startTime - baseTimestamp) / milliInterval);
        int pointNum = (int) ((endTime - startTime) / milliInterval) + 1;

        long offset = span * POINT_SIZE;
        int length = pointNum * POINT_SIZE;

        long fileLength = backend.getLength();

        if (offset + length > fileLength) {
            logger.warn("Offset:{} plus length:{} exceeds file length {}", offset, length, fileLength);
        }

        indexBytes = new byte[length];

        try {

            backend.read(offset, indexBytes);
        } catch (IOException e) {
            logger.error("Read indexFile {} error when searching!", path);
            return null;
        }

        pointNum = length / POINT_SIZE;

        long lastIndexEnd = 0;

        for (int i = 0; i < pointNum; i++) {

            long timestamp = startTime + i * milliInterval;

            long indexStart = FigureUtil.getLong(indexBytes, i * POINT_SIZE);
            long indexEnd = FigureUtil.getLong(indexBytes, i * POINT_SIZE + 8);

            if (indexEnd > 0 && indexStart < indexEnd && indexStart >= lastIndexEnd) {
                lastIndexEnd = indexEnd;
                result.put(timestamp, new IndexData(indexStart, indexEnd));
            }
        }

        return result;

    }

    private byte[] getPositionBytes(long indexStart, long indexEnd) {
        byte[] b = new byte[16];

        byte[] indexStartBytes = FigureUtil.getLongBytes(indexStart);
        byte[] indexEndBytes = FigureUtil.getLongBytes(indexEnd);

        for (int i = 0; i < 8; i++) {
            b[i] = indexStartBytes[i];
        }

        for (int i = 0; i < 8; i++) {
            b[i + 8] = indexEndBytes[i];
        }

        return b;

    }

    public void close() throws IOException {
        backend.sync();
        backend.close();
    }

}
