package com.alibaba.metrics;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MetricManagerTest {

    @Test
    public void testGetHistogramWithType() {
        Histogram his = MetricManager.getHistogram("test", MetricName.build("AAA"),
                ReservoirType.EXPONENTIALLY_DECAYING);
        assertThat(his.getSnapshot()).isInstanceOf(WeightedSnapshot.class);

        Histogram his2 = MetricManager.getHistogram("test", MetricName.build("BBB"),
                ReservoirType.BUCKET);
        assertThat(his2.getSnapshot()).isInstanceOf(BucketSnapshot.class);

        Histogram his3 = MetricManager.getHistogram("test", MetricName.build("CCC"),
                ReservoirType.SLIDING_TIME_WINDOW);
        assertThat(his3.getSnapshot()).isInstanceOf(UniformSnapshot.class);
    }

    @Test
    public void testGetTimerWithType() {
        Timer t = MetricManager.getTimer("test", MetricName.build("AAA1"),
                ReservoirType.EXPONENTIALLY_DECAYING);
        assertThat(t.getSnapshot()).isInstanceOf(WeightedSnapshot.class);

        Timer t2 = MetricManager.getTimer("test", MetricName.build("BBB1"),
                ReservoirType.BUCKET);
        assertThat(t2.getSnapshot()).isInstanceOf(BucketSnapshot.class);

        Timer t3 = MetricManager.getTimer("test", MetricName.build("CCC1"),
                ReservoirType.SLIDING_TIME_WINDOW);
        assertThat(t3.getSnapshot()).isInstanceOf(UniformSnapshot.class);
    }

    @Test
    public void testGetCompassWithType() {
        Compass c = MetricManager.getCompass("test", MetricName.build("AAA2"),
                ReservoirType.EXPONENTIALLY_DECAYING);
        assertThat(c.getSnapshot()).isInstanceOf(WeightedSnapshot.class);

        Compass c2 = MetricManager.getCompass("test", MetricName.build("BBB2"),
                ReservoirType.BUCKET);
        assertThat(c2.getSnapshot()).isInstanceOf(BucketSnapshot.class);

        Compass c3 = MetricManager.getCompass("test", MetricName.build("CCC2"),
                ReservoirType.SLIDING_TIME_WINDOW);
        assertThat(c3.getSnapshot()).isInstanceOf(UniformSnapshot.class);
    }
}
