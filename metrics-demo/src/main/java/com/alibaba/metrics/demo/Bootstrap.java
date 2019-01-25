package com.alibaba.metrics.demo;


import com.alibaba.fastjson.support.jaxrs.FastJsonAutoDiscoverable;
import com.alibaba.metrics.integrate.ConfigFields;
import com.alibaba.metrics.integrate.LoggerProvider;
import com.alibaba.metrics.integrate.MetricsIntegrateUtils;
import com.alibaba.metrics.rest.server.MetricsHttpServer;

import java.util.Properties;

public class Bootstrap {

    private static MetricsHttpServer metricsHttpServer = null;

    public static void init() {
        init(System.getProperty(ConfigFields.CONFIG_FILE_NAME));
    }

    public static void init(String configFile) {

        LoggerProvider.initLogger();

        Properties config = MetricsIntegrateUtils.parsePropertiesFromFile(configFile);
        if (configFile != null) {
            config.setProperty(ConfigFields.CONFIG_FILE_NAME, configFile);
        }

        if (MetricsIntegrateUtils.isEnabled(config, "com.alibaba.metrics.http_server.start")) {
            startHttpServer();
        }
        MetricsIntegrateUtils.registerAllMetrics(config);
    }

    public static void destroy() {
        if (metricsHttpServer != null) {
            metricsHttpServer.stop();
        }
    }

    private static void startHttpServer() {
        metricsHttpServer = new MetricsHttpServer();
        metricsHttpServer.start();
    }
}
