package com.alibaba.metrics.bin;

import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.common.MetricObject.MetricType;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.bin.DataSourceFile;
import com.alibaba.metrics.reporter.bin.IndexData;
import com.alibaba.metrics.reporter.bin.IndexFile;
import com.alibaba.metrics.reporter.bin.LogFile;
import com.alibaba.metrics.reporter.bin.MetricsLog;
import com.alibaba.metrics.server.MetricsMemoryCache;
import com.alibaba.metrics.server.MetricsOnDisk;
import com.alibaba.metrics.status.LogDescriptionManager;
import com.alibaba.metrics.status.LogDescriptionRegister;
import com.alibaba.metrics.utils.Constants;
import com.alibaba.metrics.utils.FigureUtil;
import com.alibaba.metrics.utils.FileUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrokenFileTest {

    private static String logRootPath = "logs/metrics/bin/test/singletest/";
    private static MetricsOnDisk diskServer = new MetricsOnDisk(logRootPath);

    private MetricsCollectPeriodConfig metricsCollectPeriodConfig = new MetricsCollectPeriodConfig();
    private LogDescriptionManager logDescriptionManager = new LogDescriptionManager(logRootPath);
    private MetricsMemoryCache cache = new MetricsMemoryCache(Constants.DATA_CACHE_TIME);

    private DataSourceFile dataSourceFile;
    private IndexFile indexFile;

    private static Map<MetricLevel, Long> lastUpdateTime = new HashMap<MetricLevel, Long>() {
        {
            put(MetricLevel.CRITICAL, 0L);
            put(MetricLevel.MAJOR, 0L);
            put(MetricLevel.NORMAL, 0L);
            put(MetricLevel.MINOR, 0L);
            put(MetricLevel.TRIVIAL, 0L);
        }
    };

    @Test
    public void mainProcess() {
        clear();
        try {
            initBrokenData();
            brokenDataSourceTest();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clear();
        }

    }

    public void initBrokenData() {

        MetricLevel level = MetricLevel.CRITICAL;
        long baseTimestamp = FigureUtil.getTodayStartTimestamp(System.currentTimeMillis());

        String basePath = FileUtil.getMetricsDir(baseTimestamp, logRootPath);
        String dataSourceFileName = FileUtil.getDataSourceFileName(baseTimestamp, logRootPath, level);
        String indexFileName = FileUtil.getIndexFileName(baseTimestamp, logRootPath, level);
        String logFileName = FileUtil.getLogFileName(baseTimestamp, logRootPath, level);

        LogDescriptionRegister logDescriptionRegister = new LogDescriptionRegister();
        logDescriptionManager.setLogDescriptions(baseTimestamp, logDescriptionRegister);

        MetricsLog metricsLog = new MetricsLog(MetricLevel.CRITICAL, logDescriptionRegister, logDescriptionManager,
                cache, basePath, dataSourceFileName, indexFileName, logFileName, baseTimestamp);
        metricsLog.init();

        int precision = metricsCollectPeriodConfig.period(level) * 1000;

        for (int i = 0; i < 10000; i++) {

            long timestamp = baseTimestamp + precision * i;

            List<MetricObject> metricObjects = new ArrayList<MetricObject>();

            for (int j = 0; j < 100; j++) {
                MetricObject metricObject = MetricObject.named("METRIC_TEST_" + level + "_" + j).withLevel(level)
                        .withTimestamp(timestamp).withType(MetricType.DELTA)
                        .withValue(Long.parseLong("" + timestamp + j)).build();
                metricObjects.add(metricObject);
            }

            try {
                metricsLog.write(timestamp, metricObjects);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            metricsLog.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dataSourceFile = new DataSourceFile(dataSourceFileName, level);
        indexFile = new IndexFile(indexFileName, level);
        try {
            dataSourceFile.init();
            indexFile.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] brokenDataSourceBytes = "{\"absent\":0.9,\"addTime\":1499224956000,\"archiveType\":1,\"fieldType\":1,\"level\":\"CRITICAL\",\"metricName\":\"middleware.nginx.conn.active\",\"metricType\":\"GAUGE\",\"offset\":0,\"order\""
                .getBytes();
        try {
            dataSourceFile.write(0, brokenDataSourceBytes, null, brokenDataSourceBytes.length, 0);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        byte[] brokenIndexBytes = "wiudsiisiuafiaeskuhfasudfauf".getBytes();

        try {
            indexFile.write(123456, brokenIndexBytes, brokenIndexBytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LogFile logFile = new LogFile(logFileName, level);

        metricsLog = new MetricsLog(MetricLevel.CRITICAL, logDescriptionRegister, logDescriptionManager, cache,
                basePath, dataSourceFileName, indexFileName, logFileName, baseTimestamp);
        metricsLog.init();

        for (int i = 10000; i < 20000; i++) {

            long timestamp = baseTimestamp + precision * i;

            List<MetricObject> metricObjects = new ArrayList<MetricObject>();

            for (int j = 100; j < 200; j++) {
                MetricObject metricObject = MetricObject.named("METRIC_TEST_" + level + "_" + j).withLevel(level)
                        .withTimestamp(timestamp).withType(MetricType.DELTA).withValue(3.1415926).build();
                metricObjects.add(metricObject);
            }

            try {
                metricsLog.write(timestamp, metricObjects);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            dataSourceFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void brokenDataSourceTest() {

        long baseTimestamp = FigureUtil.getTodayStartTimestamp(System.currentTimeMillis());

        LogDescriptionRegister register = logDescriptionManager.getLogDescriptions(baseTimestamp);

        Map<Long, IndexData> indexResult = logDescriptionManager.getIndexFromDisk(baseTimestamp, baseTimestamp + 86400000,
                baseTimestamp, MetricLevel.CRITICAL);

        System.out.println("dataSource num : " + register.getDataSourceNum());
        System.out.println("index num : " + indexResult.size());

        assert register.getDataSourceNum() == 200;
        assert indexResult.size() == 10000;
    }

    public void clear() {
        FileUtil.deleteDir(new File(FileUtil.getBasePath(logRootPath)));
    }

}
