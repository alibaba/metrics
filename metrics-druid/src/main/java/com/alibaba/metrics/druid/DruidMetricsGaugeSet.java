package com.alibaba.metrics.druid;

import com.alibaba.metrics.CachedMetricSet;
import com.alibaba.metrics.Clock;
import com.alibaba.metrics.DynamicMetricSet;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.PersistentGauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularDataSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This gauge set is used to collect <a href="https://github.com/alibaba/druid">Druid</a> related metrics.
 * See https://github.com/alibaba/druid/wiki/StatFilter_Items for more details.
 * The JdbcStatManager is responsible for collection of jdbc stat, e.g. sql stats.
 * However, it is not exposed as Mbean directly.
 * A work around is to call com.alibaba.druid.stat.JdbcStatManager#getInstance using reflection.
 * Currently we only collect numerical stats.
 */
public class DruidMetricsGaugeSet extends CachedMetricSet implements DynamicMetricSet {

    private static final String JDBC_STAT_MANAGER = "com.alibaba.druid.stat.JdbcStatManager";

    // we calculate delta value for these counters
    private static final String[] DruidMetricsCounters = {
            "ExecuteCount", "ErrorCount", "TotalTime", "EffectedRowCount", "FetchRowCount",
            "InTransactionCount", "ResultSetHoldTime", "ExecuteAndResultSetHoldTime",
    };

    // we only capture the latest value for these gauges
    private static final String[] DruidMetricsGauges = {
            "MaxTimespan", "ConcurrentMax", "RunningCount", "EffectedRowCountMax", "FetchRowCountMax"
    };

    private static final Logger logger = LoggerFactory.getLogger(DruidMetricsGaugeSet.class);

    private static final int DEFAULT_MAX_SQL_SIZE =
            Integer.getInteger("com.alibaba.metrics.druid.MaxSqlSize", 250);

    // the com.alibaba.druid.stat.JdbcStatManager class
    private Class<?> jdbcStatManagerClass;
    // the com.alibaba.druid.stat.JdbcStatManager instance (singleton)
    private Object jdbcStatManager;
    // the max number of sql that will be recorded
    private final int maxSqlSize;
    // sqlData contains the latest snapshot retrieved from Druid, including counter and gauge stats
    private Map<String, long[]> sqlData = new HashMap<String, long[]>();
    // deltaSqlData contains the delta value compares to last snapshot, only for counter stats
    private Map<String, long[]> deltaSqlData = new HashMap<String, long[]>();
    // base metric name
    private MetricName baseName;

    public DruidMetricsGaugeSet(long dataTTL, TimeUnit unit, MetricName baseName) {
        this(dataTTL, unit, Clock.defaultClock(), DEFAULT_MAX_SQL_SIZE, baseName);
    }

    public DruidMetricsGaugeSet(long dataTTL, TimeUnit unit, Clock clock, int maxSqlSize, MetricName baseName) {
        super(dataTTL, unit, clock);
        this.maxSqlSize = maxSqlSize;
        this.baseName = baseName;
        try {
            jdbcStatManagerClass = DruidMetricsGaugeSet.class.getClassLoader().loadClass(JDBC_STAT_MANAGER);
            jdbcStatManager = jdbcStatManagerClass.getMethod("getInstance").invoke(null);
        } catch (Exception e) {
            logger.error("Error during get jdbcStatManager instance: ", e);
        }
    }

    @Override
    protected void getValueInternal() {
        if (jdbcStatManagerClass == null || jdbcStatManager == null) return;
        try {
            // A work around is to call com.alibaba.druid.stat.JdbcStatManager#getInstance using reflection.
            TabularDataSupport tabularData = (TabularDataSupport) jdbcStatManagerClass
                    .getMethod("getSqlList").invoke(jdbcStatManager, null);

            for (Object itemValue: tabularData.values()) {
                if (!(itemValue instanceof CompositeData)) {
                    continue;
                }
                CompositeData compositeData = (CompositeData) itemValue;
                // use sql as tag
                String sql = (String) compositeData.get("SQL");
                if (!sqlData.containsKey(sql)) {
                    if (sqlData.size() >= maxSqlSize) {
                        continue;
                    }
                    sqlData.put(sql, new long[DruidMetricsCounters.length + DruidMetricsGauges.length]);
                    deltaSqlData.put(sql, new long[DruidMetricsCounters.length]);
                }
                long[] data = sqlData.get(sql);
                long[] deltaData = deltaSqlData.get(sql);
                int i = 0;
                for (; i < DruidMetricsCounters.length; i++) {
                    long latest = (Long) compositeData.get(DruidMetricsCounters[i]);
                    deltaData[i] = latest - data[i];
                    data[i] = latest;
                }
                for (; i < DruidMetricsCounters.length + DruidMetricsGauges.length; i++) {
                    data[i] = (Long) compositeData.get(DruidMetricsGauges[i - DruidMetricsCounters.length]);
                }
            }
        } catch (Exception e) {
            logger.error("Error during reading druid stats: ", e);
        }
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        Map<MetricName, Metric> metrics = new HashMap<MetricName, Metric>();
        metrics.put(new MetricName("sql"), this);
        return metrics;
    }

    @Override
    public Map<MetricName, Metric> getDynamicMetrics() {
        refreshIfNecessary();
        Map<MetricName, Metric> metrics = new HashMap<MetricName, Metric>();

        for (Map.Entry<String, long[]> entry: deltaSqlData.entrySet()) {
            int i = 0;
            for (; i < DruidMetricsCounters.length; i++) {
                metrics.put(baseName.resolve(DruidMetricsCounters[i]).tagged("sql", entry.getKey()),
                        new DruidGauge(deltaSqlData, entry.getKey(), i));
            }
            for (; i < DruidMetricsCounters.length + DruidMetricsGauges.length; i++) {
                metrics.put(baseName.resolve(DruidMetricsGauges[i - DruidMetricsCounters.length]).tagged("sql", entry.getKey()),
                        new DruidGauge(sqlData, entry.getKey(), i));
            }
        }
        return metrics;
    }

    private class DruidGauge extends PersistentGauge<Long> {

        private Map<String, long[]> dataSource;
        private String sql;
        private int index;

        DruidGauge(Map<String, long[]> dataSource, String sql, int index) {
            this.dataSource = dataSource;
            this.sql = sql;
            this.index = index;
        }

        @Override
        public Long getValue() {
            refreshIfNecessary();
            return dataSource.get(sql)[index];
        }
    }
}
