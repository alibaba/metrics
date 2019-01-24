package com.alibaba.metrics.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.alibaba.metrics.Compass;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.bean.MetricResult;
import com.alibaba.metrics.bean.MetricSearch;
import com.alibaba.metrics.bean.MetricsSearchResponse;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.bin.DataSource;
import com.alibaba.metrics.reporter.bin.LogStatusManager;
import com.alibaba.metrics.status.LogDescriptionManager;
import com.alibaba.metrics.utils.Constants;
import com.alibaba.metrics.utils.FIFOMap;
import com.alibaba.metrics.utils.FigureUtil;

public class MetricsMemoryCache extends MetricsDataStore {

    private Map<MetricLevel, DataInfo> cachedMetrics = new HashMap<MetricLevel, DataInfo>();

    private int size = 10;

    private LogDescriptionManager logDescriptionManager;

    private MetricsCollectPeriodConfig metricsCollectPeriodConfig = new MetricsCollectPeriodConfig();

    private long startTime;

    private long endTime;

    public MetricsMemoryCache(int size, LogDescriptionManager logDescriptionManager) {
        this(size);
        this.logDescriptionManager = logDescriptionManager;
    }

    public MetricsMemoryCache(int size){
        this.size = size;
        for (MetricLevel level : MetricLevel.values()) {
            int absoluteSize = size / metricsCollectPeriodConfig.period(level) * 2 - 1;
            cachedMetrics.put(level, new DataInfo(absoluteSize));
        }
    }

    public void add(MetricLevel level, long timestamp, long[] dataBlock) {

        DataInfo cache = cachedMetrics.get(level);
        cache.add(timestamp, dataBlock);

        if (timestamp > endTime) {
            endTime = timestamp;
        }

    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public Map<MetricLevel, SortedMap<Long, List<MetricResult>>> getDataFromCache(
            Map<MetricLevel, SortedMap<Long, List<MetricResult>>> result, Map<MetricLevel, Set<DataSource>> dataSources,
            long startTime, long endTime, int precision) {

        if (dataSources == null || dataSources.size() == 0){
            return result;
        }

        for (Entry<MetricLevel, Set<DataSource>> entry : dataSources.entrySet()) {

            MetricLevel level = entry.getKey();
            Set<DataSource> dataSourceSet = entry.getValue();

            DataInfo info = cachedMetrics.get(level);

            SortedMap<Long, BasicLongArray> timeSeries = info.getDataMap().subMap(startTime, endTime);

            SortedMap<Long, List<MetricResult>> resultList = result.get(level);

            if (resultList == null) {
                SortedMap<Long, List<MetricResult>> newMap = new TreeMap<Long, List<MetricResult>>();
                result.put(level, newMap);
                resultList = newMap;
            }

            for (Entry<Long, BasicLongArray> entry1 : timeSeries.entrySet()) {

                long key = entry1.getKey();

                List<MetricResult> tempResult = resultList.get(key);

                tempResult = createMetricsResult(dataSourceSet, entry1.getValue().array, key, level, precision, tempResult);

                resultList.put(key, tempResult);
            }

            result.put(level, resultList);

        }

        return result;
    }
}

class DataInfo {

    private FIFOMap<Long, BasicLongArray> dataMap;

    private int size = 0;

    public DataInfo(int size) {
        this.size = size;
        dataMap = new FIFOMap<Long, BasicLongArray>(size);
    }

    public void add(long timestamp, long[] data) {

//        if (dataMap.size() == size){
//            dataMap.remove(dataMap.firstKey());
//        }

        dataMap.put(timestamp, new BasicLongArray(data));

    }

    // public long getStartTime() {
    // return startTime;
    // }
    //
    // public long getEndTime() {
    // return endTime;
    // }

    public FIFOMap<Long, BasicLongArray> getDataMap() {
        return dataMap;
    }
}

class BasicLongArray{

    long[] array;

    BasicLongArray(long[] array){
        this.array = array;
    }

}

