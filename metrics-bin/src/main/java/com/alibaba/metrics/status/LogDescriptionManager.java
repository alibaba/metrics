package com.alibaba.metrics.status;

import com.alibaba.fastjson.JSON;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.StringUtils;
import com.alibaba.metrics.reporter.bin.AbstractFileBackend;
import com.alibaba.metrics.reporter.bin.ChannelFileBackend;
import com.alibaba.metrics.reporter.bin.DataSource;
import com.alibaba.metrics.reporter.bin.IndexData;
import com.alibaba.metrics.reporter.bin.IndexFile;
import com.alibaba.metrics.utils.Constants;
import com.alibaba.metrics.utils.FigureUtil;
import com.alibaba.metrics.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.alibaba.metrics.utils.Constants.LINE_FEED_SEPARATOR;
import static com.alibaba.metrics.utils.Constants.LONG_LENGTH;
import static com.alibaba.metrics.utils.Constants.SITUATION_GROUP;

public class LogDescriptionManager {

    private static final Logger logger = LoggerFactory.getLogger(LogDescriptionManager.class);

    // private static LogDescriptionManager logDescriptionManager;

    private static String path;

    /** 保存datasource描述信息和索引信息的引用 */
    private Map<Long, LogDescriptionRegister> descriptions = new ConcurrentHashMap<Long, LogDescriptionRegister>();

    private Counter metaTimeout = MetricManager.getCounter(SITUATION_GROUP,
            new MetricName("middleware.metrics.cache.meta_timeout"));

    private Counter metaAccess = MetricManager.getCounter(SITUATION_GROUP,
            new MetricName("middleware.metrics.cache.meta_access"));

    private Compass metaLoad = MetricManager.getCompass("self-statistics",
            new MetricName("middleware.metrics.cache.meta_load"));

    private Map<MetricLevel, Long> lastCollectionTime = new HashMap<MetricLevel, Long>() {
        {
            long currentTime = System.currentTimeMillis();
            put(MetricLevel.CRITICAL, currentTime);
            put(MetricLevel.MAJOR, currentTime);
            put(MetricLevel.MINOR, currentTime);
            put(MetricLevel.NORMAL, currentTime);
            put(MetricLevel.TRIVIAL, currentTime);
        }
    };

    private int cacheTime = Constants.META_CACHE_TIME * 1000;

    private final ScheduledExecutorService executor;

