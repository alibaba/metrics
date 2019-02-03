package com.alibaba.metrics.reporter.opentsdb;

import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.MetricRegistryImpl;
import com.alibaba.metrics.jvm.GarbageCollectorMetricSet;
import com.alibaba.metrics.jvm.MemoryUsageGaugeSet;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class OpenTsdbReporterTest {

    static final MetricRegistry metrics = new MetricRegistryImpl();

    @Test
    public void test() throws InterruptedException {
        String baseUrl = "http://100.69.200.55:8242/";
        OpenTsdb opentsdb = OpenTsdb.forService(baseUrl)
        		.withReadTimeout(20000)
        		.create();

        OpenTsdbReporter openTsdbReporter = OpenTsdbReporter.forRegistry(metrics)
                .withGlobalTags(Collections.singletonMap("foo", "bar"))
                .timestampPrecision(TimeUnit.MILLISECONDS)
                .build(opentsdb);

        long period = 3;
        openTsdbReporter.start(period, TimeUnit.SECONDS);

        metrics.register("jvm.mem", new MemoryUsageGaugeSet());
        metrics.register("jvm.gc", new GarbageCollectorMetricSet());

        TimeUnit.SECONDS.sleep(6);
    }

}
