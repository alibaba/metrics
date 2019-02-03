package com.alibaba.metrics.reporter.file;

import com.alibaba.metrics.IMetricManager;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class Benchmark {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void test() throws IOException, InterruptedException {

        IMetricManager metricManager = MetricManager.getIMetricManager();

        MetricRegistry metricRegistry = metricManager.getMetricRegistryByGroup("system");

        RollingFileAppender appender = RollingFileAppender.builder()
                .name("metrics/metrics.log")
                .fileSize(1024 * 1024 * 1)
                .build();

        FileMetricManagerReporter reporter = FileMetricManagerReporter.forMetricManager(metricManager)
                .withGlobalTags(Collections.singletonMap("foo", "bar"))
                .timestampPrecision(TimeUnit.MILLISECONDS)
                .fileAppender(appender)
                .build();

        long period = 1;
        try {
            reporter.start(period, TimeUnit.SECONDS);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int count = 5000;
        for(int i = 0 ; i < count  ; ++i) {
            metricRegistry.counter(new MetricName("abcccccccccccccccc" + i).tagged("t" + i, "vvvv"));
        }

        TimeUnit.SECONDS.sleep(6);

    }
}
