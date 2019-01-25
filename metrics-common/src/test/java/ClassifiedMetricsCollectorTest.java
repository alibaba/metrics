import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.ClusterHistogram;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.common.MetricObject;
import org.junit.Assert;
import org.junit.Test;

import com.alibaba.metrics.common.ClassifiedMetricsCollector;
import com.alibaba.metrics.common.CollectLevel;
import com.alibaba.metrics.common.MetricsCollector;
import com.alibaba.metrics.common.MetricsCollectorFactory;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClassifiedMetricsCollectorTest {

    @Test
    public void adjustStartTimeTest() {

        MetricsCollector collector = MetricsCollectorFactory.createNew(CollectLevel.CLASSIFIER, null,
                1000, 1000);

        ClassifiedMetricsCollector classifiedMetricsCollector = (ClassifiedMetricsCollector) collector;
        MetricsCollectPeriodConfig metricsCollectPeriodConfig = new MetricsCollectPeriodConfig();

        long timestamp = System.currentTimeMillis();

        MetricLevel level = null;


        level = MetricLevel.NORMAL;
        int interval = metricsCollectPeriodConfig.period(level) * 1000;
        long startTime = timestamp - 90000;
        long endTime = timestamp;
        long adjustedStartTime = classifiedMetricsCollector.adjustStartTime(startTime, endTime, interval, level);

        assert adjustedStartTime == endTime - interval * 2;

        level = MetricLevel.TRIVIAL;
        interval = metricsCollectPeriodConfig.period(level) * 1000;
        startTime = timestamp - 90000;
        endTime = timestamp;
        adjustedStartTime = classifiedMetricsCollector.adjustStartTime(startTime, endTime, interval, level);

        assert adjustedStartTime == startTime;

        level = MetricLevel.CRITICAL;
        interval = metricsCollectPeriodConfig.period(level) * 1000;
        startTime = timestamp - 90000;
        endTime = timestamp;
        adjustedStartTime = classifiedMetricsCollector.adjustStartTime(startTime, endTime, interval, level);

        assert adjustedStartTime == endTime - interval * 10;


    }

    @Test
    public void testCollectClusterHistogram() {
        double rateFactor = TimeUnit.SECONDS.toSeconds(1);
        double durationFactor = 1.0 / TimeUnit.MILLISECONDS.toNanos(1);
        ClassifiedMetricsCollector collector = (ClassifiedMetricsCollector)MetricsCollectorFactory.createNew(
                CollectLevel.CLASSIFIER, Collections.EMPTY_MAP, rateFactor, durationFactor, null);
        Map<MetricLevel, Long> lastTimestamp = new HashMap<MetricLevel, Long>();
        lastTimestamp.put(MetricLevel.NORMAL, 90000L);
        collector.setLastTimestamp(lastTimestamp);
        ClusterHistogram ch = mock(ClusterHistogram.class);
        Map<Long, Map<Long, Long>> mockData = new HashMap<Long, Map<Long, Long>>();
        Map<Long, Long> mockedBuckets = new HashMap<Long, Long>();
        mockedBuckets.put(10L, 1L);
        mockedBuckets.put(100L, 2L);
        mockedBuckets.put(1000L, 3L);
        mockedBuckets.put(Long.MAX_VALUE, 4L);
        mockData.put(105000L, mockedBuckets);
        when(ch.getBucketValues(105000L)).thenReturn(mockData);
        when(ch.getBuckets()).thenReturn(new long[]{10L, 100L, 1000L, Long.MAX_VALUE});
        collector.collect(MetricName.build("test"), ch, 120000L);
        Map<MetricLevel, Map<Long, List<MetricObject>>> results = collector.getMetrics();
        Assert.assertEquals(1, results.size());
        Map<Long, List<MetricObject>> levelMetrics = results.get(MetricLevel.NORMAL);
        Assert.assertNotNull(levelMetrics);
        List<MetricObject> objects = levelMetrics.get(105000L);
        Assert.assertNotNull(objects);
        Assert.assertEquals(4, objects.size());
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("bucket", "10");
        Assert.assertEquals(4, objects.size());
        MetricObject obj = MetricObject.named("test.cluster_percentile").withLevel(MetricLevel.NORMAL)
                .withTimestamp(105000L).withTags(tags).withType(MetricObject.MetricType.PERCENTILE)
                .withValue(1L).build();
        Assert.assertTrue(objects.contains(obj));
    }
}
