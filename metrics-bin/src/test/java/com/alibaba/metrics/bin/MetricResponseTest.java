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
import com.alibaba.metrics.bean.MetricResult;
import com.alibaba.metrics.bean.MetricsSearchResponse;
import com.alibaba.metrics.bean.ValueStatus;
import com.alibaba.metrics.common.MetricObject.MetricType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MetricResponseTest {

	@Test
	public void createMetricResponse() {

		MetricsSearchResponse response = new MetricsSearchResponse();

		long startTime = 1495455700000L;
		long endTime = 1495468200000L;

		List<MetricResult> result = new ArrayList<MetricResult>() {
			{
				add(new MetricResult("metric.name.count", new HashMap<String, String>() {
					{
						put("tag1", "value1");
					}
				}, ValueStatus.EXIST, 90.60, 1495468200000L, 1000, MetricType.GAUGE, "FastCompass"));

				add(new MetricResult("metric.name.count.delta", new HashMap<String, String>() {
					{
						put("tag1", "value1");
						put("tag2", "value2");
					}
				}, ValueStatus.EXIST, 90.60, 1495468200000L, 1000, MetricType.GAUGE, "FastCompass"));

				add(new MetricResult("metric.name.count.error", new HashMap<String, String>() {
					{
						put("tag1", "value1");
						put("tag2", "value2");
						put("tag3", "value3");
					}
				}, ValueStatus.EXIST, 0, 1495468200000L, 1000, MetricType.GAUGE, "FastCompass"));
				add(new MetricResult("metric.name.count.m1", new HashMap<String, String>() {
					{
						put("tag1", "value1");
						put("tag2", "value2");
						put("tag3", "value3");
						put("tag4", "value4");
					}
				}, ValueStatus.NAN, 0, 1495468200000L, 1000, MetricType.GAUGE, "FastCompass"));
			}
		};

		response.setStartTime(startTime);
		response.setEndTime(endTime);
		response.setResult(result);

		System.out.println(JSON.toJSON(response));

	}
}
