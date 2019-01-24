package com.alibaba.metrics.bin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.bean.MetricSearch;
import com.alibaba.metrics.bean.MetricsSearchRequest;
import com.alibaba.metrics.bean.MetricsSearchResponse;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.common.MetricObject.Builder;
import com.alibaba.metrics.common.MetricObject.MetricType;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.bin.BinAppender;
import com.alibaba.metrics.server.MetricsMemoryCache;
import com.alibaba.metrics.server.MetricsOnDisk;
import com.alibaba.metrics.server.MetricsSearchService;
import com.alibaba.metrics.status.LogDescriptionManager;
import com.alibaba.metrics.utils.FileUtil;
import com.alibaba.metrics.utils.Constants;
import com.alibaba.metrics.utils.FigureUtil;

public class MetricsSearchServiceTest {

    private static String logRootPath = "logs/metrics/bin/test/";
    private static MetricsOnDisk diskServer = new MetricsOnDisk(logRootPath);

    private MetricsCollectPeriodConfig metricsCollectPeriodConfig = new MetricsCollectPeriodConfig();
    private LogDescriptionManager logDescriptionManager = new LogDescriptionManager(logRootPath);
    private MetricsMemoryCache cache = new MetricsMemoryCache(Constants.DATA_CACHE_TIME);

    private static Map<MetricLevel, Long> lastUpdateTime = new HashMap<MetricLevel, Long>(){{
        put(MetricLevel.CRITICAL, 0L);
        put(MetricLevel.MAJOR, 0L);
        put(MetricLevel.NORMAL, 0L);
        put(MetricLevel.MINOR, 0L);
        put(MetricLevel.TRIVIAL, 0L);
    }};

    private MetricsSearchService service = MetricsSearchService.getInstance();

    @Ignore
    @Test
    public void mainServer() {

        clear();
        try {
            initData();
            dataInCurrentOneDay();
            dataInPassedDay();
            dataInAnyDay();
            hasNoData();
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

        for(long timestampx : startTimestamps){

            long baseTimestamp = FigureUtil.getTodayStartTimestamp(timestampx);

            for (int i = 0; i < 86400000; i = i + 1000) {

                long timestamp = baseTimestamp + i;

                if (timestamp > timestampx){
                    break;
                }

                Map<MetricLevel, List<MetricObject>> result = new HashMap<MetricLevel, List<MetricObject>>();

                for (MetricLevel level : MetricLevel.values()) {

                    int precision = metricsCollectPeriodConfig.period(level) * 1000;
                    if (i % precision == 0){

                        List<MetricObject> metricObjects = new ArrayList<MetricObject>();

                        for (int j = 0; j <= 100; j++) {
                            MetricObject metricObject = MetricObject.named("METRIC_TEST_" + level + "_" + j).withLevel(level)
                                    .withTimestamp(timestamp).withType(MetricType.DELTA).withValue(baseTimestamp + i).build();
                            metricObjects.add(metricObject);
                        }

                        result.put(level, metricObjects);
                    }

                }

                 appender.append(timestamp, result, lastUpdateTime);

            }


        }

        appender.close();

        service.build(cache, diskServer, logDescriptionManager);
    }

    public void dataInCache(){

    }

    public void dataInCurrentOneDay() {

        long timstamp = FigureUtil.getTodayStartTimestamp(System.currentTimeMillis());

        long startTime = timstamp + 60000;
        long endTime = startTime + 60000;

        MetricsSearchRequest request = new MetricsSearchRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setLimit(0);
        request.setPrecision(60);

        List<MetricSearch> searchKey = new ArrayList<MetricSearch>();
        for(int i = 0; i <= 0; i++){

            for(MetricLevel level : MetricLevel.values()){
                MetricSearch metricSearch = new MetricSearch("METRIC_TEST_" + level + "_" + i, new HashMap<String, String>());
                searchKey.add(metricSearch);
            }

        }

        request.setQueries(searchKey);

        MetricsSearchResponse response = service.search(request);
        System.out.println(response);
        System.out.println(response.getResult().size());
        assert response.getResult().size() == 84;

    }

    public void dataInPassedDay(){

        long timstamp = FigureUtil.getTodayStartTimestamp(System.currentTimeMillis());
        long startTime = timstamp - 60000;
        long endTime = timstamp + 60000;

        MetricsSearchRequest request = new MetricsSearchRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setLimit(0);
        request.setPrecision(60);

        List<MetricSearch> searchKey = new ArrayList<MetricSearch>();
        for(int i = 0; i <= 0; i++){

            for(MetricLevel level : MetricLevel.values()){
                MetricSearch metricSearch = new MetricSearch("METRIC_TEST_" + level + "_" + i, new HashMap<String, String>());
                searchKey.add(metricSearch);
            }

        }

        request.setQueries(searchKey);

        MetricsSearchResponse response = service.search(request);
        assert(response.getResult().size() == 163);

    }

    public void dataInAnyDay(){

        long timstamp = FigureUtil.getTodayStartTimestamp(System.currentTimeMillis() - 86400000 * 2);
        long startTime = timstamp + 60000;
        long endTime = timstamp + 120000;

        MetricsSearchRequest request = new MetricsSearchRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setLimit(0);
        request.setPrecision(60);

        List<MetricSearch> searchKey = new ArrayList<MetricSearch>();
        for(int i = 0; i <= 0; i++){

            for(MetricLevel level : MetricLevel.values()){
                MetricSearch metricSearch = new MetricSearch("METRIC_TEST_" + level + "_" + i, new HashMap<String, String>());
                searchKey.add(metricSearch);
            }

        }

        request.setQueries(searchKey);

        MetricsSearchResponse response = service.search(request);
        assert(response.getResult().size() == 84);

    }

    public void hasNoData(){

        long timstamp = FigureUtil.getTodayStartTimestamp(System.currentTimeMillis() - 86400000 * 3);
        long startTime = timstamp + 60000;
        long endTime = timstamp + 120000;

        MetricsSearchRequest request = new MetricsSearchRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setLimit(0);
        request.setPrecision(60);

        List<MetricSearch> searchKey = new ArrayList<MetricSearch>();
        for(int i = 0; i <= 0; i++){

            for(MetricLevel level : MetricLevel.values()){
                MetricSearch metricSearch = new MetricSearch("METRIC_TEST_" + level + "_" + i, new HashMap<String, String>());
                searchKey.add(metricSearch);
            }

        }

        MetricsSearchResponse response = service.search(request);
        assert response.getResult().size() == 0;
    }

    public void clear(){
        File file = new File(FileUtil.getBasePath(logRootPath));
        FileUtil.deleteDir(file);
    }

}
