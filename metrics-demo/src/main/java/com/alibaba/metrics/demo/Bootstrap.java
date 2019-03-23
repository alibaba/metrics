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
package com.alibaba.metrics.demo;


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
