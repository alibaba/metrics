package com.alibaba.metrics.os.linux;

import com.alibaba.metrics.CachedMetricSet;
import com.alibaba.metrics.Clock;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.PersistentGauge;
import com.alibaba.metrics.os.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TcpGaugeSet extends CachedMetricSet {

    private static final Logger logger = LoggerFactory.getLogger(SystemMemoryGaugeSet.class);

    private static final String DEFAULT_FILE_PATH = "/proc/net/snmp";

    /**
     * Note that all the metrics are counters, EXCEPT for tcp.curr_estab
     * see more: http://perthcharles.github.io/2015/11/09/wiki-rfc2012-snmp-proc/
     */
    private static final String[] METRICS = {
            "tcp.active_opens",   // active opening connections
            "tcp.passive_opens",  // passive opening connections
            "tcp.attempt_fails",  // number of failed connection attempts
            "tcp.estab_resets",   // number of resets that have occurred at ESTABLISHED
            "tcp.in_segs",        // incoming segments
            "tcp.out_segs",       // outgoing segments
            "tcp.retran_segs",    // number of retran segements
            "tcp.in_errs",        // incoming segments with errs, e.g. checksum error
            "tcp.out_rsts",       // outgoing segments with resets
    };

    /**
     * Number of connections that is in ESTABLISHED state, which is a gauge value
     */
    private static final String CURRENT_ESTAB = "tcp.current_estab";
    /**
     * The ratio of retran segments and outgoing segments
     */
    private static final String RETRAN_RATIO = "tcp.retran_ratio";

    private static final String SPACE_REGEX = "\\s+";

    private String filePath;

    /**
     * The counters of the metrics
     */
    private long[] counters;

    /**
     * The rate of the counters, e.g. active open connections per seconds
     */
    private double[] rates;

    /**
     * The number of tcp connections that is in established state.
     * Note that this data is not a counter
     */
    private long currentEstab;

    /**
     * retranRatio = (RetransSegs－last RetransSegs) ／ (OutSegs－last OutSegs)
     */
    private double retranRatio;

    /**
     * Whether it is the first collection or not, if true, rate will be calculated as 0.
     */
    private boolean firstCollection;

    private Map<MetricName, Metric> gauges;

    public TcpGaugeSet() {
        this(DEFAULT_FILE_PATH, DEFAULT_DATA_TTL, TimeUnit.MILLISECONDS, Clock.defaultClock());
    }

    public TcpGaugeSet(String filePath) {
        this(filePath, DEFAULT_DATA_TTL, TimeUnit.MILLISECONDS, Clock.defaultClock());
    }

    public TcpGaugeSet(long dataTTL, TimeUnit unit) {
        this(DEFAULT_FILE_PATH, dataTTL, unit, Clock.defaultClock());
    }

    public TcpGaugeSet(String filePath, long dataTTL, TimeUnit unit, Clock clock) {
        super(dataTTL, unit, clock);
        this.filePath = filePath;
        this.counters = new long[METRICS.length];
        this.rates = new double[METRICS.length];
        this.gauges = new HashMap<MetricName, Metric>();
        this.firstCollection = true;
        populateGauges();
    }

    /**
     * Basically, we are seeking the second line and want to filter the first line:
     * Tcp: RtoAlgorithm RtoMin RtoMax MaxConn ActiveOpens PassiveOpens AttemptFails EstabResets CurrEstab InSegs OutSegs RetransSegs InErrs OutRsts
     * Tcp: 1 200 120000 -1 6463920 8949291 793366 541849 10 6107706493 3268075709 24668021 1 4964874
     * see https://tools.ietf.org/html/rfc2012 section 2
     * The first part after 'Tcp:' (tcpRtoAlgorithm), have only valid values of 1,2,3,4
     */
    @Override
    protected void getValueInternal() {
        try {
            List<String> lines = FileUtils.readFileAsStringArray(filePath);
            for (String line: lines) {
                if (line.startsWith("Tcp:")) {
                    String[] tokens = line.split(SPACE_REGEX);
                    if (Character.isDigit(tokens[1].charAt(0))) {
                        // parse counters
                        long[] counts = {
                            Long.parseLong(tokens[5]),  // tcp.active_opens
                            Long.parseLong(tokens[6]),  // tcp.passive_opens
                            Long.parseLong(tokens[7]),  // tcp.attempt_fails
                            Long.parseLong(tokens[8]),  // tcp.estab_resets
                            Long.parseLong(tokens[10]), // tcp.in_segs
                            Long.parseLong(tokens[11]), // tcp.out_segs
                            Long.parseLong(tokens[12]), // tcp.retran_segs
                            Long.parseLong(tokens[13]), // tcp.in_errs
                            Long.parseLong(tokens[14]), // tcp.out_rsts
                        };

                        if (!firstCollection) {
                            // calculate deltas
                            long[] deltas = new long[counters.length];
                            for (int i = 0; i < counters.length; i++) {
                                deltas[i] = counts[i] - counters[i];
                            }

                            // calculate rates
                            long duration = clock.getTime() - lastCollectTime.get();

                            for (int i = 0; i < counters.length; i++) {
                                rates[i] = 1000.0d * deltas[i] / duration;
                            }

                            // calculate tcp retran rate
                            retranRatio = 1.0d * deltas[6] / deltas[5];
                        }

                        // store counters
                        System.arraycopy(counts, 0, counters, 0, counters.length);

                        // tcp.current_estab, which is not a counter
                        currentEstab = Long.parseLong(tokens[9]);

                        // mark first collection
                        if (firstCollection) {
                            firstCollection = false;
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("Error during reading file {}", filePath, e);
        }
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        return gauges;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private void populateGauges() {
        for (int i = 0; i < METRICS.length; i++) {
            gauges.put(MetricName.build(METRICS[i]), new TcpGauge(i));
        }
        // add tcp.current_estab
        gauges.put(MetricName.build(CURRENT_ESTAB), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return currentEstab;
            }
        });
        // add tcp.retran_ratio
        gauges.put(MetricName.build(RETRAN_RATIO), new PersistentGauge<Double>() {
            @Override
            public Double getValue() {
                return retranRatio;
            }
        });
    }

    private class TcpGauge extends PersistentGauge<Double> {

        private int index;

        public TcpGauge(int index) {
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
}
