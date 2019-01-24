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
