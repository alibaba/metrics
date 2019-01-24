package com.alibaba.metrics.nginx;

import com.alibaba.metrics.CachedMetricSet;
import com.alibaba.metrics.Clock;
import com.alibaba.metrics.Constants;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.PersistentGauge;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Collecting nginx metrics, this class will query from a url periodically,
 * and parse the returned data to nginx metrics.
 * See https://nginx.org/en/docs/http/ngx_http_stub_status_module.html for more details.
 */
public class NginxGaugeSet extends CachedMetricSet {

    /**
     * The following status information is provided:
         Active connections
            The current number of active client connections including Waiting connections.
         accepts
            The total number of accepted client connections.
         handled
            The total number of handled connections.
            Generally, the parameter value is the same as accepts unless some resource limits have been reached
            (for example, the worker_connections limit).
         requests
            The total number of client requests.
         Reading
            The current number of connections where nginx is reading the request header.
         Writing
            The current number of connections where nginx is writing the response back to the client.
         Waiting
            The current number of idle client connections waiting for a request.
     */
    private static final String[] DELTA = {
            "conn.accepted",            // number of connections accepted
            "conn.handled",             // number of connections handled
            "request.qps",              // the number of client request
            "request.processing_time",  // the time in ms that cost to processing the requests.
                                        // it is not listed in Nginx official documentation,
                                        // looks like Tengine specific.
    };

    private static final String[] GAUGES = {
            "conn.active",  // the current number of active client connections including Waiting connections.
            "conn.reading", // the current number of connections where nginx is reading the request header.
            "conn.writing", // the current number of connections where nginx is writing the response back to the client.
            "conn.waiting", // the current number of idle client connections waiting for a request.
    };

    private static final String SPACE = "\\s+";

    /**
     * The default nginx host used to query nginx status
     */
    private static final String DEFAULT_NGINX_HOST = "127.0.0.1";

    /**
     * The default nginx port used to query nginx status
     */
    private static final int DEFAULT_NGINX_PORT = 80;

    /**
     * The default nginx status path used to query nginx status
     */
    private static final String DEFAULT_NGINX_STATUS_PATH = "/nginx_status";

    /**
     * The status host used to query nginx status
     */
    private static final String DEFAULT_STATUS_HOST = "127.0.0.1";

    /**
     * The host used to query nginx stats.
     * e.g. 127.0.0.1
     */
    private String nginxHost;

    /**
     * The host port used to query nginx stats.
     * e.g. 80
     */
    private int nginxPort;

    /**
     * The status path used to query nginx stats.
     * e.g. /nginx_status
     */
    private String statusPath;

    /**
     * The host to query
     */
    private String statusHost;

    /**
     * Store the counters metrics, e.g. total request, total processing time
     */
    private long[] counters;

    /**
     * Store the rates metrics, e.g. request per seconds
     */
    private double[] rates;

    /**
     * The average processing time for each request
     */
    private double averageRt;

    /**
     * Store the gauge metrics, e.g. current connections for reading request
     */
    private long[] gauges;

    /**
     * Whether it is the first collection or not, if true, rate will be calculated as 0.
     */
    private boolean firstCollection;

    /**
     * Store all the metrics
      */
    private Map<MetricName, Metric> metrics;

    public NginxGaugeSet() {
        this(DEFAULT_NGINX_HOST, DEFAULT_NGINX_PORT, DEFAULT_NGINX_STATUS_PATH, DEFAULT_STATUS_HOST,
                DEFAULT_DATA_TTL, TimeUnit.MILLISECONDS, Clock.defaultClock());
    }

    public NginxGaugeSet(long dataTTL, TimeUnit unit) {
        this(DEFAULT_NGINX_HOST, DEFAULT_NGINX_PORT, DEFAULT_NGINX_STATUS_PATH, DEFAULT_STATUS_HOST,
                dataTTL, unit, Clock.defaultClock());
    }

    public NginxGaugeSet(String nginxHost, int nginxPort, String statusPath, String statusHost,
                         long dataTTL, TimeUnit unit, Clock clock) {
        super(dataTTL, unit, clock);
        this.nginxHost = nginxHost;
        this.nginxPort = nginxPort;
        this.statusPath = statusPath;
        this.statusHost = statusHost;
        this.counters = new long[DELTA.length];
        this.rates = new double[DELTA.length-1]; // except processing time
        this.gauges = new long[GAUGES.length];
        this.firstCollection = true;
        metrics = new HashMap<MetricName, Metric>();
        populateGauges();
    }

