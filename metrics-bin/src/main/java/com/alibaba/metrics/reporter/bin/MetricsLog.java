package com.alibaba.metrics.reporter.bin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.reporter.bin.zigzag.LongDZBP;
import com.alibaba.metrics.server.MetricsMemoryCache;
import com.alibaba.metrics.status.LogDescriptionManager;
import com.alibaba.metrics.status.LogDescriptionRegister;
import com.alibaba.metrics.utils.Constants;
import com.alibaba.metrics.utils.FigureUtil;

import static com.alibaba.metrics.utils.Constants.*;
import static com.alibaba.metrics.utils.FigureUtil.*;

public class MetricsLog {
	
	private final static Logger logger = LoggerFactory.getLogger(MetricsLog.class);

    private MetricsMemoryCache cache;

    private LogDescriptionRegister register;

    private LogDescriptionManager logDescriptionManager;

    public DataSourceFile dataSourceDescribe;

    public IndexFile indexDescribe;

    private LogFile logDescribe;

    private String dataSourceFileName;

    private String indexFileName;

    private String logFileName;

    private String basePath;
    
    private long baseTimestamp;

    private MetricLevel level;

    private Map<MetricObject, DataSource> dataSourceObjects = new HashMap<MetricObject, DataSource>();

    private byte[] dataSourceBlock;

    private long[] dataBlock;

    private int dataBlockIndex = 0;

    private int dataSourceBlockIndex = 0;

    private int dataSourceBlockSize = 4096;

    public MetricsLog(MetricLevel level, LogDescriptionRegister register, LogDescriptionManager logDescriptionManager,
            MetricsMemoryCache cache, String basePath, String dataSourceFileName, String indexFileName,
            String logFileName, long baseTimestamp) {
        this.level = level;
        this.basePath = basePath;
        this.dataSourceFileName = dataSourceFileName;
        this.indexFileName = indexFileName;
        this.logFileName = logFileName;
        this.register = register;
        this.logDescriptionManager = logDescriptionManager;
        this.cache = cache;
        this.baseTimestamp = baseTimestamp;
    }

    public void init() {

        File baseDir = new File(basePath);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        File dataSourceFile = new File(dataSourceFileName);
        File indexFile = new File(indexFileName);
        File dataFile = new File(logFileName);

        dataSourceDescribe = new DataSourceFile(dataSourceFileName, level);
        indexDescribe = new IndexFile(indexFileName, level);
        logDescribe = new LogFile(logFileName, level);

        if (dataSourceFile.exists() && indexFile.exists() && dataFile.exists()) {
            try {
                
                dataSourceDescribe.init();
                indexDescribe.init();
                logDescribe.init();
                
                dataSourceDescribe.read(register);
                //indexDescribe.read(register, logDescriptionManager);
                
            } catch (IOException e) {
                logger.error("Init metricslog error when files exist", e.fillInStackTrace());
            }

        } else {
            try {

                dataSourceDescribe.create();
                indexDescribe.create();
                logDescribe.create();

            } catch (IOException e) {
                logger.error("Init metricslog error when files not exist", e.fillInStackTrace());
            }

        }
    }

