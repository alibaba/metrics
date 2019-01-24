package com.alibaba.metrics.os.windows;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.hyperic.sigar.NetInterfaceStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.metrics.CachedMetricSet;
import com.alibaba.metrics.Clock;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.PersistentGauge;

import static com.alibaba.metrics.os.utils.SystemInfoUtils.*;

public class TcpGaugeSet extends CachedMetricSet{

    private static final Logger logger = LoggerFactory.getLogger(TcpGaugeSet.class);

    private Map<MetricName, Metric> gauges = new HashMap<MetricName, Metric>();

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

    public TcpGaugeSet(long dataTTL, TimeUnit unit, Clock clock) {

        super(dataTTL, unit, clock);
        this.counters = new long[METRICS.length];
        this.rates = new double[METRICS.length];
        this.gauges = new HashMap<MetricName, Metric>();
        this.firstCollection = true;
        populateGauges();

    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        return gauges;
    }

    @Override
    protected void getValueInternal() {

//        String[] lst = sigar.getNetInterfaceList();
//        NetInterfaceStat netStat;
//
//        if (lst != null && lst.length > 2){
//            netStat = sigar.getNetInterfaceStat(lst[1]);
//        }

//        netStat.get
//
//        netInfoList.add(new SigarInfoEntity(nIfStat.getRxPackets() + "",
//                "接收的总包裹数" + i));
//        netInfoList.add(new SigarInfoEntity(nIfStat.getTxPackets() + "",
//                "发送的总包裹数" + i));
//        netInfoList.add(new SigarInfoEntity(nIfStat.getRxBytes() + "",
//                "接收到的总字节数" + i));
//        netInfoList.add(new SigarInfoEntity(nIfStat.getTxBytes() + "",
//                "发送的总字节数" + i));
//        netInfoList.add(new SigarInfoEntity(nIfStat.getRxErrors() + "",
//                "接收到的错误包数" + i));
//        netInfoList.add(new SigarInfoEntity(nIfStat.getTxErrors() + "",
//                "发送数据包时的错误数" + i));
//        netInfoList.add(new SigarInfoEntity(nIfStat.getRxDropped() + "",
//                "接收时丢弃的包数" + i));
//        netInfoList.add(new SigarInfoEntity(nIfStat.getTxDropped() + "",
//                "发送时丢弃的包数" + i));
//
//        long[] counts = {
//                Long.parseLong(tokens[5]),  // tcp.active_opens
//                Long.parseLong(tokens[6]),  // tcp.passive_opens
//                Long.parseLong(tokens[7]),  // tcp.attempt_fails
//                Long.parseLong(tokens[8]),  // tcp.estab_resets
//                Long.parseLong(tokens[10]), // tcp.in_segs
//                Long.parseLong(tokens[11]), // tcp.out_segs
//                Long.parseLong(tokens[12]), // tcp.retran_segs
//                Long.parseLong(tokens[13]), // tcp.in_errs
//                Long.parseLong(tokens[14]), // tcp.out_rsts
//            };

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
