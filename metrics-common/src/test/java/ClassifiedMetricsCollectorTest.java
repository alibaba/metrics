import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.common.ClassifiedMetricsCollector;
import com.alibaba.metrics.common.CollectLevel;
import com.alibaba.metrics.common.MetricsCollector;
import com.alibaba.metrics.common.MetricsCollectorFactory;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import org.junit.Test;

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
}
