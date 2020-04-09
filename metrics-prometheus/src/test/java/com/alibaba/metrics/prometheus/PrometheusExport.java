package com.alibaba.metrics.prometheus;

import com.alibaba.metrics.Clock;
import com.alibaba.metrics.ClusterHistogram;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.Timer;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.concurrent.TimeUnit;

public class PrometheusExport {

    private static final String GROUP = "test_group";
    public static void main(String[] args) throws Exception {

        final io.prometheus.client.Counter promCounter = io.prometheus.client.Counter.build()
                .name("test_prom_counter").help("Total requests.").register();

        final Summary rtSummary = Summary.build().name("test_prom_summary").help("test summary")
                .quantile(0.5, 0.05).quantile(0.9, 0.01).quantile(0.99, 0.001)
                .register();

        final Histogram rtHist = Histogram.build().name("test_prom_histogram").help("test histogram")
                .buckets(10, 100, 500, 1000).register();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Counter counter = MetricManager.getCounter(GROUP, new MetricName("test.abc.counter"));
                    counter.inc();

                    promCounter.inc();

                    Meter m = MetricManager.getMeter(GROUP, MetricName.build("test.abc.meter"));
                    m.mark();
                    m.mark();

                    Timer t = MetricManager.getTimer(GROUP, MetricName.build("test.abc.timer"));
                    long duration = (long) (Math.random() * 1000);
                    t.update(duration, TimeUnit.MILLISECONDS);

                    rtSummary.observe(duration);

                    ClusterHistogram ch = MetricManager.getClusterHistogram(GROUP, MetricName.build("test.abc.clusterHistogram"),
                            new long[]{10, 100, 500, 1000});
                    ch.update(duration);

                    rtHist.observe(duration);


                    Compass compass = MetricManager.getCompass(GROUP, MetricName.build("test.abc.compass"));
                    Compass.Context context = compass.time();
                    if (duration % 2 == 0) {
                        context.success();
                    } else {
                        context.error("randomError");
                    }
                    context.stop();


                    FastCompass fc = MetricManager.getFastCompass(GROUP, MetricName.build("test.abc.fastcompass"));
                    if (duration % 2 == 0) {
                        fc.record(duration, "success");
                    } else {
                        fc.record(duration, "error");
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        CollectorRegistry.defaultRegistry.register(new AlibabaMetricsExports());
        Server server = new Server(9000);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");
        // Add metrics about CPU, JVM memory etc.
        DefaultExports.initialize();
        // Start the webserver.
        server.start();
        server.join();
    }
}
