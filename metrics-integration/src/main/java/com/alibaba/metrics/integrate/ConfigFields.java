package com.alibaba.metrics.integrate;

import com.alibaba.metrics.MetricLevel;

/**
* 启动参数、启动配置文件、diamond配置统一管理，优先级diamond > 启动参数 > 启动时指定的配置文件
*/
public class ConfigFields {

    /** 目前支持的变量 */
    /** 配置文件字段名 */
    public static String CONFIG_FILE_NAME = "com.alibaba.metrics.config_file";

    /** 是否开启文件日志输出 */
    public static String FILE_REPORTER_DEFAULT_FIELD = "com.alibaba.metrics.reporter.file.enable";

    /** 是否开启二进制日志输出 */
    public static String BIN_REPORTER_DEFAULT_FIELD = "com.alibaba.metrics.reporter.bin.enable";

    /** 重新设置文件日志输出间隔，单位秒 */
    public static String FILE_REPORTER_RESCHEDULE_FIELD = "com.alibaba.metrics.reporter.file.schedule";

    /** 重新设置二进制日志输出间隔，单位秒 */
    public static String BIN_REPORTER_RESCHEDULE_FIELD = "com.alibaba.metrics.reporter.bin.schedule";

    /** 文件输出路径 */
    public static String LOG_PATH_FIELD = "com.alibaba.metrics.log.path";

    /** 二进制文件输出的最大限制 */
    public static String MAX_FILE_SIZE_FIELD = "com.alibaba.metrics.reporter.bin.max_file_size";

    /** 类似p99之类的统计数据开关 */
    public static String ADVANCED_METRICS_FIELDS_DEFAULT_FIELD = "com.alibaba.metrics.advanced_fields";

    /** 系统指标 */
    public static String SYSTEM_CPU_FIELD = "com.alibaba.metrics.system.cpu.enable";
    public static String SYSTEM_CPU_LEVEL_FIELD = "com.alibaba.metrics.system.cpu.level";

    public static String SYSTEM_LOAD_FIELD = "com.alibaba.metrics.system.load.enable";
    public static String SYSTEM_LOAD_LEVEL_FIELD = "com.alibaba.metrics.system.load.level";

    public static String SYSTEM_NET_FIELD = "com.alibaba.metrics.system.net.enable";
    public static String SYSTEM_NET_LEVEL_FIELD = "com.alibaba.metrics.system.net.level";

    public static String SYSTEM_MEMORY_FIELD = "com.alibaba.metrics.system.memory.enable";
    public static String SYSTEM_MEMORY_LEVEL_FIELD = "com.alibaba.metrics.system.memory.level";

    public static String SYSTEM_TCP_FIELD = "com.alibaba.metrics.system.tcp.enable";
    public static String SYSTEM_TCP_LEVEL_FIELD = "com.alibaba.metrics.system.tcp.level";

    public static String SYSTEM_DISK_FIELD = "com.alibaba.metrics.system.disk.enable";
    public static String SYSTEM_DISK_LEVEL_FIELD = "com.alibaba.metrics.system.disk.level";

    public static boolean SYSTEM_CPU_DEFAULT = true;
    public static MetricLevel SYSTEM_CPU_LEVEL = MetricLevel.MAJOR;

    public static boolean SYSTEM_LOAD_DEFAULT = true;
    public static MetricLevel SYSTEM_LOAD_LEVEL = MetricLevel.MAJOR;

    public static boolean SYSTEM_NET_DEFAULT = true;
    public static MetricLevel SYSTEM_NET_LEVEL = MetricLevel.TRIVIAL;

    public static boolean SYSTEM_MEMORY_DEFAULT = true;
    public static MetricLevel SYSTEM_MEMORY_LEVEL = MetricLevel.TRIVIAL;

    public static boolean SYSTEM_TCP_DEFAULT = true;
    public static MetricLevel SYSTEM_TCP_LEVEL = MetricLevel.TRIVIAL;

    public static boolean SYSTEM_DISK_DEFAULT = true;
    public static MetricLevel SYSTEM_DISK_LEVEL = MetricLevel.TRIVIAL;

    public static boolean SYSTEM_JVM_DEFAULT = true;
    public static MetricLevel SYSTEM_JVM_LEVEL = MetricLevel.NORMAL;

    /** JVM指标 */

    public static String JVM_MEM_FIELD = "com.alibaba.metrics.jvm.mem.enable";
    public static String JVM_MEM_LEVEL_FIELD = "com.alibaba.metrics.jvm.mem.level";

    public static String JVM_GC_FIELD = "com.alibaba.metrics.jvm.gc.enable";
    public static String JVM_GC_LEVEL_FIELD = "com.alibaba.metrics.jvm.gc.level";

    public static String JVM_CLASS_LOAD_FIELD = "com.alibaba.metrics.jvm.class_load.enable";
    public static String JVM_CLASS_LOAD_LEVEL_FIELD = "com.alibaba.metrics.jvm.class_load.level";

