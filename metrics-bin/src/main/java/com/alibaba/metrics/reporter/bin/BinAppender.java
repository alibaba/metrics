package com.alibaba.metrics.reporter.bin;

import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.server.MetricsMemoryCache;
import com.alibaba.metrics.status.LogDescriptionManager;
import com.alibaba.metrics.status.LogDescriptionRegister;
import com.alibaba.metrics.utils.Constants;
import com.alibaba.metrics.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.alibaba.metrics.utils.Constants.CHECK_OUT_OF_DATE_INTERVAL;
import static com.alibaba.metrics.utils.Constants.CURRENTDATA_REGEX;
import static com.alibaba.metrics.utils.Constants.DATE_PATH_REGEX;
import static com.alibaba.metrics.utils.Constants.INDEX_V2_REGEX;
import static com.alibaba.metrics.utils.FigureUtil.getNextDayStartTimestamp;
import static com.alibaba.metrics.utils.FigureUtil.getTodayStartTimestamp;

public class BinAppender {

    private static final Logger logger = LoggerFactory.getLogger(BinAppender.class);

    private LogDescriptionRegister logDescriptionRegister = new LogDescriptionRegister();

    private ScheduledExecutorService executor = Executors
            .newSingleThreadScheduledExecutor(new NamedThreadFactory("BinAppender"));

    private LogDescriptionManager logDescriptionManager;

    private MetricsMemoryCache cache;

    private Map<MetricLevel, MetricsLog> currentMetricsLog = new HashMap<MetricLevel, MetricsLog>();

    /** 创建时传入的时间戳 */
    private long logTimestamp;

    /** 今日零点时间戳 */
    private long baseTimestamp;

    /** 小于这个时间戳的日志都会被写入今天 */
    private long lastTimestamp;

    private MetricsCollectPeriodConfig metricsCollectPeriodConfig;

    private String logRootPath;

    private int archiveHold;

    private int currentHold;

    private long maxFileSize = Constants.DEFAULT_MAX_FILE_SIZE;

    /** 这个appender是否可用 */
    private AtomicBoolean working = new AtomicBoolean(false);

    private static class NamedThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        private NamedThreadFactory(String name) {
            final SecurityManager s = System.getSecurityManager();
            this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = "metrics-" + name + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            final Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    public BinAppender(long timestamp, String logRootPath, MetricsCollectPeriodConfig metricsCollectPeriodConfig,
            LogDescriptionManager logDescriptionManager, MetricsMemoryCache cache, int archiveHold, int currentHold) {

        this.logTimestamp = timestamp;
        this.baseTimestamp = getTodayStartTimestamp(timestamp);
        this.lastTimestamp = getNextDayStartTimestamp(timestamp);
        this.cache = cache;
        this.logDescriptionManager = logDescriptionManager;

        this.metricsCollectPeriodConfig = metricsCollectPeriodConfig;
        this.logRootPath = logRootPath;
        this.archiveHold = archiveHold;
        this.currentHold = currentHold;

    }

