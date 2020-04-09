package com.alibaba.metrics.prometheus;

import com.alibaba.metrics.BucketCounterImpl;
import com.alibaba.metrics.ClusterHistogram;
import com.alibaba.metrics.ClusterHistogramImpl;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.CompassImpl;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.FastCompassImpl;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Histogram;
import com.alibaba.metrics.HistogramImpl;
import com.alibaba.metrics.ManualClock;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.MeterImpl;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.ReservoirType;
import com.alibaba.metrics.Timer;
import com.alibaba.metrics.TimerImpl;
import io.prometheus.client.Collector;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestAlibabaMetricsExports {

    private ManualClock clock = new ManualClock();

    @Test
    public void testExportCounter() {
        AlibabaMetricsExports exports = new AlibabaMetricsExports(clock);
        Counter c1 = new BucketCounterImpl(1, 5, clock);
        MetricManager.getIMetricManager().register("test",
                MetricName.build("prom.test.counter").level(MetricLevel.CRITICAL), c1);
        for (int i = 0; i < 10; i++) {
            c1.inc();
        }
        clock.addSeconds(1);
        List<Collector.MetricFamilySamples> samples = exports.collect();
        Assert.assertEquals(2, samples.size());
        Assert.assertEquals("prom_test_counter_count", samples.get(0).name);
        Assert.assertEquals(10, samples.get(0).samples.get(0).value, 0.0001d);
        Assert.assertEquals("prom_test_counter_bucket_count", samples.get(1).name);
        Assert.assertEquals(10, samples.get(1).samples.get(0).value, 0.0001d);
    }

    @Test
    public void testExportGauge() {
        AlibabaMetricsExports exports = new AlibabaMetricsExports(clock);
        final double random = Math.random();
        Gauge<Double> gauge = new Gauge<Double>() {
            @Override
            public Double getValue() {
                return random;
            }

            @Override
            public long lastUpdateTime() {
                return 0;
            }
        };
        MetricManager.getIMetricManager().register("test",
                MetricName.build("prom.test.gauge"), gauge);

        List<Collector.MetricFamilySamples> samples = exports.collect();
        Assert.assertEquals(1, samples.size());
        Assert.assertEquals("prom_test_gauge", samples.get(0).name);
        Assert.assertEquals(random, samples.get(0).samples.get(0).value, 0.0001d);
    }

    @Test
    public void testExportMeter() {
        AlibabaMetricsExports exports = new AlibabaMetricsExports(clock);
        Meter meter = new MeterImpl(clock, 5, 1);
        MetricManager.getIMetricManager().register("test",
                MetricName.build("prom.test.meter").level(MetricLevel.CRITICAL), meter);
        for (int i = 0; i < 10; i++) {
            meter.mark();
        }
        clock.addSeconds(1);
        List<Collector.MetricFamilySamples> samples = exports.collect();
        Assert.assertEquals(5, samples.size());
        Assert.assertEquals("prom_test_meter_count", samples.get(0).name);
        Assert.assertEquals(10, samples.get(0).samples.get(0).value, 0.0001d);
        Assert.assertEquals("prom_test_meter_bucket_count", samples.get(4).name);
        Assert.assertEquals(10, samples.get(4).samples.get(0).value, 0.0001d);
    }

    @Test
    public void testExportHistogram() {
        AlibabaMetricsExports exports = new AlibabaMetricsExports(clock);
        Histogram his = new HistogramImpl(ReservoirType.UNIFORM, 1, 5, clock);
        MetricManager.getIMetricManager().register("test",
                MetricName.build("prom.test.histogram").level(MetricLevel.CRITICAL), his);

        int min = Integer.MAX_VALUE;
        int max = 0;
        for (int i = 0; i < 10; i++) {
            int value = (int)(1000 * Math.random());
            his.update(value);
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }

        List<Collector.MetricFamilySamples> samples = exports.collect();
        Assert.assertEquals(6, samples.size());
        Assert.assertEquals("prom_test_histogram_summary", samples.get(0).name);
        Assert.assertEquals("should contain p50/p75/p95/p99 percentiles", 4, samples.get(0).samples.size());
        Assert.assertEquals("prom_test_histogram_min", samples.get(1).name);
        Assert.assertEquals(min, samples.get(1).samples.get(0).value, 0.0001d);
        Assert.assertEquals("prom_test_histogram_max", samples.get(2).name);
        Assert.assertEquals(max, samples.get(2).samples.get(0).value, 0.0001d);
        Assert.assertEquals("prom_test_histogram_mean", samples.get(3).name);
        Assert.assertEquals("prom_test_histogram_stddev", samples.get(4).name);
        Assert.assertEquals("prom_test_histogram_count", samples.get(5).name);
    }

    @Test
    public void testExportTimer() {
        AlibabaMetricsExports exports = new AlibabaMetricsExports(clock);
        Timer timer = new TimerImpl(ReservoirType.UNIFORM, clock, 1);
        MetricManager.getIMetricManager().register("test",
                MetricName.build("prom.test.timer").level(MetricLevel.CRITICAL), timer);

        int min = Integer.MAX_VALUE;
        int max = 0;
        for (int i = 0; i < 10; i++) {
            int value = (int)(1000 * Math.random());
            timer.update(value, TimeUnit.MILLISECONDS);
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }

        clock.addSeconds(1);

        List<Collector.MetricFamilySamples> samples = exports.collect();
        Assert.assertEquals(10, samples.size());
        Assert.assertEquals("prom_test_timer_summary", samples.get(0).name);
        Assert.assertEquals("should contain p50/p75/p95/p99 percentiles", 4, samples.get(0).samples.size());
        Assert.assertEquals("prom_test_timer_min", samples.get(1).name);
        Assert.assertEquals(min, samples.get(1).samples.get(0).value, 0.0001d);
        Assert.assertEquals("prom_test_timer_max", samples.get(2).name);
        Assert.assertEquals(max, samples.get(2).samples.get(0).value, 0.0001d);
        Assert.assertEquals("prom_test_timer_mean", samples.get(3).name);
        Assert.assertEquals("prom_test_timer_stddev", samples.get(4).name);
        Assert.assertEquals("prom_test_timer_count", samples.get(5).name);
        Assert.assertEquals(10, samples.get(5).samples.get(0).value, 0.0001d);
        Assert.assertEquals("prom_test_timer_m1", samples.get(6).name);
        Assert.assertEquals("prom_test_timer_m5", samples.get(7).name);
        Assert.assertEquals("prom_test_timer_m15", samples.get(8).name);
        Assert.assertEquals("prom_test_timer_bucket_count", samples.get(9).name);
        Assert.assertEquals(10, samples.get(9).samples.get(0).value, 0.0001d);
    }

    @Test
    public void testExportClusterHistogram() {
        AlibabaMetricsExports exports = new AlibabaMetricsExports(clock);
        long [] buckets = new long[]{0, 10, 100, 1000};
        ClusterHistogram ch = new ClusterHistogramImpl(buckets, 1, clock);
        MetricManager.getIMetricManager().register("test",
                MetricName.build("prom.test.ch").level(MetricLevel.CRITICAL), ch);
        for (int i = 0; i < 10; i++) {
            int value = (int)(1000 * Math.random());
            ch.update(value);
        }
        clock.addSeconds(1);

        List<Collector.MetricFamilySamples> samples = exports.collect();
        Assert.assertEquals(1, samples.size());
        Assert.assertEquals("prom_test_ch_cluster_percentile", samples.get(0).name);
        Assert.assertEquals(Collector.Type.HISTOGRAM, samples.get(0).type);
        Assert.assertEquals("should contain one more bucket called +Inf", buckets.length+1,
                samples.get(0).samples.size());


    }

    @Test
    public void testExportCompass() {
        AlibabaMetricsExports exports = new AlibabaMetricsExports(clock);
        Compass compass = new CompassImpl(ReservoirType.UNIFORM, clock,  5, 1, 2, 2);
        MetricManager.getIMetricManager().register("test",
                MetricName.build("prom.test.compass").level(MetricLevel.CRITICAL), compass);

        int min = Integer.MAX_VALUE;
        int max = 0;
        for (int i = 0; i < 10; i++) {
            int value = (int)(1000 * Math.random());
            compass.update(value, TimeUnit.MILLISECONDS);
            Compass.Context c = compass.time();
            if (i % 2 == 0) {
                c.success();
                c.markAddon("hit");
            } else {
                c.error("random");
            }
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }
        clock.addSeconds(1);

        List<Collector.MetricFamilySamples> samples = exports.collect();
        Assert.assertEquals(11, samples.size());
        Assert.assertEquals("prom_test_compass_summary", samples.get(0).name);
        Assert.assertEquals(Collector.Type.SUMMARY, samples.get(0).type);
        Assert.assertEquals("should contain p50/p75/p95/p99 percentiles", 4, samples.get(0).samples.size());
        Assert.assertEquals("prom_test_compass_min", samples.get(1).name);
        Assert.assertEquals(min, samples.get(1).samples.get(0).value, 0.0001d);
        Assert.assertEquals("prom_test_compass_max", samples.get(2).name);
        Assert.assertEquals(max, samples.get(2).samples.get(0).value, 0.0001d);
        Assert.assertEquals("prom_test_compass_mean", samples.get(3).name);
        Assert.assertEquals("prom_test_compass_stddev", samples.get(4).name);
        Assert.assertEquals("prom_test_compass_count", samples.get(5).name);
        Assert.assertEquals("prom_test_compass_m1", samples.get(6).name);
        Assert.assertEquals("prom_test_compass_m5", samples.get(7).name);
        Assert.assertEquals("prom_test_compass_m15", samples.get(8).name);
        Assert.assertEquals("prom_test_compass_bucket_count", samples.get(9).name);
        Assert.assertEquals("success count", 5, samples.get(9).samples.get(0).value, 0.0001d);
        Assert.assertEquals("failure count", 5, samples.get(9).samples.get(1).value, 0.0001d);
        Assert.assertEquals("prom_test_compass_addon_bucket_count", samples.get(10).name);
        Assert.assertEquals("hit count", 5, samples.get(10).samples.get(0).value, 0.0001d);
    }

    @Test
    public void testExportFastCompass() {
        AlibabaMetricsExports exports = new AlibabaMetricsExports(clock);
        FastCompass fc = new FastCompassImpl(1, 5, clock, 5);
        MetricManager.getIMetricManager().register("test",
                MetricName.build("prom.test.fastcompass").level(MetricLevel.CRITICAL), fc);

        int successSum = 0;
        int failureSum = 0;

        for (int i = 0; i < 10; i++) {
            int value = (int)(1000 * Math.random());
            if (i % 2 == 0) {
                fc.record(value, "success");
                successSum += value;
            } else {
                fc.record(value, "failure");
                failureSum += value;
            }
        }
        clock.addSeconds(1);

        List<Collector.MetricFamilySamples> samples = exports.collect();
        Assert.assertEquals(1, samples.size());
        Assert.assertEquals("prom_test_fastcompass_bucket_count", samples.get(0).name);
        Assert.assertEquals("should contain success/failure count and sum", 4, samples.get(0).samples.size());
        Assert.assertEquals("prom_test_fastcompass_bucket_count", samples.get(0).samples.get(0).name);
        Assert.assertEquals("success count", 5, samples.get(0).samples.get(0).value, 0.0001d);
        Assert.assertEquals("failure count", 5, samples.get(0).samples.get(1).value, 0.0001d);
        Assert.assertEquals("success sum", successSum, samples.get(0).samples.get(2).value, 0.0001d);
        Assert.assertEquals("failure sum", failureSum, samples.get(0).samples.get(3).value, 0.0001d);
    }

    @After
    public void clear() {
        MetricManager.getIMetricManager().clear();
    }
}