    public static String JVM_BUFFER_POOL_FIELD = "com.alibaba.metrics.jvm.buffer_pool.enable";
    public static String JVM_BUFFER_POOL_LEVEL_FIELD = "com.alibaba.metrics.jvm.buffer_pool.level";

    public static String JVM_FILE_DESC_FIELD = "com.alibaba.metrics.jvm.file_desc.enable";
    public static String JVM_FILE_DESC_LEVEL_FIELD = "com.alibaba.metrics.jvm.file_desc.level";

    public static String JVM_THREAD_STATE_FIELD = "com.alibaba.metrics.jvm.thread_state.enable";
    public static String JVM_THREAD_STATE_LEVEL_FIELD = "com.alibaba.metrics.jvm.thread_state.level";

    public static String JVM_COMPILATION_FIELD  = "com.alibaba.metrics.jvm.compilation.enable";
    public static String JVM_COMPILATION_LEVEL_FIELD = "com.alibaba.metrics.jvm.compilation.level";

    public static String JVM_RUNTIME_FIELD = "com.alibaba.metrics.jvm.runtime.enable";
    public static String JVM_RUNTIME_LEVEL_FIELD = "com.alibaba.metrics.jvm.runtime.level";

    public static String JVM_COROUTINE_FIELD = "com.alibaba.metrics.jvm.coroutine.enable";
    public static String JVM_COROUTINE_LEVEL_FIELD = "com.alibaba.metrics.jvm.coroutine.level";

    public static boolean JVM_MEM_DEFAULT = true;
    public static MetricLevel JVM_MEM_LEVEL = MetricLevel.NORMAL;

    public static boolean JVM_GC_DEFAULT = true;
    public static MetricLevel JVM_GC_LEVEL = MetricLevel.NORMAL;

    public static boolean JVM_CLASS_LOAD_DEFAULT = true;
    public static MetricLevel JVM_CLASS_LOAD_LEVEL = MetricLevel.NORMAL;

    public static boolean JVM_BUFFER_POOL_DEFAULT = true;
    public static MetricLevel JVM_BUFFER_POOL_LEVEL = MetricLevel.NORMAL;

    public static boolean JVM_FILE_DESC_DEFAULT = true;
    public static MetricLevel JVM_FILE_DESC_LEVEL = MetricLevel.NORMAL;

    public static boolean JVM_THREAD_STATE_DEFAULT = true;
    public static MetricLevel JVM_THREAD_STATE_LEVEL = MetricLevel.TRIVIAL;

    public static boolean JVM_RUNTIME_DEFAULT = true;
    public static MetricLevel JVM_RUNTIME_LEVEL = MetricLevel.TRIVIAL;

    public static boolean JVM_COROUTINE_DEFAULT = false;
    public static MetricLevel JVM_COROUTINE_LEVEL = MetricLevel.TRIVIAL;

    /** TOMCAT指标 */

    public static String TOMCAT_HTTP_FIELD = "com.alibaba.metrics.tomcat.http.enable";
    public static String TOMCAT_HTTP_LEVEL_FIELD = "com.alibaba.metrics.tomcat.http.level";
    public static boolean TOMCAT_HTTP_DEFAULT = true;
    public static MetricLevel TOMCAT_HTTP_LEVEL = MetricLevel.MAJOR;

    public static String TOMCAT_THREAD_FIELD = "com.alibaba.metrics.tomcat.thread.enable";
    public static String TOMCAT_THREAD_LEVEL_FIELD = "com.alibaba.metrics.tomcat.thread.level";
    public static boolean TOMCAT_THREAD_DEFAULT = true;
    public static MetricLevel TOMCAT_THREAD_LEVEL = MetricLevel.NORMAL;

    /** NGINX指标 */

    public static String NGINX_FIELD = "com.alibaba.metrics.nginx.enable";
    public static String NGINX_LEVEL_FIELD = "com.alibaba.metrics.nginx.level";
    public static boolean NGINX_DEFAULT = true;
    public static MetricLevel NGINX_LEVEL = MetricLevel.MAJOR;


    /** Druid指标 */
    public static String DRUID_FIELD = "com.alibaba.metrics.druid.enable";
    public static String DRUID_LEVEL_FIELD = "com.alibaba.metrics.druid.level";
    public static boolean DRUID_DEFAULT = false;
    public static MetricLevel DRUID_LEVEL = MetricLevel.TRIVIAL;


    /** Metrics Cleaner Config */
    public static String METRICS_CLEANER_ENABLE = "com.alibaba.metrics.cleaner.enable";
    public static String METRICS_CLEANER_KEEP_INTERVAL = "com.alibaba.metrics.cleaner.keep_interval";
    public static int METRICS_CLEANER_DEFAULT_KEEP_INTERVAL = 86400; // one day
    public static String METRICS_CLEANER_DELAY = "com.alibaba.metrics.cleaner.delay";
    public static int METRICS_CLEANER_DEFAULT_DELAY = 3600; // one hour

}
