package com.alibaba.metrics.server;

import static com.alibaba.metrics.utils.Constants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.ReservoirType;
import com.alibaba.metrics.bean.MetricResult;
import com.alibaba.metrics.bean.MetricSearch;
import com.alibaba.metrics.bean.MetricSource;
import com.alibaba.metrics.bean.MetricsDataStatus;
import com.alibaba.metrics.bean.MetricsRecordStatus;
import com.alibaba.metrics.bean.MetricsSearchRequest;
import com.alibaba.metrics.bean.MetricsSearchResponse;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.bin.DataSource;
import com.alibaba.metrics.reporter.bin.IndexData;
import com.alibaba.metrics.status.LogDescriptionManager;
import com.alibaba.metrics.status.LogDescriptionRegister;
import com.alibaba.metrics.utils.Constants;
import com.alibaba.metrics.utils.FigureUtil;

/**
 *          服务实现，提供整体流程的调用以及流量控制等功能，不包含具体的数据查询有关的实现
 *
 */
public class MetricsSearchService {

    private final static Logger logger = LoggerFactory.getLogger(MetricsSearchService.class);

    private static LogDescriptionManager logDescriptionManager;

    private static MetricsSearchService instance;

    private static MetricsMemoryCache cache;

    private static MetricsOnDisk disk;

    private MetricsCollectPeriodConfig metricsCollectPeriodConfig = new MetricsCollectPeriodConfig();

    private static int MAX_SEARCH_INTERVAL = Constants.DEFAULT_MAX_SEARCH_INTERVAL;

    private Counter cacheHit = MetricManager.getCounter(SITUATION_GROUP,
            new MetricName("middleware.metrics.cache.hit"));

    private Counter cachePartsHit = MetricManager.getCounter(SITUATION_GROUP,
            new MetricName("middleware.metrics.cache.parts_hit"));

    private Counter cacheMiss = MetricManager.getCounter(SITUATION_GROUP,
            new MetricName("middleware.metrics.cache.miss"));

    private Compass dataRead = MetricManager.getCompass(SITUATION_GROUP,
            new MetricName("middleware.metrics.cache.data_read"), ReservoirType.BUCKET);

    private Counter currentDayAccess = MetricManager.getCounter(SITUATION_GROUP,
            new MetricName("middleware.metrics.cache.current_access"));

    private Counter passedDayAccess = MetricManager.getCounter(SITUATION_GROUP,
            new MetricName("middleware.metrics.cache.passed_access"));

    private Counter dataCollected = MetricManager.getCounter(SITUATION_GROUP,
            new MetricName("middleware.metrics.cache.data_collected"));

    private Counter dataUnfinished = MetricManager.getCounter(SITUATION_GROUP,
            new MetricName("middleware.metrics.cache.data_unfinished"));

    public MetricsSearchService() {

    }

    public static void build(MetricsMemoryCache cache, MetricsOnDisk disk,
            LogDescriptionManager logDescriptionManager) {
        MetricsSearchService.cache = cache;
        MetricsSearchService.disk = disk;
        MetricsSearchService.logDescriptionManager = logDescriptionManager;
    }

    public String search(String metricsSearch) {
        logger.info(metricsSearch);
        String result = "";

        try {
            MetricsSearchRequest request = JSON.parseObject(metricsSearch, MetricsSearchRequest.class);
            MetricsSearchResponse response = search(request);
            result = JSON.toJSONString(response, SerializerFeature.DisableCircularReferenceDetect);
        } catch (Exception e) {
            logger.error("An error occur in searching! Search json is: " + metricsSearch, e);
        }

        return result;
    }

