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
        int low = 0;
        int high = buckets.length - 1;
        int i = 0;
        while (low <= high) {
            i = low + (high - low) / 2;
            if (value < buckets[i] && (i == 0 || value >= buckets[i-1])) {
                values[i].update();
                break;
            }
            if (value >= buckets[i]) {
                low = i + 1;
            } else {
                high = i - 1;
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
                bucketAndValue.put(buckets[i], entry.getValue());
            }
        }
        return result;
    }

    @Override
    public long lastUpdateTime() {
        long latest = 0;
        for (BucketCounter value : values) {
            if (value.lastUpdateTime() > latest) {
                latest = value.lastUpdateTime();
            }
        }
        return latest;
    }
}