    public void write(long metricsTime, List<MetricObject> metrics) throws IOException {

        int offset = dataSourceDescribe.getCurrentOffset();
        int order = dataSourceDescribe.getCurrentOrder();

        int dataStart = (int) logDescribe.getPosition();
        int dataEnd = 0;

        for (MetricObject metricObject : metrics) {

            DataSource dataSource = register.getDataSource(metricObject);

            if (dataSource == null) {
                dataSource = new DataSource(metricObject, this.level, offset, order, metricsTime, metricObject.getMeterName());
                dataSourceObjects.put(metricObject, dataSource);
                order = order + 1;
                offset = offset + LONG_LENGTH;
            }

        }

        int length = order;
        dataBlock = new long[length];

        for (int i = 0; i < length; i++) {
            dataBlock[i] = Constants.VALUE_STATUS_NAN;
        }

        for (MetricObject metricObject : metrics) {
            
            Object o = metricObject.getValue();
            long value = convertToLong(o);

            DataSource dataSource = register.getDataSource(metricObject);

            if (dataSource == null) {
                dataSource = dataSourceObjects.get(metricObject);
            }

            dataBlock[dataSource.getOrder()] = value;
            dataBlockIndex = dataBlockIndex + 1;

        }

        dataSourceBlock = new byte[dataSourceBlockSize];

        for (DataSource dataSource : dataSourceObjects.values()) {
            addDataSourceBytes(dataSource);
        }

        try {
            // 写数据
            byte[] data = LongDZBP.toBytes(dataBlock);
            logDescribe.write(metricsTime, data);

            // 写索引
            dataEnd = dataStart + data.length;
            indexDescribe.write(metricsTime, dataStart, dataEnd);

            // 写字段配置
            if (dataSourceObjects != null && dataSourceObjects.size() > 0) {
                dataSourceDescribe.write(metricsTime, dataSourceBlock, dataSourceObjects, dataSourceBlockIndex, offset);
            }

            // 写到缓存中
            cache.add(level, metricsTime, dataBlock);

            // 写内存状态
            variableEffective(offset, order);
            //register.addIndex(metricsTime, level, dataStart, dataEnd);

        } catch (IOException e) {
            throw e;
        } finally {
            statusClear();
        }

    }

    public void addDataSourceBytes(DataSource dataSource) {

        byte[] dataSourceBytes = dataSource.toJsonBytes();

        int oldLength = dataSourceBlock.length;
        int newLength = oldLength;

        while (dataSourceBlockIndex + dataSourceBytes.length + 1 > newLength) {
            newLength = newLength * 2;
        }

        if (newLength > oldLength) {
            byte[] newDataSourceBlock = new byte[newLength];
            System.arraycopy(dataSourceBlock, 0, newDataSourceBlock, 0, oldLength);
            dataSourceBlock = newDataSourceBlock;
        }

        System.arraycopy(dataSourceBytes, 0, dataSourceBlock, dataSourceBlockIndex, dataSourceBytes.length);
        dataSourceBlock[dataSourceBlockIndex + dataSourceBytes.length] = LINE_FEED;

        dataSourceBlockIndex = dataSourceBlockIndex + dataSourceBytes.length + 1;

    }

    public void statusClear() {
        dataSourceBlockIndex = 0;
        dataBlockIndex = 0;
        dataSourceObjects.clear();
    }

    public void variableEffective(int offset, int order) {

        dataSourceDescribe.setCurrentOffset(offset);
        dataSourceDescribe.setCurrentOrder(order);
        register.addDataSources(dataSourceObjects);

    }

    public void close() throws IOException {
        logDescribe.close();
        indexDescribe.close();
        dataSourceDescribe.close();
    }

    // public void readLogHead() throws Exception{
    // head = new LogHead();
    //
    // byte[] b = new byte[40];
    // fileBackend.read(0, b);
    //
    // if (signature == null ||
    // !signature.equals(DataSourceHead.HEAD_SIGNATURE)){
    // throw new Exception("错误的文件格式!");
    // }
    //
    // int index = 0;
    //
    // String signature = getString(b, index, 4);
    // index = index + 4;
    //
    // int version = getInt(b, index);
    // index = index + INT_LENGTH;
    //
    // long size = getLong(b, index);
    // index = index + LONG_LENGTH;
    //
    // int level = fileBackend.readInt(position);
    // index = index + INT_LENGTH;
    //
    // long indexStart = fileBackend.readInt(position);
    // index = index + LONG_LENGTH;
    //
    // long dataStart = fileBackend.readInt(position);
    // index = index + LONG_LENGTH;
    //
    // long currentRecord = fileBackend.readInt(position);
    // index = index + LONG_LENGTH;
    //
    // long totalTheoreticRecord = fileBackend.readInt(position);
    // index = index + LONG_LENGTH;
    //
    // long lastUpdateTime = fileBackend.readInt(position);
    // index = index + LONG_LENGTH;
    //
    // }

}