    public MetricsSearchResponse search(MetricsSearchRequest request) {

        long startTime = request.getStartTime();
        long endTime = request.getEndTime();
        int limit = request.getLimit();
        int precision = request.getPrecision();
        List<MetricSearch> metricNames = request.getQueries();
        boolean zeroIgnore = request.isZeroIgnore();

        long currentTimeStamp = System.currentTimeMillis();
        long todayBaseTime = FigureUtil.getTodayStartTimestamp(currentTimeStamp);

        MetricsSearchResponse response = new MetricsSearchResponse();
        List<MetricResult> resultList = new ArrayList<MetricResult>();

        MetricSource source = request.getSource();

        Map<MetricLevel, SortedMap<Long, List<MetricResult>>> results = distinguishSearch(startTime, endTime,
                currentTimeStamp, precision, metricNames, source);

        long range = endTime - startTime;

         if (range > Constants.DAY_MILLISECONDS) {
             startTime = endTime - Constants.DAY_MILLISECONDS;
             logger.warn("The time range of this search is too large, reset starttime to {}", startTime);
         }

         response.setStartTime(startTime);
         response.setEndTime(endTime);

        for (Entry<MetricLevel, SortedMap<Long, List<MetricResult>>> entry : results.entrySet()) {

            SortedMap<Long, List<MetricResult>> classifiedResult = entry.getValue();

            if (classifiedResult != null && classifiedResult.size() > 0) {
                for (Entry<Long, List<MetricResult>> entry1 : classifiedResult.entrySet()) {
                    List<MetricResult> metricResults = entry1.getValue();
                    if (metricResults != null && metricResults.size() > 0) {
                        resultList.addAll(metricResults);
                    }
                }
            }
        }

        boolean collected = checkFinishStatus(results, endTime);

        if (collected) {
            response.setDataStatus(MetricsDataStatus.COLLECTED);
            dataCollected.inc();
        } else {
            response.setDataStatus(MetricsDataStatus.UNFINISHED);
            dataUnfinished.inc();
        }

        if (limit > 0) {
            if (resultList.size() > limit) {
                response.setResult(resultList.subList(0, limit - 1));
                response.setRecordStatus(MetricsRecordStatus.LIMITED);
            } else {
                response.setResult(resultList);
                response.setRecordStatus(MetricsRecordStatus.ENTIRE);
            }
        } else {
            response.setResult(resultList);
            response.setRecordStatus(MetricsRecordStatus.ENTIRE);
        }

        if (zeroIgnore){
            List<MetricResult> allResultList = resultList;
            resultList = new ArrayList<MetricResult>();
            for(MetricResult result : allResultList){
                if (!FigureUtil.checkZero(result.getValue())){
                    resultList.add(result);
                }
            }

            response.setResult(resultList);
        }

        return response;
    }

