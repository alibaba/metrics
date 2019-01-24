package com.alibaba.metrics.bin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.bean.MetricResult;
import com.alibaba.metrics.bean.MetricsSearchResponse;
import com.alibaba.metrics.bean.ValueStatus;
import com.alibaba.metrics.common.MetricObject.MetricType;

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
