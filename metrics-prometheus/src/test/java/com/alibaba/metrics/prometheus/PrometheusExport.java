package com.alibaba.metrics.prometheus;

import com.alibaba.metrics.Counter;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class PrometheusExport {

    private static final String GROUP = "test_group";
    private static final String NAME = "TOTAL";
    public static void main(String[] args) throws Exception{
        Counter counter = MetricManager.getCounter(GROUP, new MetricName(NAME));
        counter.inc();
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
