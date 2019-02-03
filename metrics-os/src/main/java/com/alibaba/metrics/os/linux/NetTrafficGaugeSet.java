package com.alibaba.metrics.os.linux;

import com.alibaba.metrics.CachedMetricSet;
import com.alibaba.metrics.Clock;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetTrafficGaugeSet extends CachedMetricSet {

    private static final Logger logger = LoggerFactory.getLogger(NetTrafficGaugeSet.class);

    private static final String DEFAULT_FILE_PATH = "/proc/net/dev";

    /**
     * See https://github.com/OpenTSDB/tcollector/blob/master/collectors/0/ifstat.py
     */
    private static final String STR_PATTERN = "\\s*" +
            "(" +
                "eth\\d+|" +
                "em\\d+_\\d+/\\d+|em\\d+_\\d+|em\\d+" +
                "p\\d+p\\d+_\\d+/\\d+|p\\d+p\\d+_\\d+|p\\d+p\\d+|" +
                "(?:" + // Start of 'predictable network interface names'
                    "(?:en|sl|wl|ww)" +
                    "(?:" +
                        "b\\d+|" +                                              // # BCMA bus
                        "c[0-9a-f]+|" +                                         // # CCW bus group
                        "o\\d+(?:d\\d+)?|" +                                    // # On-board device
                        "s\\d+(?:f\\d+)?(?:d\\d+)?|" +                          // # Hotplug slots
                        "x[0-9a-f]+|" +                                         // # Raw MAC address
                        "p\\d+s\\d+(?:f\\d+)?(?:d\\d+)?|" +                     // # PCI geographic loc
                        "p\\d+s\\d+(?:f\\d+)?(?:u\\d+)*(?:c\\d+)?(?:i\\d+)?" +  // # USB
                    ")" +
                ")" +
            "):(.*)";

    private static final Pattern NET_PATTERN = Pattern.compile(STR_PATTERN);

    private static final String[] FIELDS = {
            "net.in.bytes", "net.in.packets", "net.in.errs", "net.in.dropped",
            "net.in.fifo.errs", "net.in.frame.errs", "net.in.compressed", "net.in.multicast",
            "net.out.bytes", "net.out.packets", "net.out.errs", "net.out.dropped",
            "net.out.fifo.errs", "net.out.collisions", "net.out.carrier.errs", "net.out.compressed"
    };

    private static final String DELIM = "\\s+";

    private String filePath;

    private Map<String, Long[]> countByFace;
    private Map<String, Double[]> rateByFace;

    private Map<MetricName, Metric> gauges;

    public NetTrafficGaugeSet() {
        this(DEFAULT_FILE_PATH);
    }

    public NetTrafficGaugeSet(String filePath) {
        this(filePath, DEFAULT_DATA_TTL, TimeUnit.MILLISECONDS, Clock.defaultClock());
    }

    public NetTrafficGaugeSet(long dataTTL, TimeUnit unit) {
        this(DEFAULT_FILE_PATH, dataTTL, unit, Clock.defaultClock());
    }

    public NetTrafficGaugeSet(String filePath, long dataTTL, TimeUnit unit, Clock clock) {
        super(dataTTL, unit, clock);
        this.gauges = new HashMap<MetricName, Metric>();
        this.filePath = filePath;
        countByFace = new HashMap<String, Long[]>();
        rateByFace = new HashMap<String, Double[]>();
        getValueInternal();
        populateGauges();
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        return gauges;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    protected void getValueInternal() {
        try {
            List<String> lines = FileUtils.readFileAsStringArray(filePath);

            for (String line: lines) {
                Matcher netMatcher = NET_PATTERN.matcher(line);

                if (netMatcher.matches()) {
                    String face = netMatcher.group(1);
                    if (countByFace.get(face) == null) {
                        Long[] counts = new Long[FIELDS.length];
                        Double[] rates = new Double[FIELDS.length];
                        for (int i=0; i<counts.length; i++) {
                            counts[i] = 0L;
                            rates[i] = 0.0d;
                        }
                        countByFace.put(face, counts);
                        rateByFace.put(face, rates);
                    }

                    String[] stats = netMatcher.group(2).trim().split(DELIM);
                    for (int i = 0; i < stats.length; i++) {
                        try {
                            long count = Long.parseLong(stats[i]);
                            long delta = count - countByFace.get(face)[i];
                            countByFace.get(face)[i] = count;
                            long duration = clock.getTime() - lastCollectTime.get();
                            rateByFace.get(face)[i] = 1000.0d * delta / duration;
                        } catch (Exception e) {
                            logger.warn("Error parsing net traffic metrics:", e);
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("Error during reading file {}", filePath, e);
        }
    }

    private class NetGauge extends PersistentGauge<Double> {

        private String face;
        private int index;

        public NetGauge(String face, int index) {
            this.face = face;
            this.index = index;
        }

        @Override
        public Double getValue() {
            try {
                refreshIfNecessary();
                return rateByFace.get(face)[index];
            } catch (Exception e) {
                return 0.0d;
            }
        }
    }

    private void populateGauges() {
        for (Map.Entry<String, Long[]> entry: countByFace.entrySet()) {
            for (int i = 0; i < entry.getValue().length; i++) {
                gauges.put(MetricName.build(FIELDS[i]).tagged("face", entry.getKey()),
                        new NetGauge(entry.getKey(), i));
            }
        }
    }
}
