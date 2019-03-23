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
package com.alibaba.metrics.common.filter;

import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;

/**
 *
 * 计算某一个level是否允许report 每到达一个整的时间间隔，就允许report
 *
 */
public class BucketMetricLevelFilter extends TimeMetricLevelFilter {

	public BucketMetricLevelFilter() {
        super();
	}

    public BucketMetricLevelFilter(MetricsCollectPeriodConfig config) {
        super(config);
    }

	/**
	 * 在report之前调用。计算并标记好每一个Level的Metric是否可以report
	 */
	@Override
	public void beforeReport() {
		if (config == null) {
			return;
		}
		long currentTimeMillis = System.currentTimeMillis();

		MetricLevel[] levels = MetricLevel.values();
		for (MetricLevel level : levels) {
			int period = config.period(level);
			// 如果配置的时间间隔是负数，则直接忽略
			if (period < 0) {
				continue;
			}

			MetricsLevelInfo metricsLevelInfo = levelInfos[level.ordinal()];


			int interval = period * 1000;
			long bucketEdge = currentTimeMillis / interval * interval;

			if (metricsLevelInfo.lastReportTimeStamp < bucketEdge) {
				metricsLevelInfo.allow = true;
				metricsLevelInfo.lastReportTimeStamp = currentTimeMillis;
			}
		}
	}

	/**
	 * 在report之后调用。将Metrics Level里允许被report的标记清除
	 */
	@Override
	public void afterReport() {
		if (config == null) {
			return;
		}

		for (MetricsLevelInfo info : levelInfos) {
			if (info.allow) {
				info.allow = false;
			}
		}
	}

}
