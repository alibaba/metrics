package com.alibaba.metrics.tomcat;

import com.alibaba.metrics.CachedMetricSet;
import com.alibaba.metrics.Clock;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.PersistentGauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpGaugeSet extends CachedMetricSet {

    private static final Logger logger = LoggerFactory.getLogger(HttpGaugeSet.class);

    private enum HttpMetrics {
        REQUEST_COUNT, PROCESSING_TIME, ERROR_COUNT,
        MAX_TIME, BYTES_SENT, BYTES_RECEIVED
    }

    private static final String[] metricNames = {
            "request_count",
            "request.processing_time",
            "request.error_count",
            "request.max_time",
            "request.bytes_sent",
            "request.bytes_received"
    };

    private ObjectName globalReqProcessor;
    private MBeanServer mbeanServer;
    private Map<String, Long[]> connectorMetrics;
    private Map<String, Integer[]> failureStats;
    final Map<MetricName, Metric> gauges;

    public HttpGaugeSet() {
        this(JMXUtils.getMBeanServer(), DEFAULT_DATA_TTL, TimeUnit.MILLISECONDS, Clock.defaultClock());
    }

    public HttpGaugeSet(long dataTTL, TimeUnit unit) {
        this(JMXUtils.getMBeanServer(), dataTTL, unit, Clock.defaultClock());
    }

    public HttpGaugeSet(MBeanServer mbeanServer, long dataTTL, TimeUnit unit, Clock clock) {
        super(dataTTL, unit, clock);
        connectorMetrics = new HashMap<String, Long[]>();
        globalReqProcessor = JMXUtils.createObjectName("*:type=GlobalRequestProcessor,*");
        this.mbeanServer = mbeanServer;
        failureStats = new HashMap<String, Integer[]>();
        gauges = new HashMap<MetricName, Metric>();
        populateGauges();
    }

    @Override
    protected void getValueInternal() {
        // collect http metrics
        ObjectName[] connectorNames = JMXUtils.getObjectNames(globalReqProcessor);

        for (ObjectName connectorName : connectorNames) {
            String name = ObjectName.unquote(connectorName.getKeyProperty("name"));
            Long[] metrics = connectorMetrics.get(name);
            try {
                metrics[HttpMetrics.REQUEST_COUNT.ordinal()] =
                        Long.valueOf((Integer)mbeanServer.getAttribute(connectorName, "requestCount"));
                metrics[HttpMetrics.PROCESSING_TIME.ordinal()] =
                        (Long)mbeanServer.getAttribute(connectorName, "processingTime");
                metrics[HttpMetrics.ERROR_COUNT.ordinal()] =
                        Long.valueOf((Integer)mbeanServer.getAttribute(connectorName, "errorCount"));
                metrics[HttpMetrics.MAX_TIME.ordinal()] =
                        (Long)mbeanServer.getAttribute(connectorName, "maxTime");
                metrics[HttpMetrics.BYTES_RECEIVED.ordinal()] =
                        (Long)mbeanServer.getAttribute(connectorName, "bytesReceived");
                metrics[HttpMetrics.BYTES_SENT.ordinal()] =
                        (Long)mbeanServer.getAttribute(connectorName, "bytesSent");
            } catch (Exception e) {
                logger.error("Exception occur when getting connector global stats: ", e);
            }

            // get request failure count
            try {
                Object attribute = mbeanServer.getAttribute(connectorName, "requestFailureCount");
                if (attribute != null) {
                    Integer[] data = (Integer[]) attribute;
                    for (int i = 0; i < data.length; i++) {
                        failureStats.get(name)[i] = data[i];
                    }
                }
            } catch (Exception ex) {
                // it is ok if running on Apache Tomcat, which has no such attribute indeed.
            }
        }
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        return gauges;
    }

    private void populateGauges() {
        // collect http metrics
        ObjectName[] connectorNames = JMXUtils.getObjectNames(globalReqProcessor);

        for (ObjectName connectorName : connectorNames) {
            String name = ObjectName.unquote(connectorName.getKeyProperty("name"));
            connectorMetrics.put(name, new Long[HttpMetrics.values().length]);
        }

        for (Map.Entry<String, Long[]> entry: connectorMetrics.entrySet()) {
            for (HttpMetrics metric : HttpMetrics.values()) {
                gauges.put(MetricName.build(metricNames[metric.ordinal()]).tagged("connector", entry.getKey()),
                        new HttpStatGauge<Long>(entry.getValue(), metric.ordinal()));
            }
        }

    }

    private class HttpStatGauge<T> extends PersistentGauge {
        private T[] dataArray;
        private int index;

        public HttpStatGauge(T[] dataArray, int index) {
            this.dataArray = dataArray;
            this.index = index;
        }

        @Override
        public T getValue() {
            refreshIfNecessary();
            return dataArray[index];
        }
    }
}
