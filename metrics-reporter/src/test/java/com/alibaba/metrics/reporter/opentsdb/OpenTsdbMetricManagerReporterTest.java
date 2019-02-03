package com.alibaba.metrics.reporter.opentsdb;

import com.alibaba.metrics.IMetricManager;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.jvm.GarbageCollectorMetricSet;
import com.alibaba.metrics.jvm.MemoryUsageGaugeSet;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class OpenTsdbMetricManagerReporterTest {

    @Test
    public void test() throws InterruptedException {
        String baseUrl = "http://100.69.200.55:8242/";
        OpenTsdb opentsdb = OpenTsdb.forService(baseUrl)
        		.withReadTimeout(20000)
        		.create();

        IMetricManager metricManager = MetricManager.getIMetricManager();

        MetricRegistry metricRegistry= metricManager.getMetricRegistryByGroup("opentsdb");

        OpenTsdbMetricManagerReporter reporter = OpenTsdbMetricManagerReporter.forMetricManager(metricManager)
                .withGlobalTags(Collections.singletonMap("foo", "bar"))
                .timestampPrecision(TimeUnit.MILLISECONDS)
                .build(opentsdb);

        long period = 3;
        try {
			reporter.start(period, TimeUnit.SECONDS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        metricRegistry.register("jvm.mem", new MemoryUsageGaugeSet());
        metricRegistry.register("jvm.gc", new GarbageCollectorMetricSet());

        TimeUnit.SECONDS.sleep(6);
    }
}
