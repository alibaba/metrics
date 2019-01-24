package com.alibaba.metrics.utils;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Constants {
	
	public static final String UTF8 = "UTF-8";
	
	public static final boolean DEBUGING = true;
	
	public static final int INT_LENGTH = 4, LONG_LENGTH = 8, DOUBLE_LENGTH = 8, FLOAT_LENGTH = 4;
	
	public static final int TYPE_INT = 0, TYPE_LONG = 1, TYPE_DOUBLE = 2, TYPE_FLOAT = 3;
	
	public static final String FILE_DIR_DAILY_BASE = "log_";
	
	public static final String CURRENT_FILE = "current";
	public static final String ARCHIVE_FILE = "archive";
	
	public static final long LATEST_VERSION = 0;
	
    public static final char CARRIAGE_RETURN = '\r';
    public static final char LINE_FEED= '\n';
    
    public static final String LINE_FEED_SEPARATOR = "\n";
    
    public static final String INDEX_SEPARATOR = ",";
    
    public static final String METRICS_SEPARATOR = new String(new char[]{17});
    public static final String TAGS_SEPARATOR = new String(new char[]{18});
    public static final String TAG_KV_SEPARATOR = new String(new char[]{19});
    
    /** 找了一个不常用的负数值，表示这个点没有值写入 */
    public static final long VALUE_STATUS_NAN = -10001;
    
    /** 匹配日志文件名及目录 */
    public static final Pattern DATE_PATH_REGEX = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    public static final Pattern CURRENTDATA_REGEX = Pattern.compile("currentdata_\\d{4}-\\d{2}-\\d{2}_.*");
    public static final Pattern DATASOURCE_REGEX = Pattern.compile("datasource_\\d{4}-\\d{2}-\\d{2}_.*");
    public static final Pattern INDEX_V2_REGEX = Pattern.compile("index_v2_\\d{4}-\\d{2}-\\d{2}_.*");
    public static final Pattern ARCHIVE_REGEX = Pattern.compile("archive_\\d{4}-\\d{2}-\\d{2}_.*");
    
    /** 日志保存时间,不包含当天 */
    public static final int DEFAULT_ARCHIVE_HOLD = 6;
    public static final int DEFAULT_CURRENT_HOLD = 1;
    
    /** 一天的毫秒数 */
    public static final int DAY_MILLISECONDS = 60 * 60 * 24 * 1000;
    
    /** 一天的秒数 */
    public static final int DAY_SECONDS = 60 * 60 * 24;
    
    /** 校正到东八区的时间差 */
    public static final int UTC_PLUS_8_ADJUST = 60 * 60 * 8 * 1000;
    
    /** 缓存大小,单位秒 */
    public static final int DATA_CACHE_TIME = 60;
    
    /** 从文件中的读取的meta信息缓存时间,单位秒 */
    public static final int META_CACHE_TIME = 60;
    
    /** 检查线程检查需要删除的过期文件的间隔，单位秒 */
    public static final int CHECK_OUT_OF_DATE_INTERVAL = 60;
    
    /** 默认文件路径 */
    public static final String DEFAULT_ROOT_PATH = "logs" + File.separator + "metrics" + File.separator;
    
    /** 默认二进制文件路径 */
    public static final String BIN_ROOT_TAIL = "bin" + File.separator;
    
    /** 默认输出文件名 */
    public static final String DEFAULT_FILE_NAME = "metrics.log";
    
    /** 自身监控信息分组 */
    public static final String SITUATION_GROUP = "situation_awareness";
    
    /** 输出文件大小的默认最大值 */
    public static final long DEFAULT_MAX_FILE_SIZE = 1 * 1024 * 1024 * 1024;
    
    /** 默认输出间隔，毫秒*/
    public static final int DEFAULT_REPORT_INTERVAL = 5000; 
    
    /** 默认查询最长跨越的天数，避免对服务产生太大压力 */
    public static final int DEFAULT_MAX_SEARCH_INTERVAL = 2;
    
    /** 默认查询时一个数据块 */
    public static final int DEFAULT_MAX_DATA_BLOCK_SIZE = 1 * 1024 *1024;
    
}

