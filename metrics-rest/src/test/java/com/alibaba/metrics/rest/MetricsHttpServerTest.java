package com.alibaba.metrics.rest;

import com.alibaba.metrics.Clock;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.IMetricManager;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.PersistentGauge;
import com.alibaba.metrics.common.CollectLevel;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.bin.BinAppender;
import com.alibaba.metrics.reporter.bin.StructMetricManagerReporter;
import com.alibaba.metrics.rest.server.MetricsHttpServer;
import com.alibaba.metrics.server.MetricsMemoryCache;
import com.alibaba.metrics.server.MetricsOnDisk;
import com.alibaba.metrics.server.MetricsSearchService;
import com.alibaba.metrics.status.LogDescriptionManager;
import com.alibaba.metrics.utils.Constants;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MetricsHttpServerTest {

    private static IMetricManager metricManager = MetricManager.getIMetricManager();

    @Ignore
    @Test
    public void mainStart() throws Exception {
        initTestValue();
        initServer();
    }

    private void initServer() throws InterruptedException {

        long timestamp = System.currentTimeMillis();
        String logRootPath = "logs/metrics/bin/";

        LogDescriptionManager logDescriptionManager = new LogDescriptionManager(logRootPath);
        MetricsMemoryCache cache = new MetricsMemoryCache(Constants.DATA_CACHE_TIME, logDescriptionManager);
        MetricsOnDisk metricsOnDisk = new MetricsOnDisk(logRootPath);

        MetricsCollectPeriodConfig metricsCollectPeriodConfig = new MetricsCollectPeriodConfig();

        BinAppender binAppender = new BinAppender(timestamp, logRootPath, metricsCollectPeriodConfig,
                logDescriptionManager, cache, 5, 5);
        binAppender.initWithoutCheckThread();

        StructMetricManagerReporter reporter = new StructMetricManagerReporter(metricManager, binAppender, null,
                Clock.defaultClock(), TimeUnit.SECONDS, TimeUnit.SECONDS, TimeUnit.SECONDS, MetricFilter.ALL,
                new MetricsCollectPeriodConfig(), new HashMap<String, String>(), null, CollectLevel.CLASSIFIER);

        reporter.start(5, TimeUnit.SECONDS);

        // 以查询打开文件
        MetricsSearchService metricsSearch = MetricsSearchService.getInstance();
        metricsSearch.build(cache, metricsOnDisk, logDescriptionManager);


        new MetricsHttpServer().start();
        new CountDownLatch(1).await();
    }

    private void initTestValue() {

        Gauge xx = new PersistentGauge() {
            @Override
            public Long getValue() {
                return 123456L;
            }
        };

        metricManager.register("group", new MetricName("testgauge", MetricLevel.TRIVIAL), xx);
    }
}
