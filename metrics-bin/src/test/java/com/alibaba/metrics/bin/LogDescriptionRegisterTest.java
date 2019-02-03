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

		final DataSource dataSource1 = new DataSource("middleware.dubbo.consumer.rt", new HashMap<String, String>(){{
			put("appName","bcp");
		}}, null, 0, MetricLevel.CRITICAL, 0, 0, 0, 1, 0, "FastCompass");

		final DataSource dataSource2 = new DataSource("middleware.dubbo.consumer.rt", new HashMap<String, String>(){{
			put("appName","bcp");
			put("serviceName", "recv~65h");
		}}, null, 0, MetricLevel.CRITICAL, 0, 0, 0, 2, 0, "FastCompass");

		final DataSource dataSource3 = new DataSource("middleware.dubbo.consumer.rt", new HashMap<String, String>(){{
			put("appName","bcp");
			put("serviceName", "send~65h");
		}}, null, 0, MetricLevel.CRITICAL, 0, 0, 0, 3, 0, "FastCompass");

		final DataSource dataSource4 = new DataSource("middleware.dubbo.consumer.rt", new HashMap<String, String>(){{
			put("appName","bcp");
			put("serviceName", "send~63h");
		}}, null, 0, MetricLevel.CRITICAL, 0, 0, 0, 4, 0, "FastCompass");

		final DataSource dataSource5 = new DataSource("middleware.dubbo.consumer.qps", new HashMap<String, String>(){{
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


		final MetricSearch metricSearch1 = new MetricSearch("middleware.dubbo.consumer.rt", new HashMap<String, String>(){{
			put("serviceName", "*");
		}});

		List<MetricSearch> metricNames1 = new ArrayList<MetricSearch>(){{
			add(metricSearch1);
		}};

		Set<DataSource> result0 = logDescriptionRegister.getMetricNames(metricNames1).get(MetricLevel.CRITICAL);

		for(DataSource datasource : result0){
			assert (datasource.getOrder() == 3 || datasource.getOrder() == 2 || datasource.getOrder() == 4);
		}

		final MetricSearch metricSearch2 = new MetricSearch("middleware.dubbo.consumer.rt", new HashMap<String, String>(){{
			put("serviceName", null);
		}});

		List<MetricSearch> metricNames2 = new ArrayList<MetricSearch>(){{
			add(metricSearch2);
		}};

	}

}
