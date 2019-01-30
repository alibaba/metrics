import com.alibaba.metrics.ClusterHistogram;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.common.CollectLevel;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.common.MetricsCollector;
import com.alibaba.metrics.common.MetricsCollectorFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author wangtao 2019-01-24 17:33
 */
public class MetricsCollectTest {

    @Test
    public void testCollectClusterHistogram() {
        double rateFactor = TimeUnit.SECONDS.toSeconds(1);
        double durationFactor = 1.0 / TimeUnit.MILLISECONDS.toNanos(1);
        MetricsCollector collector = MetricsCollectorFactory.createNew(
                CollectLevel.NORMAL, Collections.EMPTY_MAP, rateFactor, durationFactor, null);
        ClusterHistogram ch = mock(ClusterHistogram.class);
        Map<Long, Map<Long, Long>> mockData = new HashMap<Long, Map<Long, Long>>();
        Map<Long, Long> mockedBuckets = new HashMap<Long, Long>();
        mockedBuckets.put(10L, 1L);
        mockedBuckets.put(100L, 2L);
        mockedBuckets.put(1000L, 3L);
        mockedBuckets.put(Long.MAX_VALUE, 4L);
        Map<Long, Long> mockedBuckets2 = new HashMap<Long, Long>();
        mockedBuckets2.put(10L, 5L);
        mockedBuckets2.put(100L, 4L);
        mockedBuckets2.put(1000L, 8L);
        mockedBuckets2.put(Long.MAX_VALUE, 7L);
        mockData.put(105000L, mockedBuckets);
        mockData.put(120000L, mockedBuckets2);
        when(ch.getBucketValues(105000L)).thenReturn(mockData);
        when(ch.getBuckets()).thenReturn(new long[]{10L, 100L, 1000L, Long.MAX_VALUE});
        collector.collect(MetricName.build("test"), ch, 120000L);
        List<MetricObject> objects = collector.build();

        Map<String, String> tags = new HashMap<String, String>();
        tags.put("bucket", "10");
        Assert.assertEquals(4, objects.size());
        MetricObject obj = MetricObject.named("test.cluster_percentile").withLevel(MetricLevel.NORMAL)
                .withTimestamp(105000L).withTags(tags).withType(MetricObject.MetricType.PERCENTILE)
                .withValue(1L).build();
        Assert.assertTrue(objects.contains(obj));
    }
}
