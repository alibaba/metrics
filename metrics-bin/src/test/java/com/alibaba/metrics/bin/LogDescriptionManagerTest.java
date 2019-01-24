package com.alibaba.metrics.bin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.metrics.Compass;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.common.MetricObject.MetricType;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.bin.BinAppender;
import com.alibaba.metrics.server.MetricsMemoryCache;
import com.alibaba.metrics.status.LogDescriptionManager;
import com.alibaba.metrics.status.LogDescriptionRegister;
import com.alibaba.metrics.utils.FileUtil;
import com.alibaba.metrics.utils.Constants;
import com.alibaba.metrics.utils.FigureUtil;

public class LogDescriptionManagerTest {

    private String logRootPath = "logs/metrics/bin/test/";
    private MetricsCollectPeriodConfig metricsCollectPeriodConfig = new MetricsCollectPeriodConfig();
    private LogDescriptionManager logDescriptionManager = new LogDescriptionManager(logRootPath);
    private MetricsMemoryCache cache = new MetricsMemoryCache(Constants.DATA_CACHE_TIME);

    private Compass logDescriptionGet = MetricManager.getCompass("self-statistics",
            new MetricName("middleware.eagleeye.log-meta.readdisk"));

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

            initData();
            correctnessTest();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clear();
        }

    }

    public void initData() {

        long[] startTimestamps = new long[3];

        long timstamp = System.currentTimeMillis();
        long firstDay = FigureUtil.getTodayStartTimestamp(timstamp - 86400000 * 2);
        long secondDay = firstDay + 86400000;

        startTimestamps[0] = firstDay + 86399999;
        startTimestamps[1] = secondDay + 86399999;
        startTimestamps[2] = timstamp;

        BinAppender appender = new BinAppender(startTimestamps[0], logRootPath, metricsCollectPeriodConfig,
                logDescriptionManager, cache, 5, 5);
        appender.initWithoutCheckThread();

        for (long timestampx : startTimestamps) {

            long baseTimestamp = FigureUtil.getTodayStartTimestamp(timestampx);

            for (int i = 0; i < 86400000; i = i + 1000) {

                long timestamp = baseTimestamp + i;

                if (timestamp > timestampx) {
                    break;
                }

                Map<MetricLevel, List<MetricObject>> result = new HashMap<MetricLevel, List<MetricObject>>();

                for (MetricLevel level : MetricLevel.values()) {

                    int precision = metricsCollectPeriodConfig.period(level) * 1000;
                    if (i % precision == 0) {

                        List<MetricObject> metricObjects = new ArrayList<MetricObject>();

                        for (int j = 0; j <= 100; j++) {
                            MetricObject metricObject = MetricObject.named("METRIC_TEST_" + level + "_" + j)
                                    .withLevel(level).withTimestamp(timestamp).withType(MetricType.DELTA)
                                    .withValue(baseTimestamp + i).build();
                            metricObjects.add(metricObject);
                        }

                        result.put(level, metricObjects);
                    }

                }

                appender.append(timestamp, result, lastUpdateTime);

            }

        }

        appender.close();

    }

    public void correctnessTest() throws InterruptedException {

        assert logDescriptionManager.getSize() == 3;

        // 很短的缓存保存周期，用于测试缓存失效
        logDescriptionManager.start(2, 2, TimeUnit.SECONDS);
        Thread.sleep(3000L);

        long timestampToday = FigureUtil.getTodayStartTimestamp(System.currentTimeMillis());
        long timestampYesterday = timestampToday - Constants.DAY_MILLISECONDS;
        long timestamp2DaysBefore = timestampYesterday - Constants.DAY_MILLISECONDS;
        long timestamp3DaysBefore = timestamp2DaysBefore - Constants.DAY_MILLISECONDS;

        LogDescriptionRegister todayRegister = logDescriptionManager.getLogDescriptions(timestampToday);

        assert logDescriptionManager.getSize() == 1;
        assert todayRegister.getDataSourceNum() == 505;

        LogDescriptionRegister yesterdayRegister = logDescriptionManager.getLogDescriptions(timestampYesterday);

        Assert.assertEquals(505, yesterdayRegister.getDataSourceNum());

        LogDescriptionRegister twoDaysBeforeRegister = logDescriptionManager.getLogDescriptions(timestamp2DaysBefore);
        twoDaysBeforeRegister.getDataSourceNum();

        assert logDescriptionManager.getSize() == 3;

        LogDescriptionRegister threeDaysBeforeRegister = logDescriptionManager.getLogDescriptions(timestamp3DaysBefore);
        threeDaysBeforeRegister.getDataSourceNum();

        assert logDescriptionManager.getSize() == 4;

        /**---------------------------------------------------------------------*/

        logDescriptionManager.clear();

        todayRegister = logDescriptionManager.getLogDescriptions(timestampToday);
        yesterdayRegister = logDescriptionManager.getLogDescriptions(timestampYesterday);

        Thread.sleep(1000L);
        yesterdayRegister = logDescriptionManager.getLogDescriptions(timestampYesterday);
        assert logDescriptionManager.getSize() == 2;
        Thread.sleep(1000L);
        yesterdayRegister = logDescriptionManager.getLogDescriptions(timestampYesterday);
        assert logDescriptionManager.getSize() == 2;
        Thread.sleep(1000L);
        yesterdayRegister = logDescriptionManager.getLogDescriptions(timestampYesterday);
        assert logDescriptionManager.getSize() == 2;
        Thread.sleep(1000L);
        yesterdayRegister = logDescriptionManager.getLogDescriptions(timestampYesterday);
        assert logDescriptionManager.getSize() == 2;
        Thread.sleep(1000L);
        yesterdayRegister = logDescriptionManager.getLogDescriptions(timestampYesterday);
        assert logDescriptionManager.getSize() == 2;

        Thread.sleep(4000L);
        assert logDescriptionManager.getSize() == 1;

        System.out.println("read disk total count : " + logDescriptionGet.getCount());
        System.out.println("cache hit count : " + logDescriptionGet.getSuccessCount());
        System.out.println("read disk average time : " + (logDescriptionGet.getSnapshot().getMean() / 1000000));
        System.out.println("read disk min time : " + (logDescriptionGet.getSnapshot().getMin()) / 1000000);
        System.out.println("read disk max time : " + (logDescriptionGet.getSnapshot().getMax()) / 1000000);
        // Compass readTime = MetricManager.getCompass("self-statistics", new
        // System.out.println(readTime.getCount());

        // Thread.sleep(3000L);
    }

    public void clear() {
        File file = new File(FileUtil.getBasePath(logRootPath));
        FileUtil.deleteDir(file);
    }
}
