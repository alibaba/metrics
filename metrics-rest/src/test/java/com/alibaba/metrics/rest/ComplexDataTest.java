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
package com.alibaba.metrics.rest;

import com.alibaba.fastjson.support.jaxrs.FastJsonAutoDiscoverable;
import com.alibaba.metrics.Clock;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.IMetricManager;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.common.CollectLevel;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.bin.BinAppender;
import com.alibaba.metrics.reporter.bin.StructMetricManagerReporter;
import com.alibaba.metrics.rest.server.MetricsHttpServer;
import com.alibaba.metrics.server.MetricsMemoryCache;
import com.alibaba.metrics.server.MetricsOnDisk;
import com.alibaba.metrics.server.MetricsSearchService;
import com.alibaba.metrics.status.LogDescriptionManager;
import com.alibaba.metrics.utils.Constants;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ComplexDataTest {

	private static final String binPath = "logs/metrics/bin/test/";

    private static MetricsOnDisk diskServer = new MetricsOnDisk(binPath);

    private MetricsCollectPeriodConfig metricsCollectPeriodConfig = new MetricsCollectPeriodConfig();
    private LogDescriptionManager logDescriptionManager = new LogDescriptionManager(binPath);
    private MetricsMemoryCache cache = new MetricsMemoryCache(Constants.DATA_CACHE_TIME);

	@Ignore
	@Test
	public void complexDataTest() {
	    FastJsonAutoDiscoverable.autoDiscover = false;
		BinAppender binAppender = new BinAppender(System.currentTimeMillis(), binPath, metricsCollectPeriodConfig, logDescriptionManager,cache, 5, 5);
		binAppender.initWithoutCheckThread();

		IMetricManager metricManager = MetricManager.getIMetricManager();
		StructMetricManagerReporter reporter = new StructMetricManagerReporter(metricManager, binAppender, null,
				Clock.defaultClock(), TimeUnit.SECONDS, TimeUnit.SECONDS, TimeUnit.SECONDS, MetricFilter.ALL,
				new MetricsCollectPeriodConfig(), new HashMap<String, String>(), null, CollectLevel.CLASSIFIER);

		try {
			reporter.start(5, TimeUnit.SECONDS);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		logDescriptionManager.start(0, 60, TimeUnit.SECONDS);
		MetricsSearchService service = MetricsSearchService.getInstance();
		service.build(cache, diskServer, logDescriptionManager);

		new MetricsHttpServer().start();
		Compass productClientSuccess = MetricManager.getCompass("PRODUCT_GROUP",
				new MetricName("PRODUCT_PROVIDER", null, MetricLevel.CRITICAL));

		productClientSuccess.time().success();
		productClientSuccess.update(5, TimeUnit.MILLISECONDS);

		Compass productClientSuccessService = MetricManager.getCompass("PRODUCT_GROUP",
				new MetricName("PRODUCT_PROVIDER_SERVICE", new HashMap<String, String>() {
					{
						put("service", "success_server_1");
					}
				}, MetricLevel.NORMAL));

		productClientSuccessService.time().success();
		productClientSuccessService.update(5, TimeUnit.MILLISECONDS);

		while (true) {
			try {
				Thread.sleep(1000);
				productClientSuccess.time().success();
				productClientSuccess.update(5, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
