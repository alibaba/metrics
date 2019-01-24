package com.alibaba.metrics.os.linux;

import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.ManualClock;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TcpGaugeSetTest {

    @Test
    public void testRegexStartWith() {

        String desc = "Tcp: RtoAlgorithm RtoMin RtoMax MaxConn ActiveOpens PassiveOpens";
        String data = "Tcp: 1 200 120000 -1 6463920 8949291 793366 541849 10";

        String[] aaa = data.split("\\s+");
        Assert.assertTrue(Character.isDigit(aaa[1].charAt(0)));

        String[] bbb = desc.split("\\s+");
        Assert.assertFalse(Character.isDigit(bbb[1].charAt(0)));
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testTcpGaugeSet() {
        ManualClock clock = new ManualClock();

        TcpGaugeSet tcpGaugeSet = new TcpGaugeSet(
                "src/test/resources/proc_net_snmp", 10, TimeUnit.MILLISECONDS, clock);

        Map<MetricName, Metric> metrics = tcpGaugeSet.getMetrics();

        Assert.assertEquals(11, metrics.keySet().size());

        clock.addMillis(20);

        final String[] METRICS = {
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

        Gauge<Double> tcpActiveOpens = (Gauge<Double>)metrics.get(MetricName.build("tcp.active_opens"));
        Gauge<Double> tcpPassiveOpens = (Gauge<Double>)metrics.get(MetricName.build("tcp.passive_opens"));
        Gauge<Double> attemptFails = (Gauge<Double>)metrics.get(MetricName.build("tcp.attempt_fails"));
        Gauge<Long> currentEstab = (Gauge<Long>)metrics.get(MetricName.build("tcp.current_estab"));
        Gauge<Double> outSegs = (Gauge<Double>)metrics.get(MetricName.build("tcp.out_segs"));
        Gauge<Double> retranSeg = (Gauge<Double>)metrics.get(MetricName.build("tcp.retran_segs"));
        Gauge<Double> retranRatio = (Gauge<Double>)metrics.get(MetricName.build("tcp.retran_ratio"));

        Assert.assertEquals(0.0d, tcpActiveOpens.getValue(), 0.0001d);
        Assert.assertEquals(0.0d, tcpPassiveOpens.getValue(), 0.0001d);
        Assert.assertEquals(0.0d, attemptFails.getValue(), 0.0001d);

        clock.addMillis(200);

        tcpGaugeSet.setFilePath("src/test/resources/proc_net_snmp_2");

        Assert.assertEquals(10000.0d, tcpActiveOpens.getValue(), 0.0001d);
        Assert.assertEquals(1000.0d, tcpPassiveOpens.getValue(), 0.0001d);
        Assert.assertEquals(0.0d, attemptFails.getValue(), 0.0001d);

        clock.addMillis(200);

        tcpGaugeSet.setFilePath("src/test/resources/proc_net_snmp_3");

        Assert.assertEquals(0.0d, tcpActiveOpens.getValue(), 0.0001d);
        Assert.assertEquals(0.0d, tcpPassiveOpens.getValue(), 0.0001d);
        Assert.assertEquals(15000.0d, attemptFails.getValue(), 0.0001d);
        Assert.assertEquals(10L, currentEstab.getValue().longValue());
        Assert.assertEquals(2500.0d, outSegs.getValue(), 0.001d);
        Assert.assertEquals(1000.0d, retranSeg.getValue(), 0.001d);
        Assert.assertEquals(0.4d, retranRatio.getValue(), 0.001d);
    }
}
