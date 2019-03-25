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

import com.taobao.middleware.logger.Level;
import com.taobao.middleware.logger.Logger;
import com.taobao.middleware.logger.LoggerFactory;

public class LoggerProvider {

    private static final Logger JERSEY_LOGGER = LoggerFactory.getLogger("org.glassfish.jersey");
    private static final Logger METRICS_LOGGER = LoggerFactory.getLogger("com.alibaba.metrics");

    public static void initLogger() {
        JERSEY_LOGGER.setLevel(Level.WARN);
        JERSEY_LOGGER.activateAppender("metrics", "jersey-info.log", "UTF-8");
        JERSEY_LOGGER.setAdditivity(false);
        METRICS_LOGGER.setLevel(Level.INFO);
        METRICS_LOGGER.activateAppender("metrics", "metrics-info.log", "UTF-8");
        METRICS_LOGGER.setAdditivity(false);
    }

    public static String changeLogLevel(int level) {
        switch (level) {
            case 0:
                JERSEY_LOGGER.setLevel(Level.DEBUG);
                METRICS_LOGGER.setLevel(Level.DEBUG);
                return Level.DEBUG.toString();
            case 2:
                JERSEY_LOGGER.setLevel(Level.WARN);
                METRICS_LOGGER.setLevel(Level.WARN);
                return Level.WARN.toString();
            case 3:
                JERSEY_LOGGER.setLevel(Level.ERROR);
                METRICS_LOGGER.setLevel(Level.ERROR);
                return Level.ERROR.toString();
            case 4:
                JERSEY_LOGGER.setLevel(Level.OFF);
                METRICS_LOGGER.setLevel(Level.OFF);
                return Level.OFF.toString();
            default:
                JERSEY_LOGGER.setLevel(Level.INFO);
                METRICS_LOGGER.setLevel(Level.INFO);
                return Level.INFO.toString();
        }
    }
}
