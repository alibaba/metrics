package com.alibaba.metrics.bin;

import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.reporter.bin.IndexData;
import com.alibaba.metrics.reporter.bin.IndexFile;
import com.alibaba.metrics.utils.FigureUtil;
import com.alibaba.metrics.utils.FileUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class IndexFileTest {

    private static String logRootPath = "logs/metrics/bin/test/singletest/";
    private static String pathToDelete = "logs/metrics/bin/test/";
    private static IndexFile indexFile = new IndexFile(
            FileUtil.getIndexFileName(System.currentTimeMillis(), logRootPath, MetricLevel.CRITICAL),
            MetricLevel.CRITICAL);
    private static long baseTimestamp = FigureUtil.getTodayStartTimestamp(System.currentTimeMillis());

    @Test
    public void mainProcess() {
        clear();
        try {
            initFile();
            checkFile();
            randomAccess();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            clear();
        }
    }

    public void clear() {
        FileUtil.deleteDir(new File(FileUtil.getBasePath(pathToDelete)));
    }

    public void initFile() {
        try {

            File baseDir = new File(FileUtil.getMetricsDir(System.currentTimeMillis(), logRootPath));
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }

            indexFile.init();
            indexFile.create();

        } catch (IOException e) {
            e.printStackTrace();
        }

        long indexStart = 0;
        long indexEnd = 0;

        for(int i = 0; i < 86400; i++){
            long metricsTime = baseTimestamp + i * 1000;
            try {
                indexEnd = indexStart + i;
                indexFile.write(metricsTime, indexStart, indexEnd);
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                indexStart = indexEnd;
            }
        }

        try {
            indexFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void checkFile(){

        try {
            indexFile.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<Long, IndexData> indexDatas = indexFile.read(baseTimestamp, baseTimestamp + 86400000);

        assert indexDatas.size() == 86399;

        long indexStart = 0;
        long indexEnd = 0;

        for(int i = 1; i < 86400;i++){
            long index = baseTimestamp + i * 1000;
            IndexData indexData = indexDatas.get(index);

            assert indexStart == indexData.getIndexStart();
            indexEnd = indexStart + i;
            assert indexEnd == indexData.getIndexEnd();

            indexStart = indexEnd;
        }
        try {
            indexFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void randomAccess(){

        try {
            indexFile.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<Long, IndexData> indexDatas = indexFile.read(baseTimestamp + 20000, baseTimestamp + 30000);

        assert indexDatas.size() == 10;

        long indexStart = 190;
        long indexEnd = 210;
        long size = 20;

        for(int i = 0; i < 10; i = i + 1){
            long timestamp = baseTimestamp + 20000 + 1000 * i;
            IndexData indexData = indexDatas.get(timestamp);
            assert indexData.getIndexStart() == indexStart;
            assert indexData.getIndexEnd() == indexEnd;
            indexStart = indexStart + size;
            size = size + 1;
            indexEnd = indexEnd + size;
        }

        try {
            indexFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