    /**
     * 初始化逻辑，检查磁盘上的文件是否存在，不存在就创建
     */
    public void init() {

        createMetricsLog(baseTimestamp);

        executor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    long timestamp = System.currentTimeMillis();
                    // 检查日志是否要删除
                    checkForRolling(timestamp);
                    // 检查是否有需要归档的
                    checkForArchive();
                } catch (Throwable e) {
                    logger.error("BinAppender schedule task error.", e);
                }
            }

        }, CHECK_OUT_OF_DATE_INTERVAL, CHECK_OUT_OF_DATE_INTERVAL, TimeUnit.SECONDS);
    }

    public void initWithoutCheckThread() {
        createMetricsLog(baseTimestamp);
    }

    public void append(long metricsTime, Map<MetricLevel, List<MetricObject>> metricObjects,
            Map<MetricLevel, Long> lastUpdateTime) {

        if (metricObjects == null || metricObjects.size() == 0) {
            return;
        }

        if (metricsTime >= baseTimestamp) {

            if (metricsTime >= lastTimestamp) {
                changeDay(metricsTime);
            }

            for (MetricLevel level : metricObjects.keySet()) {

                if (level == null) {
                    logger.error("metriclevel in binappender is null");
                    continue;
                }

                if (metricsTime == lastUpdateTime.get(level)) {
                    continue;
                }

                MetricsLog metricsLog = currentMetricsLog.get(level);
                if (metricsLog == null) {
                    // 重新创建逻辑，暂时不加
                    // metricsLog = new MetricsLog(level, logRootPath,
                    // dataSourceFileName, indexFileName, logFileName,
                    // metricsTime)
                }

                try {
                    int interval = metricsCollectPeriodConfig.period(level) * 1000;
                    metricsTime = metricsTime / interval * interval;
                    metricsLog.write(metricsTime, metricObjects.get(level));

                    if (lastUpdateTime.get(level) < metricsTime) {
                        lastUpdateTime.put(level, metricsTime);
                    }

                    if (logDescriptionManager.getLastCollectionTime(level) < metricsTime) {
                        logDescriptionManager.setLastCollectionTime(level, metricsTime);
                    }

                } catch (IOException e) {
                    logger.error("write exception", e);
                }

            }

        } else {
            logger.info("this metrics is yesterday's data, ignore it");
            return;
        }

    }

    public void changeDay(long timestamp) {

        for (Entry<MetricLevel, MetricsLog> entry : currentMetricsLog.entrySet()) {
            try {
                entry.getValue().close();
            } catch (IOException e) {
                logger.error("Close metrics log fail!", e);
            }
        }

        this.logTimestamp = timestamp;
        this.baseTimestamp = getTodayStartTimestamp(timestamp);
        this.lastTimestamp = getNextDayStartTimestamp(timestamp);

        createMetricsLog(this.baseTimestamp);

    }

    private void createMetricsLog(long baseTimestamp) {

        // 创建新的register
        logDescriptionRegister = new LogDescriptionRegister();

        // 新建日志与索引文件
        Map<MetricLevel, String> fileNames = new HashMap<MetricLevel, String>();

        for (MetricLevel level : MetricLevel.values()) {

            String basePath = FileUtil.getMetricsDir(baseTimestamp, logRootPath);
            String dataSourceFileName = FileUtil.getDataSourceFileName(baseTimestamp, logRootPath,
                    level);
            String indexFileName = FileUtil.getIndexFileName(baseTimestamp, logRootPath, level);
            String logFileName = FileUtil.getLogFileName(baseTimestamp, logRootPath, level);

            MetricsLog metricsLog = new MetricsLog(level, logDescriptionRegister, logDescriptionManager, cache,
                    basePath, dataSourceFileName, indexFileName, logFileName, baseTimestamp);
            metricsLog.init();

            currentMetricsLog.put(level, metricsLog);
            fileNames.put(level, logFileName);

        }

        logDescriptionManager.setLogDescriptions(baseTimestamp, logDescriptionRegister);

    }

    private void writeBackendClose() throws IOException {

        if (currentMetricsLog == null) {
            return;
        }

        for (MetricLevel level : currentMetricsLog.keySet()) {
            MetricsLog metricsLog = currentMetricsLog.get(level);
            metricsLog.close();
        }
    }

    private void checkForRolling(long timestamp) {

        Date date = new Date(timestamp);

        Date archiveHoldDate = new Date(date.getYear(), date.getMonth(), date.getDate() - archiveHold);
        Date currentHoldDate = new Date(date.getYear(), date.getMonth(), date.getDate() - currentHold);

        String pathForScan = FileUtil.getBasePath(logRootPath);

        logger.debug(
                "Check and delete unnecessary file, path is {}... Current time is {}, we will delete archive data before {} and current data before {}",
                pathForScan, timestamp, archiveHoldDate.toString(), currentHoldDate.toString());

        File path = new File(pathForScan);

        if (path.exists()) {

            File[] files = path.listFiles();
            for (File file : files) {

                if (file.isDirectory()) {

                    String dir = file.getName();

                    if (DATE_PATH_REGEX.matcher(dir).matches()) {

                        String[] times = dir.split(FileUtil.DATE_SPLIT);
                        Date fileDate = new Date(Integer.parseInt(times[0]) - 1900, Integer.parseInt(times[1]) - 1,
                                Integer.parseInt(times[2]));

                        if (fileDate.before(archiveHoldDate)) {
                            boolean result = FileUtil.deleteDir(file);
                            logger.info("delete directory {},{}", file.getName(), result);
                            continue;
                        }

                        if (fileDate.before(currentHoldDate)) {
                            File[] childrens = file.listFiles();
                            for (File child : childrens) {

                                if (child.isDirectory()) {
                                    boolean result = FileUtil.deleteDir(child);
                                    logger.info("delete directory {},{}", child.getName(), result);
                                    continue;
                                }

                                if (CURRENTDATA_REGEX.matcher(child.getName()).matches()) {
                                    boolean result = child.delete();
                                    logger.info("delete file {},{}", child.getName(), result);
                                    continue;
                                }

                                if (INDEX_V2_REGEX.matcher(child.getName()).matches()) {
                                    boolean result = child.delete();
                                    logger.info("delete file {},{}", child.getName(), result);
                                    continue;
                                }

                            }
                        }

                    } else {
                        boolean result = FileUtil.deleteDir(file);
                        logger.info("delete directory {},{}", file.getName(), result);
                    }
                } else {
                    boolean result = file.delete();
                    logger.info("delete file {},{}", file.getName(), result);
                }
            }

        }

    }

    private void checkForArchive() {

    }

    public void close() {
        try {

            executor.shutdown();
            writeBackendClose();

        } catch (IOException e) {
            logger.error("Close binappender failed!", e.fillInStackTrace());
        }
    }

    public String getPath(){
        return logRootPath;
    }

    protected void flush() {

    }

}
