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

import com.alibaba.fastjson.JSON;
import com.alibaba.metrics.bean.MetricSearch;
import com.alibaba.metrics.bean.MetricsSearchRequest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MetricRequestTest {

	@Test
	public void createMetricRequest(){

		MetricsSearchRequest request = new MetricsSearchRequest();

		long startTime = 1496318881000L;
		long endTime = 1496318883000L;

		List<MetricSearch> queries = new ArrayList<MetricSearch>(){{
			add(new MetricSearch("metric.name.count", new HashMap<String, String>(){{
				put("tag1", "value1");
			}}));

			add(new MetricSearch("metric.name.count.delta", new HashMap<String, String>(){{
				put("tag1", "value1");
			}}));

			add(new MetricSearch("metric.name.count.error", new HashMap<String, String>(){{
				put("tag1", "value1");
			}}));

			add(new MetricSearch("metric.name.count.m1", new HashMap<String, String>(){{
				put("tag1", "value1");
			}}));

		}};

		request.setStartTime(startTime);
		request.setEndTime(endTime);
		request.setQueries(queries);

		System.out.println(JSON.toJSON(request));

	}
}
