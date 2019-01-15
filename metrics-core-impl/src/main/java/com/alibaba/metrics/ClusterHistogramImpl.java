package com.alibaba.metrics;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangtao 2019-01-16 10:26
 */
public class ClusterHistogramImpl extends ClusterHistogram {

    private static final int DEFAULT_NUM_OF_BUCKETS = 5;

    private BucketCounter[] values;

    public ClusterHistogramImpl(int interval, Clock clock) {
        this(null, interval, clock);
    }

    public ClusterHistogramImpl(long[] buckets, int interval, Clock clock) {
        super(buckets);
        values = new BucketCounter[buckets.length+1];
        for (int i = 0; i < values.length; i++) {
            values[i] = new BucketCounterImpl(interval, DEFAULT_NUM_OF_BUCKETS, clock, false);
        }
    }

    @Override
    public void update(long value) {
        // TODO use binary search to improve performance
        for (int i = 0; i < values.length; i++) {
            if (i == buckets.length) {
                values[i].update();
                break;
            }
            if (value < buckets[i]) {
                values[i].update();
                break;
            }

        }
    }


    @Override
    public Map<Long, Map<Long, Long>> getBucketValues(long startTime) {
        Map<Long, Map<Long, Long>> result = new HashMap<Long, Map<Long, Long>>();
        for (int i = 0; i < values.length; i++) {
            Map<Long, Long> counts = values[i].getBucketCounts(startTime);
            for (Map.Entry<Long, Long> entry : counts.entrySet()) {
                if (!result.containsKey(entry.getKey())) {
                    result.put(entry.getKey(), new HashMap<Long, Long>());
                }
                Map<Long, Long> bucketAndValue = result.get(entry.getKey());
                bucketAndValue.put(i == buckets.length ? Long.MAX_VALUE : buckets[i], entry.getValue());
            }
        }
        return result;
    }

    @Override
    public long lastUpdateTime() {
        // TODO implement this
        return 0;
    }
}
