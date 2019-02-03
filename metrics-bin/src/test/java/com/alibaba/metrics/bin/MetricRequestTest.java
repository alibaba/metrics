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