    private Map<MetricLevel, SortedMap<Long, List<MetricResult>>> distinguishSearch(long startTime, long endTime,
            long currentTimeStamp, int precision, List<MetricSearch> metricNames, MetricSource source) {

        if (source == MetricSource.CURRENT) {

        } else if (source == MetricSource.ARCHIVE) {

        }

        Map<MetricLevel, SortedMap<Long, List<MetricResult>>> results = new HashMap<MetricLevel, SortedMap<Long, List<MetricResult>>>();

        long cacheUpperBound = currentTimeStamp / 1000 * 1000 - Constants.DATA_CACHE_TIME * 1000 * 2 + 1000;

        long cacheStartTime = 0;

        if (cacheUpperBound < startTime){
            cacheStartTime = startTime;
        }else{
            cacheStartTime = cacheUpperBound;
        }

        long cacheEndTime = endTime;

        if (cacheUpperBound < endTime) {

            // 有数据在缓存中

            long todayBaseTime = FigureUtil.getTodayStartTimestamp(currentTimeStamp);

            if (todayBaseTime <= startTime && todayBaseTime <= endTime) {

                currentDayAccess.inc();
                LogDescriptionRegister logDescribeToday = logDescriptionManager.getLogDescriptions(todayBaseTime);

                // 全部数据都是当天的数据, 只查询当天的datasource缓存
                Map<MetricLevel, Set<DataSource>> dataSources = logDescribeToday.getMetricNames(metricNames);
                results = cache.getDataFromCache(results, dataSources, cacheStartTime, cacheEndTime, precision);

            } else if (todayBaseTime > startTime && todayBaseTime < endTime) {
                passedDayAccess.inc();
                // 数据跨天
                LogDescriptionRegister logDescribeToday = logDescriptionManager.getLogDescriptions(todayBaseTime);
                Map<MetricLevel, Set<DataSource>> dataSourcesToday = logDescribeToday.getMetricNames(metricNames);

                results = cache.getDataFromCache(results, dataSourcesToday, todayBaseTime, cacheEndTime, precision);

                LogDescriptionRegister logDescribeYesterday = logDescriptionManager
                        .getLogDescriptions(todayBaseTime - Constants.DAY_MILLISECONDS);

                Map<MetricLevel, Set<DataSource>> dataSourcesYesterday = logDescribeYesterday
                        .getMetricNames(metricNames);
                results = cache.getDataFromCache(results, dataSourcesYesterday, cacheStartTime, todayBaseTime - 1000,
                        precision);

            } else if (startTime < todayBaseTime && todayBaseTime < endTime) {
                passedDayAccess.inc();
                // 数据都是昨天的
                LogDescriptionRegister logDescribeYesterday = logDescriptionManager
                        .getLogDescriptions(todayBaseTime - Constants.DAY_MILLISECONDS);
                Map<MetricLevel, Set<DataSource>> dataSourcesYesterday = logDescribeYesterday
                        .getMetricNames(metricNames);

                // endtime-1000，让时间戳保持在前一天
                results = cache.getDataFromCache(results, dataSourcesYesterday, cacheStartTime, todayBaseTime - 1000,
                        precision);

            } else {
                // donothing
            }

            if (startTime >= cacheUpperBound) {
                // 全部数据都在缓存中
                cacheHit.inc();
                return results;
            } else {
                cachePartsHit.inc();
            }

        } else {
            cacheMiss.inc();
        }

        // 读取磁盘中的部分数据

        long remainingEndTime = 0;

        if (endTime < cacheUpperBound) {
            remainingEndTime = endTime;
        } else {
            remainingEndTime = cacheUpperBound;
        }

        List<Long> timeSplit = FigureUtil.splitRangeByDay(startTime, remainingEndTime);
        int length = timeSplit.size() / 2;

        if (length > MAX_SEARCH_INTERVAL){
            logger.warn("This query crosses {} days, exceed limit {}", length, MAX_SEARCH_INTERVAL);
            length = MAX_SEARCH_INTERVAL;
        }

        for (int i = 0; i < length; i++) {

            long diskStartTime = timeSplit.get(2 * i);
            long diskEndTime = timeSplit.get(2 * i + 1);

            long baseTimestamp = FigureUtil.getTodayStartTimestamp(diskStartTime);

            LogDescriptionRegister logDescribeThisDay = logDescriptionManager.getLogDescriptions(baseTimestamp);

            Map<MetricLevel, Set<DataSource>> dataSourcesThisDay = logDescribeThisDay.getMetricNames(metricNames);

            if (dataSourcesThisDay == null || dataSourcesThisDay.size() == 0) {
                continue;
            }

            for (MetricLevel level : MetricLevel.values()) {
                Map<Long, IndexData> indexs = logDescriptionManager.getIndexFromDisk(diskStartTime, diskEndTime,
                        baseTimestamp, level);

                if (indexs == null || indexs.size() == 0) {
                    continue;
                }

                Set<DataSource> dataSources = dataSourcesThisDay.get(level);

                if (dataSources == null || dataSources.size() == 0) {
                    continue;
                }

                long dataReadStart = System.currentTimeMillis();
                results = disk.getDataFromDisk(precision, level, dataSources, indexs, results);
                long dataReadEnd = System.currentTimeMillis();

                dataRead.update(dataReadEnd - dataReadStart, TimeUnit.MILLISECONDS);
            }

        }

        return results;

    }

    public final static MetricsSearchService getInstance() {

        if (instance == null) {
            instance = new MetricsSearchService();
        }
        return instance;
    }

    public boolean checkFinishStatus(Map<MetricLevel, SortedMap<Long, List<MetricResult>>> results, long endTime) {

        boolean collected = true;

        for (MetricLevel level : results.keySet()) {
            int interval = metricsCollectPeriodConfig.period(level) * 1000;
            long lastCollectionTime = logDescriptionManager.getLastCollectionTime(level);
            if (endTime >= lastCollectionTime + interval) {
                collected = false;
                break;
            }
        }
        return collected;
    }

}
