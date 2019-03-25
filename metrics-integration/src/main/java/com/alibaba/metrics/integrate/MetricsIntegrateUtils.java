/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.metrics.integrate;

import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.StringUtils;
import com.alibaba.metrics.druid.DruidMetricsGaugeSet;
import com.alibaba.metrics.jvm.BufferPoolMetricSet;
import com.alibaba.metrics.jvm.ClassLoadingGaugeSet;
import com.alibaba.metrics.jvm.CompilationGauge;
import com.alibaba.metrics.jvm.FileDescriptorGauge;
import com.alibaba.metrics.jvm.FileDescriptorRatioGauge;
import com.alibaba.metrics.jvm.GarbageCollectorMetricSet;
import com.alibaba.metrics.jvm.JvmAttributeGaugeSet;
import com.alibaba.metrics.jvm.MemoryUsageGaugeSet;
import com.alibaba.metrics.jvm.ThreadStatesGaugeSet;
import com.alibaba.metrics.nginx.NginxGaugeSet;
import com.alibaba.metrics.os.utils.SystemInfoUtils;
import com.alibaba.metrics.tomcat.HttpGaugeSet;
import com.alibaba.metrics.tomcat.ThreadGaugeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.alibaba.metrics.integrate.ConfigFields.DRUID_DEFAULT;
import static com.alibaba.metrics.integrate.ConfigFields.DRUID_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.DRUID_LEVEL;
import static com.alibaba.metrics.integrate.ConfigFields.DRUID_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_BUFFER_POOL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_BUFFER_POOL_LEVEL;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_BUFFER_POOL_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_CLASS_LOAD_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_CLASS_LOAD_LEVEL;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_CLASS_LOAD_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_COMPILATION_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_COMPILATION_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_FILE_DESC_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_FILE_DESC_LEVEL;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_FILE_DESC_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_GC_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_GC_LEVEL;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_GC_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_MEM_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_MEM_LEVEL;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_MEM_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_THREAD_STATE_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.JVM_THREAD_STATE_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.METRICS_CLEANER_DEFAULT_DELAY;
import static com.alibaba.metrics.integrate.ConfigFields.METRICS_CLEANER_DEFAULT_KEEP_INTERVAL;
import static com.alibaba.metrics.integrate.ConfigFields.METRICS_CLEANER_DELAY;
import static com.alibaba.metrics.integrate.ConfigFields.METRICS_CLEANER_ENABLE;
import static com.alibaba.metrics.integrate.ConfigFields.METRICS_CLEANER_KEEP_INTERVAL;
import static com.alibaba.metrics.integrate.ConfigFields.NGINX_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.NGINX_LEVEL;
import static com.alibaba.metrics.integrate.ConfigFields.NGINX_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_CPU_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_CPU_LEVEL;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_CPU_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_DISK_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_DISK_LEVEL;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_DISK_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_LOAD_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_LOAD_LEVEL;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_LOAD_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_MEMORY_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_MEMORY_LEVEL;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_MEMORY_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_NET_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_NET_LEVEL;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_NET_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_TCP_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_TCP_LEVEL;
import static com.alibaba.metrics.integrate.ConfigFields.SYSTEM_TCP_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.TOMCAT_HTTP_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.TOMCAT_HTTP_LEVEL;
import static com.alibaba.metrics.integrate.ConfigFields.TOMCAT_HTTP_LEVEL_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.TOMCAT_THREAD_FIELD;
import static com.alibaba.metrics.integrate.ConfigFields.TOMCAT_THREAD_LEVEL;
import static com.alibaba.metrics.integrate.ConfigFields.TOMCAT_THREAD_LEVEL_FIELD;
import static com.alibaba.metrics.os.utils.SystemInfoUtils.needLoad;

public class MetricsIntegrateUtils {

    private static final Logger log = LoggerFactory.getLogger(MetricsIntegrateUtils.class);

    private static final String DEFAULT_SCRAPE_FILE = ".ali_metrics_scrape_config";

    private static MetricsCleaner metricsCleaner = null;

