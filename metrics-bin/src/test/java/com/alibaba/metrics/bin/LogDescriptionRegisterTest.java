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
package com.alibaba.metrics.bin;

import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.bean.MetricSearch;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.reporter.bin.DataSource;
import com.alibaba.metrics.status.LogDescriptionRegister;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LogDescriptionRegisterTest {

	private LogDescriptionRegister logDescriptionRegister = new LogDescriptionRegister();

	@Test
	public void logStatusManagerTest(){

		final DataSource dataSource1 = new DataSource("middleware.product.consumer.rt", new HashMap<String, String>(){{
			put("appName","bcp");
		}}, null, 0, MetricLevel.CRITICAL, 0, 0, 0, 1, 0, "FastCompass");

		final DataSource dataSource2 = new DataSource("middleware.product.consumer.rt", new HashMap<String, String>(){{
			put("appName","bcp");
			put("serviceName", "recv~65h");
		}}, null, 0, MetricLevel.CRITICAL, 0, 0, 0, 2, 0, "FastCompass");

		final DataSource dataSource3 = new DataSource("middleware.product.consumer.rt", new HashMap<String, String>(){{
			put("appName","bcp");
			put("serviceName", "send~65h");
		}}, null, 0, MetricLevel.CRITICAL, 0, 0, 0, 3, 0, "FastCompass");

		final DataSource dataSource4 = new DataSource("middleware.product.consumer.rt", new HashMap<String, String>(){{
			put("appName","bcp");
			put("serviceName", "send~63h");
		}}, null, 0, MetricLevel.CRITICAL, 0, 0, 0, 4, 0, "FastCompass");

		final DataSource dataSource5 = new DataSource("middleware.product.consumer.qps", new HashMap<String, String>(){{
			put("appName","bcp");
			put("serviceName", "send~65h");
		}}, null, 0, MetricLevel.CRITICAL, 0, 0, 0, 5, 0, "FastCompass");

		Map<MetricObject, DataSource> dataSources = new HashMap<MetricObject, DataSource>(){{
			put(dataSource1.getMetricObject(), dataSource1);
			put(dataSource2.getMetricObject(), dataSource2);
			put(dataSource3.getMetricObject(), dataSource3);
			put(dataSource4.getMetricObject(), dataSource4);
			put(dataSource5.getMetricObject(), dataSource5);
		}};

		logDescriptionRegister.addDataSources(dataSources);


		final MetricSearch metricSearch1 = new MetricSearch("middleware.product.consumer.rt", new HashMap<String, String>(){{
			put("serviceName", "*");
		}});

		List<MetricSearch> metricNames1 = new ArrayList<MetricSearch>(){{
			add(metricSearch1);
		}};

		Set<DataSource> result0 = logDescriptionRegister.getMetricNames(metricNames1).get(MetricLevel.CRITICAL);

		for(DataSource datasource : result0){
			assert (datasource.getOrder() == 3 || datasource.getOrder() == 2 || datasource.getOrder() == 4);
		}

		final MetricSearch metricSearch2 = new MetricSearch("middleware.product.consumer.rt", new HashMap<String, String>(){{
			put("serviceName", null);
		}});

		List<MetricSearch> metricNames2 = new ArrayList<MetricSearch>(){{
			add(metricSearch2);
		}};

	}

}
