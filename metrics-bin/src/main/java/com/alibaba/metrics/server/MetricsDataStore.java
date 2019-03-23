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
package com.alibaba.metrics.server;

import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.bean.MetricResult;
import com.alibaba.metrics.bean.ValueStatus;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.bin.DataSource;
import com.alibaba.metrics.utils.Constants;
import com.alibaba.metrics.utils.FigureUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.alibaba.metrics.utils.Constants.LONG_LENGTH;

public abstract class MetricsDataStore {

    private MetricsCollectPeriodConfig metricsCollectPeriodConfig = new MetricsCollectPeriodConfig();

    List<MetricResult> createMetricsResult(Set<DataSource> dataSources, long[] dataArray, long timestamp, MetricLevel level, int precision, List<MetricResult> resultList){

        for (DataSource dataSource : dataSources) {

            long addTime = dataSource.getAddTime();

            if (timestamp < addTime){
                continue;
            }

            if (resultList == null){
                resultList = new LinkedList<MetricResult>();
            }

            MetricResult metricResult = new MetricResult(dataSource.getMetricName(), dataSource.getTags(), ValueStatus.NAN,
                    null, timestamp, metricsCollectPeriodConfig.period(level), dataSource.getMetricType(), dataSource.getMeterName());

            //判断这个metrics的统计精度是否符合要求
            if (metricsCollectPeriodConfig.period(dataSource.getLevel()) > precision){
                metricResult.setValueStatus(ValueStatus.INVALID_PRECISION);
                resultList.add(metricResult);
                continue;
            }

            int offset = (int) dataSource.getOffset();
            if (offset > dataArray.length * LONG_LENGTH){
                continue;
            }

            int order = dataSource.getOrder();

            long value = dataArray[order];
            int fieldType = dataSource.getFieldType();

            Object o = FigureUtil.getValueByType(fieldType, value);

            ValueStatus valueStatus;
            if (value == Constants.VALUE_STATUS_NAN) {
                valueStatus = ValueStatus.NAN;
                o = 0;
            } else {
                valueStatus = ValueStatus.EXIST;
            }

            metricResult.setValue(o);
            metricResult.setValueStatus(valueStatus);

            resultList.add(metricResult);

        }

        return resultList;
    }

}