    /**
     * Register all jvm metrics as MetricLevel.NORMAL
     */
    public static void registerJvmMetrics(Properties metricsConfig) {

        /** JVM指标 */

        if (isEnabled(metricsConfig, JVM_MEM_FIELD)) {
            MetricLevel jvmMemLevel = parseMetricLevel(metricsConfig, JVM_MEM_LEVEL_FIELD, JVM_MEM_LEVEL);
            MetricManager.register("jvm", MetricName.build("jvm.mem").level(jvmMemLevel), new MemoryUsageGaugeSet());
        }

        if (isEnabled(metricsConfig, JVM_GC_FIELD)) {
            MetricLevel jvmGcLevel = parseMetricLevel(metricsConfig, JVM_GC_LEVEL_FIELD, JVM_GC_LEVEL);
            MetricManager.register("jvm", MetricName.build("jvm.gc").level(jvmGcLevel),
                    new GarbageCollectorMetricSet());
        }

        if (isEnabled(metricsConfig, JVM_CLASS_LOAD_FIELD)) {
            MetricLevel jvmClassLoadLevel = parseMetricLevel(metricsConfig, JVM_CLASS_LOAD_LEVEL_FIELD,
                    JVM_CLASS_LOAD_LEVEL);
            MetricManager.register("jvm", MetricName.build("jvm.class_load").level(jvmClassLoadLevel),
                    new ClassLoadingGaugeSet());
        }

        if (isEnabled(metricsConfig, JVM_BUFFER_POOL_FIELD)) {
            MetricLevel jvmBufferPoolLevel = parseMetricLevel(metricsConfig, JVM_BUFFER_POOL_LEVEL_FIELD,
                    JVM_BUFFER_POOL_LEVEL);
            MetricManager.register("jvm", MetricName.build("jvm.buffer_pool").level(jvmBufferPoolLevel),
                    new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        }

        if (isEnabled(metricsConfig, JVM_FILE_DESC_FIELD)) {
            MetricLevel jvmFileDescLevel = parseMetricLevel(metricsConfig, JVM_FILE_DESC_LEVEL_FIELD,
                    JVM_FILE_DESC_LEVEL);
            MetricManager.register("jvm", MetricName.build("jvm.file_descriptor.open_ratio").level(jvmFileDescLevel),
                    new FileDescriptorRatioGauge(ManagementFactory.getOperatingSystemMXBean(),
                            cachedTimeForLevel(jvmFileDescLevel), TimeUnit.SECONDS));
            MetricManager.register("jvm", MetricName.build("jvm.file_descriptor.open_count").level(jvmFileDescLevel),
                    new FileDescriptorGauge(cachedTimeForLevel(jvmFileDescLevel), TimeUnit.SECONDS, ManagementFactory.getOperatingSystemMXBean()));

        }

        if (isEnabled(metricsConfig, JVM_THREAD_STATE_FIELD)) {
            MetricLevel jvmThreadStateLevel = parseMetricLevel(metricsConfig, JVM_THREAD_STATE_LEVEL_FIELD,
                    MetricLevel.TRIVIAL);
            MetricManager.register("jvm", MetricName.build("jvm.thread").level(jvmThreadStateLevel),
                    new ThreadStatesGaugeSet(cachedTimeForLevel(jvmThreadStateLevel), TimeUnit.SECONDS));
        }


        if (isEnabled(metricsConfig, JVM_COMPILATION_FIELD)) {
            MetricLevel jvmCompilationLevel = parseMetricLevel(metricsConfig, JVM_COMPILATION_LEVEL_FIELD,
                    MetricLevel.TRIVIAL);
            MetricManager.register("jvm", MetricName.build("jvm.compilation.delta_time").level(jvmCompilationLevel),
                    new CompilationGauge(cachedTimeForLevel(jvmCompilationLevel), TimeUnit.SECONDS));
        }

    }

    /**
     * <pre>
     * Register jvm attribute metrics as MetricLevel.NORMAL
     *
     * </pre>
     */
    public static void registerJvmAttributeMetrics(Properties metricsConfig) {
        MetricManager.register("jvm", MetricName.build("jvm.runtime"), new JvmAttributeGaugeSet());
    }

    /**
     * Register all system metrics
     */
    public static void registerSystemMetrics(Properties metricsConfig) {

        SystemInfoUtils.init();

        /** 系统指标 */
        if (isEnabled(metricsConfig, SYSTEM_LOAD_FIELD)) {
            MetricLevel loadLevel = parseMetricLevel(metricsConfig, SYSTEM_LOAD_LEVEL_FIELD, SYSTEM_LOAD_LEVEL);
            Metric metric = null;

            if (!needLoad()){
                metric = new com.alibaba.metrics.os.linux.SystemLoadGaugeSet(cachedTimeForLevel(loadLevel), TimeUnit.SECONDS);
            }else{
                metric = new com.alibaba.metrics.os.windows.SystemLoadGaugeSet(cachedTimeForLevel(loadLevel), TimeUnit.SECONDS);
            }

            MetricManager.register("system", MetricName.build("system").level(loadLevel), metric);
        }

        if (isEnabled(metricsConfig, SYSTEM_CPU_FIELD)) {
            MetricLevel cpuLevel = parseMetricLevel(metricsConfig, SYSTEM_CPU_LEVEL_FIELD, SYSTEM_CPU_LEVEL);

            Metric metric = null;

            if (!needLoad()){
                metric = new com.alibaba.metrics.os.linux.CpuUsageGaugeSet(cachedTimeForLevel(cpuLevel), TimeUnit.SECONDS);
            }else{
                metric = new com.alibaba.metrics.os.windows.CpuUsageGaugeSet(cachedTimeForLevel(cpuLevel), TimeUnit.SECONDS);
            }

            MetricManager.register("system", MetricName.build("system").level(cpuLevel), metric);
        }

        if (isEnabled(metricsConfig, SYSTEM_NET_FIELD)) {
            MetricLevel netLevel = parseMetricLevel(metricsConfig, SYSTEM_NET_LEVEL_FIELD, SYSTEM_NET_LEVEL);
            MetricManager.register("system", MetricName.build("system").level(netLevel),
                    new com.alibaba.metrics.os.linux.NetTrafficGaugeSet(cachedTimeForLevel(netLevel), TimeUnit.SECONDS));
        }

        if (isEnabled(metricsConfig, SYSTEM_MEMORY_FIELD)) {
            MetricLevel memLevel = parseMetricLevel(metricsConfig, SYSTEM_MEMORY_LEVEL_FIELD, SYSTEM_MEMORY_LEVEL);

            Metric metric = null;

            if (!needLoad()){
                metric = new com.alibaba.metrics.os.linux.SystemMemoryGaugeSet(cachedTimeForLevel(memLevel), TimeUnit.SECONDS);
            }else{
                metric = new com.alibaba.metrics.os.windows.SystemMemoryGaugeSet(cachedTimeForLevel(memLevel), TimeUnit.SECONDS);
            }

            MetricManager.register("system", MetricName.build("system").level(memLevel), metric);
        }

        if (isEnabled(metricsConfig, SYSTEM_TCP_FIELD)) {
            MetricLevel tcpLevel = parseMetricLevel(metricsConfig, SYSTEM_TCP_LEVEL_FIELD, SYSTEM_TCP_LEVEL);
            MetricManager.register("system", MetricName.build("system").level(tcpLevel),
                    new com.alibaba.metrics.os.linux.TcpGaugeSet(cachedTimeForLevel(tcpLevel), TimeUnit.SECONDS));
        }

        if (isEnabled(metricsConfig, SYSTEM_DISK_FIELD)) {
            MetricLevel diskLevel = parseMetricLevel(metricsConfig, SYSTEM_DISK_LEVEL_FIELD, SYSTEM_DISK_LEVEL);
            MetricManager.register("system", MetricName.build("system").level(diskLevel),
                    new com.alibaba.metrics.os.linux.DiskStatGaugeSet(cachedTimeForLevel(diskLevel), TimeUnit.SECONDS));
        }

    }

    /**
     * Register all tomcat metrics
     */
    public static void registerTomcatMetrics(Properties metricsConfig) {

        if (isEnabled(metricsConfig, TOMCAT_HTTP_FIELD)) {
            MetricLevel httpLevel = parseMetricLevel(metricsConfig, TOMCAT_HTTP_LEVEL_FIELD, TOMCAT_HTTP_LEVEL);
            MetricManager.register("tomcat", MetricName.build("middleware.tomcat.http").level(httpLevel),
                    new HttpGaugeSet(cachedTimeForLevel(httpLevel), TimeUnit.SECONDS));
        }

        if (isEnabled(metricsConfig, TOMCAT_THREAD_FIELD)) {
            MetricLevel theadLevel = parseMetricLevel(metricsConfig, TOMCAT_THREAD_LEVEL_FIELD, TOMCAT_THREAD_LEVEL);
            MetricManager.register("tomcat", MetricName.build("middleware.tomcat.thread").level(theadLevel),
                    new ThreadGaugeSet(cachedTimeForLevel(theadLevel), TimeUnit.SECONDS));
        }

    }

    /**
     * Register nginx metrics
     */
    public static void registerNginxMetrics(Properties metricsConfig) {

        if (isEnabled(metricsConfig, NGINX_FIELD)) {
            MetricLevel nginxLevel = parseMetricLevel(metricsConfig, NGINX_LEVEL_FIELD, NGINX_LEVEL);
            MetricManager.register("nginx", MetricName.build("middleware.nginx").level(nginxLevel),
                    new NginxGaugeSet(cachedTimeForLevel(nginxLevel), TimeUnit.SECONDS));
        }

    }

    /**
     * Register druid metrics
     * @param metricsConfig the metric configurations
     */
    public static void registerDruidMetrics(Properties metricsConfig) {
        if ("true".equals(metricsConfig.getProperty("com.alibaba.metrics.druid.forceTurnOnStats"))) {
            String druidFilters = System.getProperty("druid.filters");
            if (StringUtils.isBlank(druidFilters)) {
                druidFilters = "stat";
            } else if (!druidFilters.contains("stat")) {
                druidFilters += ",stat";
            }
            System.setProperty("druid.filters", druidFilters);
            // 强制打开druid的监控功能
            System.setProperty("druid.stat.mergeSql", "true");
            System.setProperty("druid.useGlobalDataSourceStat", "true");
        }
        if (isEnabled(metricsConfig, DRUID_FIELD, DRUID_DEFAULT)) {
            MetricLevel druidLevel = parseMetricLevel(metricsConfig, DRUID_LEVEL_FIELD, DRUID_LEVEL);
            MetricName baseName = MetricName.build("druid.sql").level(druidLevel);
            MetricManager.register("druid", baseName,
                    new DruidMetricsGaugeSet(cachedTimeForLevel(druidLevel), TimeUnit.SECONDS, baseName));
        }
    }

    /**
     * Register all metrics
     */
    public static void registerAll() {
        registerAllMetrics(new Properties());
    }

    /**
     * Register metrics with configuration file
     *
     * @param configFile
     *            the absolute path of the config file
     */
    public static void registerMetrics(String configFile) {
        Properties prop = parsePropertiesFromFile(configFile);
        registerAllMetrics(prop);
    }

    /**
     * Register metrics with configuration
     *
     * @param metricsConfig
     *            the properties
     */
    public static void registerAllMetrics(Properties metricsConfig) {

        try {
            registerSystemMetrics(metricsConfig);
        } catch (Exception e) {
            log.error("Register system metrics failed: ", e);
        }

        try {
            registerJvmMetrics(metricsConfig);
        } catch (Exception e) {
            log.error("Register jvm metrics failed: ", e);
        }

        try {
            registerTomcatMetrics(metricsConfig);
        } catch (Exception e) {
            log.error("Register tomcat metrics failed: ", e);
        }

        try {
            registerNginxMetrics(metricsConfig);
        } catch (Exception e) {
            log.error("Register nginx metrics failed: ", e);
        }

        try {
            registerDruidMetrics(metricsConfig);
        } catch (Exception e) {
            log.error("Register druid metrics failed: ", e);
        }

        generateScrapeConfigFile("/tmp" + File.separator + DEFAULT_SCRAPE_FILE);
    }

    /**
     * Start the metrics cleaner thread.
     */
    public static void startMetricsCleaner(Properties metricsConfig) {
        if (isEnabled(metricsConfig, METRICS_CLEANER_ENABLE)) {
            int keepInterval = parseInteger(metricsConfig, METRICS_CLEANER_KEEP_INTERVAL,
                    METRICS_CLEANER_DEFAULT_KEEP_INTERVAL);
            int delay = parseInteger(metricsConfig, METRICS_CLEANER_DELAY,
                    METRICS_CLEANER_DEFAULT_DELAY);
            if (metricsCleaner == null) {
                metricsCleaner = new MetricsCleaner(keepInterval, delay);
                metricsCleaner.start();
            }
        }
    }

    /**
     * Stop the metrics cleaner thread.
     */
    public static void stopMetricsCleaner() {
        if (metricsCleaner != null) {
            metricsCleaner.stop();
            metricsCleaner = null;
        }
    }

    public static Properties parsePropertiesFromFile(String configFile) {
        Properties prop = new Properties();
        if (configFile == null)
            return prop;
        InputStream input = null;
        try {
            input = new FileInputStream(configFile);
            // load a properties file
            prop.load(input);
        } catch (IOException e) {
            log.error("Error when loading property file:", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return prop;
    }


    public static boolean isEnabled(Properties config, String input) {
        return isEnabled(config, input, true);
    }

    /**
     * Check whether a config is enabled or not.
     * This method only return false if a config is explicitly specified to false.
     *   the config con be specified via
     *      - system property
     *      - {@link ConfigFields.CONFIG_FILE_NAME}
     *      - system env
     *   They follow the following priority order:
     *     System property > System env > input properties as parameter > default value
     * @param config the config property
     * @param input the specific config
     * @param defaultValue the default value if it is specified in nowhere
     * @return true if the config does not exist, or the config value is null, or the value is true
     */
    public static boolean isEnabled(Properties config, String input, boolean defaultValue) {
        // 1. try system property
        try {
            String data = System.getProperty(input);
            if (data != null) {
                return Boolean.parseBoolean(data);
            }
        } catch (Exception e) {
            // ignore
        }
        // 2. try system env
        try {
            String data = System.getenv().get(input);
            if (data != null) {
                return Boolean.parseBoolean(data);
            }
        } catch (Exception e) {
            // ignore
        }
        // 3. try input properties
        try {
            String data = config.getProperty(input);
            if (data != null) {
                return Boolean.parseBoolean(data);
            }
        } catch (Exception e) {
            // ignore
        }
        // default case
        return defaultValue;
    }

    /**
     * Generate a scrape config file for external scrapers.
     * In spring-boot, http port in configured via system property: management.port
     * @return true if config file is generated successfully
     */
    static boolean generateScrapeConfigFile(String fileName) {
        Integer port;
        // test tomcat monitor port first
        port = Integer.getInteger("tomcat.monitor.http.port");
        if (port == null) {
            port = Integer.getInteger("com.alibaba.metrics.http.port");
        }
        if (port == null) {
            // test spring-boot
            port = Integer.getInteger("management.port");
        }
        if (port == null) {
            log.warn("Could not find http port, skipping scrape file generation.");
            return false;
        }

        List<ScrapeConfig> configs = new ArrayList<ScrapeConfig>();
        boolean found = false;
        // create config if not exist
        File pathFile = new File(fileName);
        if (!pathFile.exists()) {
            try {
                pathFile.createNewFile();
            } catch (IOException e) {
                log.error("Error when creating scrape config file", e);
                return false;
            }
        } else if (pathFile.length() > 0) {
            try {
                // read existing config
                JSONReader reader = new JSONReader(new FileReader(fileName));
                reader.startArray();
                while (reader.hasNext()) {
                    ScrapeConfig config = reader.readObject(ScrapeConfig.class);
                    configs.add(config);
                    if (config.getPort() == port) {
                        found = true;
                    }
                }
                reader.endArray();
                reader.close();
            } catch (Exception e) {
                log.error("Error when reading scrape config file", e);
                return false;
            }
        }

        RandomAccessFile raf = null;
        FileLock fileLock = null;
        try {
            raf = new RandomAccessFile(pathFile, "rw");
            fileLock = raf.getChannel().tryLock();
            if (fileLock == null) {
                // the lock is acquired by another process
                log.info("The scrape config file is locked by another process, fail fast.");
                return false;
            }
        } catch (Exception e) {
            log.error("Error when locking scrape config file", e);
            return false;
        }

        JSONWriter writer;
        try {
            if (!found) {
                ScrapeConfig newConfig = new ScrapeConfig(port, "http://127.0.0.1:" + port + "/metrics/search");
                newConfig.build();
                configs.add(newConfig);
                // serialize all the configs
                writer = new JSONWriter(new FileWriter(fileName));
                writer.startArray();
                for (ScrapeConfig config: configs) {
                    writer.writeValue(config);
                }
                writer.endArray();
                writer.close();
            }
            return true;
        } catch (Exception e) {
            log.error("Error when writing scrape config file", e);
            return false;
        } finally {
            try {
                fileLock.release();
                raf.close();
            } catch (Exception e) {
                log.error("Error when closing scrape config file", e);
            }
        }
    }

    private static MetricLevel parseMetricLevel(Properties config, String level, MetricLevel defaultLevel) {
        try {
            return MetricLevel.valueOf(config.getProperty(level));
        } catch (Exception e) {
            return defaultLevel;
        }
    }

    private static int parseInteger(Properties config, String input, int defaultValue) {
        try {
            return Integer.parseInt(config.getProperty(input));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static int cachedTimeForLevel(MetricLevel level) {

        switch (level) {
        case TRIVIAL:
            return 50;
        case MINOR:
            return 20;
        case NORMAL:
            return 10;
        case MAJOR:
            return 2;
        case CRITICAL:
            return 1;
        default:
            return 50;
        }
    }
}