    public LogDescriptionManager(String path) {
        this.executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("LogDescriptionManager"));
        this.path = path;
    }

    public void start(long delay, int cacheTime, TimeUnit unit) {
        this.cacheTime = (int) unit.toMillis(cacheTime);
        executor.scheduleWithFixedDelay(task, delay, this.cacheTime, TimeUnit.MILLISECONDS);
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {

            try {
                long currentTimestamp = System.currentTimeMillis();
                long checkTimestamp = FigureUtil.getTodayStartTimestamp(currentTimestamp);

                for (Long describeTime : descriptions.keySet()) {

                    if (describeTime == checkTimestamp) {
                        continue;
                    }

                    long visitTime = descriptions.get(describeTime).getVisitTime();

                    if (visitTime + cacheTime < currentTimestamp) {
                        descriptions.remove(describeTime);
                        metaTimeout.inc();
                        logger.debug("remove from descriptions, key is {}", checkTimestamp);
                    }

                }
            } catch (Exception e) {
                logger.error("LogDescriptionManager error", e);
            }

        }
    };

    public LogDescriptionRegister getLogDescriptions(long baseTimestamp) {

        LogDescriptionRegister register = descriptions.get(baseTimestamp);
        metaAccess.inc();

        if (register == null) {
            register = readRegister(baseTimestamp);
        }

        register.setVisitTime(System.currentTimeMillis());

        return register;
    }

    public void setLogDescriptions(long baseTimestamp, LogDescriptionRegister register) {
        register.setVisitTime(System.currentTimeMillis());
        descriptions.put(baseTimestamp, register);
    }

    public long getLastCollectionTime(MetricLevel level) {
        return lastCollectionTime.get(level);
    }

    public void setLastCollectionTime(MetricLevel level, long lastCollectionTime) {
        this.lastCollectionTime.put(level, lastCollectionTime);
    }

    // public static LogDescriptionManager getInstance() {
    //
    // if (logDescriptionManager == null) {
    // logDescriptionManager = new LogDescriptionManager(path);
    // }
    //
    // return logDescriptionManager;
    // }

    public LogDescriptionRegister readDataSourceFile(LogDescriptionRegister register, String dataSourceFileName)
            throws IOException {

        File file = new File(dataSourceFileName);

        if (!file.exists()) {
            return register;
        }

        AbstractFileBackend backend = new ChannelFileBackend(dataSourceFileName, true);

        byte[] dataSourceBytes = backend.readAll();

        try {
            dataSourceBytes = backend.readAll();
        } catch (IOException e) {
            throw e;
        } finally {
            if (backend != null) {
                backend.close();
            }
        }

        String[] dataSources = new String(dataSourceBytes).split(LINE_FEED_SEPARATOR);

        if (dataSources == null || dataSources.length == 0) {
            return register;
        }

        int currentOffset = 0;
        int currentOrder = 0;

        int maxOffset = 0;
        int maxOrder = 0;
        int dataSourceNum = 0;

        for (String s : dataSources) {
            try {

                if (StringUtils.isBlank(s)) {
                    continue;
                }

                DataSource dataSource = null;

                try {
                    dataSource = (DataSource) JSON.parseObject(s, DataSource.class);
                } catch (Exception e) {
                    logger.error("Error datasource json {} !", s, e);
                }

                if (dataSource == null) {
                    continue;
                }

                dataSource.addMetricObject();

                if (dataSource.getOrder() > maxOrder) {
                    maxOrder = dataSource.getOrder();
                }

                if (dataSource.getOffset() >= maxOffset) {
                    maxOffset = dataSource.getOffset();
                }

                dataSourceNum = dataSourceNum + 1;

                register.addDataSources(dataSource.getMetricObject(), dataSource);

            } catch (Exception e) {
                logger.error("Error file path {}, !", dataSourceFileName, e);
            }

        }

        if (dataSourceNum > 0) {
            currentOffset = maxOffset + LONG_LENGTH;
            currentOrder = maxOrder + 1;
        }

        register.setCurrentOffset(currentOffset);
        register.setCurrentOrder(currentOrder);

        return register;
    }

    public void clear() {
        descriptions.clear();
    }

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

    private synchronized LogDescriptionRegister readRegister(long baseTimestamp) {

        LogDescriptionRegister register = descriptions.get(baseTimestamp);

        if (register == null) {
            register = new LogDescriptionRegister();
        } else {
            return register;
        }

        long readDiskStart = System.currentTimeMillis();

        register = new LogDescriptionRegister();

        for (MetricLevel level : MetricLevel.values()) {

            String dataSourceFileName = FileUtil.getDataSourceFileName(baseTimestamp, path, level);
            String indexFileName = FileUtil.getIndexFileName(baseTimestamp, path, level);

            try {
                readDataSourceFile(register, dataSourceFileName);
            } catch (IOException e) {
                logger.error("Read file {} fail!", dataSourceFileName);
                break;
            }

        }
        long readDiskEnd = System.currentTimeMillis();

        setLogDescriptions(baseTimestamp, register);

        metaLoad.time().success();
        metaLoad.update(readDiskEnd - readDiskStart, TimeUnit.MILLISECONDS);

        return register;
    }

    public int getSize() {
        return descriptions.size();
    }

    public Map<Long, IndexData> getIndexFromDisk(long diskStartTime, long diskEndTime, long baseTimestamp,
            MetricLevel level) {

        Map<Long, IndexData> result = null;

        String indexFileName = FileUtil.getIndexFileName(baseTimestamp, path, level);

        IndexFile indexFile = new IndexFile(indexFileName, level);

        try {
            indexFile.init(true);
        } catch (Exception e) {
            logger.error("Init indexFile {} error when searching!", indexFileName);
            return result;
        }

        try {
            result = indexFile.read(diskStartTime, diskEndTime);
        } catch (Throwable e) {
            logger.error("Read indexfile error!", e);
        } finally {
            try {
                indexFile.close();
            } catch (IOException e) {
                logger.error("Close indexFile {} failed!", indexFileName);
            }
        }

        return result;
    }

}
