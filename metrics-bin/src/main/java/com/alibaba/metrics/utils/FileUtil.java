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
package com.alibaba.metrics.utils;

import com.alibaba.metrics.MetricLevel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FileUtil {

    public static final String DATASOURCE_PREFIX = "datasource";
    public static final String CURRENT_DATA_PREFIX = "currentdata";
    public static final String INDEX_V2_PREFIX = "index_v2";
    public static final String ARCHIVE_PREFIX = "archivedata";

    public static final String GENERAL_SUFFIX = ".bin";
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static final String DATE_SPLIT = "-";
    public static final String SPLIT = "_";
    public static final String FILE_SEPARATOR = "/";

    public static final String USER_HOME = locateUserHome();

    public static final String TIME_ZONE_UTC_PLUS_8 = "Asia/Shanghai";

    public static final long MAX_FILE_SIZE = 1024 * 1024 * 1024;

    public static String getBasePath(String logRootPath) {

        StringBuilder path = new StringBuilder();
        return path.append(USER_HOME).append(logRootPath).toString();
    }

    public static String getMetricsDir(long timestamp, String logRootPath) {

        Date date = new Date(timestamp);
        StringBuilder path = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC_PLUS_8));

        return path.append(USER_HOME).append(logRootPath).append(sdf.format(date)).toString();
    }

    public static String getDataSourceFileName(long timestamp, String logRootPath, MetricLevel level) {

        Date date = new Date(timestamp);
        StringBuilder fileName = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC_PLUS_8));

        fileName.append(USER_HOME).append(logRootPath).append(sdf.format(date)).append(FILE_SEPARATOR)
                .append(DATASOURCE_PREFIX).append(SPLIT).append(sdf.format(date)).append(SPLIT).append(level)
                .append(GENERAL_SUFFIX);

        // fileName.append(USER_HOME).append(BASE_PATH).append(DATASOURCE_PREFIX).append(SPLIT).append(sdf.format(date))
        // .append(GENERAL_SUFFIX);
        return fileName.toString();
    }

    public static String getLogFileName(long timestamp, String logRootPath, MetricLevel level) {

        Date date = new Date(timestamp);
        StringBuilder fileName = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC_PLUS_8));

        fileName.append(USER_HOME).append(logRootPath).append(sdf.format(date)).append(FILE_SEPARATOR)
                .append(CURRENT_DATA_PREFIX).append(SPLIT).append(sdf.format(date)).append(SPLIT).append(level)
                .append(GENERAL_SUFFIX);

        // fileName.append(USER_HOME).append(BASE_PATH).append(CURRENT_DATA_PREFIX).append(SPLIT).append(sdf.format(date))
        // .append(GENERAL_SUFFIX);
        return fileName.toString();
    }

    public static String getIndexFileName(long timestamp, String logRootPath, MetricLevel level) {
        Date date = new Date(timestamp);
        StringBuilder fileName = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC_PLUS_8));

        fileName.append(USER_HOME).append(logRootPath).append(sdf.format(date)).append(FILE_SEPARATOR)
                .append(INDEX_V2_PREFIX).append(SPLIT).append(sdf.format(date)).append(SPLIT).append(level)
                .append(GENERAL_SUFFIX);

        // fileName.append(USER_HOME).append(BASE_PATH).append(INDEX_PREFIX).append(SPLIT).append(sdf.format(date))
        // .append(GENERAL_SUFFIX);

        return fileName.toString();
    }

    public static String getArchiveFileName(long timestamp, String logRootPath) {

        Date date = new Date(timestamp);
        StringBuilder fileName = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC_PLUS_8));

        fileName.append(USER_HOME).append(logRootPath).append(ARCHIVE_PREFIX).append(SPLIT).append(sdf.format(date))
                .append(GENERAL_SUFFIX);
        return fileName.toString();
    }

    public static String getLogFileName(String year, String month, String day) {
        StringBuilder fileName = new StringBuilder();
        return fileName.toString();
    }

    public static String getArchiveFileName(String year, String month, String day) {
        StringBuilder fileName = new StringBuilder();

        return fileName.toString();
    }

    public static final String locateUserHome() {
        String userHome = System.getProperty("user.home");
        if (FigureUtil.isNotBlank(userHome)) {
            if (!userHome.endsWith(File.separator)) {
                userHome += File.separator;
            }
        } else {
            userHome = "/tmp/";
        }
        return userHome;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static boolean checkFileSize(long fileSize) {
        return fileSize <= MAX_FILE_SIZE;
    }

}