    /**
     * Example data format:
     * $curl http://127.0.0.1:80/nginx_status'
       Active connections: 3
       server accepts handled requests request_time
        1756757 1756757 1750958 25530948
       Reading: 0 Writing: 1 Waiting: 2
     */
    @Override
    protected void getValueInternal() {
        clear();
        String[] data = fetchNginxMetrics();
        for (String line: data) {
            if (line.startsWith("Active")) {
                gauges[0] = Long.parseLong(line.split(SPACE)[2]);
            } else if (line.startsWith(" ")) {
                String[] tokens = line.trim().split(SPACE);
                if (tokens.length < 3) {
                    // skip invalid lines
                    continue;
                }
                long[] latest = new long[DELTA.length];
                // the first token is empty string
                latest[0] = Long.parseLong(tokens[0]);
                latest[1] = Long.parseLong(tokens[1]);
                latest[2] = Long.parseLong(tokens[2]);
                if (tokens.length >= 4) {
                    // this metrics may not exist in Nginx
                    latest[3] = Long.parseLong(tokens[3]);
                }

                if (!firstCollection) {
                    // calculate deltas
                    long[] deltas = new long[counters.length];
                    for (int i = 0; i < counters.length; i++) {
                        long temp = latest[i] - counters[i];
                        deltas[i] = temp >= 0 ? temp : Constants.NOT_AVAILABLE;
                    }

                    // calculate rates
                    long duration = clock.getTime() - lastCollectTime.get();
                    for (int i = 0; i < rates.length; i++) {
                        rates[i] = 1000.0d * deltas[i] / duration;
                    }

                    // calculate average rt
                    if (deltas[2] == 0) {
                        averageRt = 0.0d;
                    } else if (deltas[2] == Constants.NOT_AVAILABLE || deltas[3] == Constants.NOT_AVAILABLE) {
                        averageRt = Constants.NOT_AVAILABLE;
                    } else {
                        averageRt =  ((double) deltas[3]) / deltas[2];
                    }
                }

                // store counters
                System.arraycopy(latest, 0, counters, 0, counters.length);

                // mark first collection
                if (firstCollection) {
                    firstCollection = false;
                }
            } else if (line.startsWith("Reading:")) {
                String[] tokens = line.split(SPACE);
                gauges[1] = Long.parseLong(tokens[1]);
                gauges[2] = Long.parseLong(tokens[3]);
                gauges[3] = Long.parseLong(tokens[5]);
            }
        }
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        return metrics;
    }


    protected String[] fetchNginxMetrics() {
        NetUtils.Response response =  NetUtils.request(nginxHost, nginxPort, statusPath, statusHost);
        if (response.isSuccess()) {
            return response.getContent().split("\n");
        }
        return new String[0];
    }

    private void clear() {
        for (int i = 0; i < rates.length; i++) {
            rates[i] = 0.0d;
        }
        averageRt = 0.0d;
        for (int i = 0; i < gauges.length; i++) {
            gauges[i] = 0L;
        }
    }

    private void populateGauges() {
        for (int i = 0; i < rates.length; i++) {
            metrics.put(MetricName.build(DELTA[i]), new NginxRateGauge(i));
        }

        for (int i = 0; i < gauges.length; i++) {
            metrics.put(MetricName.build(GAUGES[i]), new NginxGauge(i));
        }

        metrics.put(MetricName.build("request.avg_rt"), new PersistentGauge<Double>() {
            @Override
            public Double getValue() {
                return averageRt;
            }
        });
    }

    private class NginxRateGauge extends PersistentGauge<Double> {

        private int index;

        public NginxRateGauge(int index) {
            this.index = index;
        }

        @Override
        public Double getValue() {
            try {
                refreshIfNecessary();
                return rates[index];
            } catch (Exception e) {
                return 0.0d;
            }
        }
    }

    private class NginxGauge extends PersistentGauge<Long> {

        private int index;

        public NginxGauge(int index) {
            this.index = index;
        }

        @Override
        public Long getValue() {
            try {
                refreshIfNecessary();
                return gauges[index];
            } catch (Exception e) {
                return 0L;
            }
        }
    }
}
