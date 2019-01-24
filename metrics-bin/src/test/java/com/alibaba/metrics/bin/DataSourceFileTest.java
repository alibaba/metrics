package com.alibaba.metrics.bin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.bin.BinAppender;
import com.alibaba.metrics.reporter.bin.DataSource;
import com.alibaba.metrics.reporter.bin.LogStatusManager;
import com.alibaba.metrics.server.MetricsMemoryCache;
import com.alibaba.metrics.status.LogDescriptionManager;
import com.alibaba.metrics.status.LogDescriptionRegister;
import com.alibaba.metrics.utils.FileUtil;
import com.alibaba.metrics.utils.Constants;
import com.alibaba.metrics.utils.FigureUtil;

public class DataSourceFileTest {

    private static MetricsCollectPeriodConfig metricsCollectPeriodConfig = new MetricsCollectPeriodConfig();
    private static BinAppender binAppender;
    private static String basePath = "logs/metrics/bin/test/";

    private static LogDescriptionManager logDescriptionManager = new LogDescriptionManager(basePath);
    private static LogDescriptionRegister logDescriptionRegister;
    private static MetricsMemoryCache cache = new MetricsMemoryCache(Constants.DATA_CACHE_TIME);

    private static long todayTimestamp = FigureUtil.getTodayStartTimestamp(System.currentTimeMillis());
    private static long yesterdayTimestamp = todayTimestamp - Constants.DAY_MILLISECONDS;

    private static String metricNameBase = "METRIC_NAME_";

    private static List<MetricObject> list1;

    private static List<MetricObject> list2;

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
    public void testDataSourceReload() {

        clear();
        try {
            init();
            open();
            firstWrite();
            close();
            open();
            secondWrite();
            judge();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clear();
        }

    }

    private static void init() {

        list1 = new ArrayList<MetricObject>() {
            {
                add(MetricObject.named(metricNameBase + 1).withTimestamp(todayTimestamp + 200)
                        .withType(MetricObject.MetricType.COUNTER).withValue(1).withTags(null)
                        .withLevel(MetricLevel.CRITICAL).build());
                add(MetricObject.named(metricNameBase + 1).withTimestamp(todayTimestamp + 200)
                        .withType(MetricObject.MetricType.COUNTER).withValue(1).withTags(new HashMap<String, String>() {
                            {
                                put("tag1", "value1");
                            }
                        }).withLevel(MetricLevel.CRITICAL).build());
                add(MetricObject.named(metricNameBase + 2).withTimestamp(todayTimestamp + 200)
                        .withType(MetricObject.MetricType.COUNTER).withValue(1).withTags(null)
                        .withLevel(MetricLevel.CRITICAL).build());
                add(MetricObject.named(metricNameBase + 2).withTimestamp(todayTimestamp + 200)
                        .withType(MetricObject.MetricType.COUNTER).withValue(1).withTags(new HashMap<String, String>() {
                            {
                                put("tag1", "value1");
                            }
                        }).withLevel(MetricLevel.CRITICAL).build());
            }
        };

        list2 = new ArrayList<MetricObject>() {
            {
                add(MetricObject.named(metricNameBase + 3).withTimestamp(todayTimestamp + 200)
                        .withType(MetricObject.MetricType.COUNTER).withValue(1).withTags(null)
                        .withLevel(MetricLevel.CRITICAL).build());
                add(MetricObject.named(metricNameBase + 3).withTimestamp(todayTimestamp + 200)
                        .withType(MetricObject.MetricType.COUNTER).withValue(1).withTags(new HashMap<String, String>() {
                            {
                                put("tag1", "value1");
                            }
                        }).withLevel(MetricLevel.CRITICAL).build());
                add(MetricObject.named(metricNameBase + 4).withTimestamp(todayTimestamp + 200)
                        .withType(MetricObject.MetricType.COUNTER).withValue(1).withTags(null)
                        .withLevel(MetricLevel.CRITICAL).build());
                add(MetricObject.named(metricNameBase + 4).withTimestamp(todayTimestamp + 200)
                        .withType(MetricObject.MetricType.COUNTER).withValue(1).withTags(new HashMap<String, String>() {
                            {
                                put("tag1", "value1");
                            }
                        }).withLevel(MetricLevel.CRITICAL).build());
            }
        };

    }

    private void open() {
        binAppender = new BinAppender(todayTimestamp, basePath, metricsCollectPeriodConfig, logDescriptionManager,
                cache, 5, 5);
        binAppender.initWithoutCheckThread();
    }

    private void close() {
        binAppender.close();
        logDescriptionManager.clear();
    }

    public void judge() {
        // assert logStatusManager.getDataSourceNum() == 8;
        logDescriptionRegister = logDescriptionManager.getLogDescriptions(todayTimestamp);
        for (MetricObject metricObject : list1) {
            DataSource dataSource = logDescriptionRegister.getDataSource(metricObject);
            assert dataSource != null;
        }
        for (MetricObject metricObject : list2) {
            DataSource dataSource = logDescriptionRegister.getDataSource(metricObject);
            assert dataSource != null;
        }
    }

    private void firstWrite() {
        binAppender.append(todayTimestamp, new HashMap<MetricLevel, List<MetricObject>>() {
            {
                put(MetricLevel.CRITICAL, list1);
            }
        }, lastUpdateTime);
        binAppender.append(todayTimestamp + 1000, new HashMap<MetricLevel, List<MetricObject>>() {
            {
                put(MetricLevel.CRITICAL, list2);
            }
        }, lastUpdateTime);
    }

    private void secondWrite() {
        binAppender.append(todayTimestamp, new HashMap<MetricLevel, List<MetricObject>>() {
            {
                put(MetricLevel.CRITICAL, list1);
            }
        }, lastUpdateTime);
        binAppender.append(todayTimestamp + 1000, new HashMap<MetricLevel, List<MetricObject>>() {
            {
                put(MetricLevel.CRITICAL, list2);
            }
        }, lastUpdateTime);
    }

    private static void clear() {
        String path = FileUtil.getBasePath(basePath);
        FileUtil.deleteDir(new File(path));
    }

}
