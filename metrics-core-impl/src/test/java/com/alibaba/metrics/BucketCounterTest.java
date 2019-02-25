package com.alibaba.metrics;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class BucketCounterTest {

    @Test
    public void testSlowUpdate() {
        BucketCounter bucketCounter = new BucketCounterImpl(1, 5, Clock.defaultClock());
        for (int i = 0; i < 10; i++) {
            bucketCounter.update();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 20; i++) {
            bucketCounter.update();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // sleep for another 1 seconds
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Long[] expected = {20L, 10L};
        Assert.assertArrayEquals(expected, bucketCounter.getBucketCounts().values().toArray(new Long[2]));
    }

    @Test
    public void testSingleThreadUpdate() {
        BucketCounter bucketCounter = new BucketCounterImpl(1, 5, Clock.defaultClock());
        for (int k = 1; k <= 7; k++) {
            for (int i = 0; i < k*10; i++) {
                bucketCounter.update();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Long[] expected = {70L, 60L, 50L, 40L, 30L};
        Assert.assertArrayEquals(expected, bucketCounter.getBucketCounts().values().toArray(new Long[5]));
    }

    @Test
    public void testLatestIndexAtFirst() {
        BucketCounter bucketCounter = new BucketCounterImpl(1, 5, Clock.defaultClock());
        for (int k = 1; k <= 6; k++) {
            for (int i = 0; i < k*10; i++) {
                bucketCounter.update();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Long[] expected = {60L, 50L, 40L, 30L, 20L};
        Assert.assertArrayEquals(expected, bucketCounter.getBucketCounts().values().toArray(new Long[5]));
    }

    @Test
    public void testMultiThreadsUpdate() {
        int ROUND = 1;
        for (int r = 0; r < ROUND; r++) {
            System.out.println("Round: " + r);
            BucketCounter bucketCounter = new BucketCounterImpl(1, 15, Clock.defaultClock());
            BizThread[] bizThreads = new BizThread[80];
            for (int i = 0; i < bizThreads.length; i++) {
                bizThreads[i] = new BizThread(bucketCounter);
                bizThreads[i].start();
            }

            for (BizThread bizThread : bizThreads) {
                try {
                    bizThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Long[] expected = {8000L, 7200L, 6400L, 5600L, 4800L, 4000L, 3200L, 2400L, 1600L, 800L};
            long expectedTotal = 0L;
            for (Long exp : expected) {
                expectedTotal += exp;
            }
            Long[] actual = bucketCounter.getBucketCounts().values().toArray(new Long[10]);
            long actualTotal = 0L;
            for (Long act : actual) {
                if (act != null) {
                    actualTotal += act;
                }
            }
            Assert.assertEquals(Arrays.toString(actual), expectedTotal, actualTotal);
//            Assert.assertArrayEquals(Arrays.toString(actual), expected, actual);
        }
    }

    private static class BizThread extends Thread {

        private BucketCounter bucketCounter;

        BizThread(BucketCounter bucketCounter) {
            this.bucketCounter = bucketCounter;
        }

        @Override
        public void run() {
            for (int k = 1; k <= 10; k++) {
                for (int i = 0; i < k*10; i++) {
                    bucketCounter.update();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void test10sInterval() {
        ManualClock manual = new ManualClock();
        BucketCounter bucketCounter = new BucketCounterImpl(10, 5, manual);
        for (int k = 1; k <= 7; k++) {
            for (int i = 0; i < k*10; i++) {
                bucketCounter.update();
            }
            manual.addSeconds(10);
        }

        Long[] expected = {70L, 60L, 50L, 40L, 30L};
        Assert.assertArrayEquals(expected, bucketCounter.getBucketCounts().values().toArray(new Long[5]));
        Assert.assertEquals(70L + 60L + 50L + 40L + 30L + 20L + 10L, bucketCounter.getCount());
    }

    @Test
    public void testQueryWithStartTime() {
        ManualClock manual = new ManualClock();
        BucketCounter bucketCounter = new BucketCounterImpl(10, 5, manual);
        for (int k = 1; k <= 7; k++) {
            for (int i = 0; i < k*10; i++) {
                bucketCounter.update();
            }
            manual.addSeconds(10);
        }

        Assert.assertArrayEquals(new Long[]{}, bucketCounter.getBucketCounts(70000L).values().toArray(new Long[0]));
        Assert.assertArrayEquals(new Long[]{70L}, bucketCounter.getBucketCounts(60000L).values().toArray(new Long[1]));
        Assert.assertEquals(70L + 60L + 50L + 40L + 30L + 20L + 10L, bucketCounter.getCount());
    }

    @Test
    public void testQueryWith0() {
        ManualClock manual = new ManualClock();
        BucketCounter bucketCounter = new BucketCounterImpl(10, 10, manual);
        for (int k = 1; k <= 5; k++) {
            for (int i = 0; i < k*100; i++) {
                bucketCounter.update();
            }
            manual.addSeconds(10);
        }

        Long[] expected = {500L, 400L, 300L, 200L, 100L};
        Assert.assertArrayEquals(expected, bucketCounter.getBucketCounts(0L).values().toArray(new Long[5]));

        Long[] timestamps = {40000L, 30000L, 20000L, 10000L, 0L};
        Assert.assertArrayEquals(timestamps, bucketCounter.getBucketCounts(0L).keySet().toArray(new Long[5]));
    }

    @Test
    public void testAccurateQuery() {
        ManualClock manual = new ManualClock();
        BucketCounter bucketCounter = new BucketCounterImpl(10, 10, manual);
        manual.addSeconds(10);
        bucketCounter.inc(100);
        Assert.assertEquals("latest bucket value should be able to query",
                100, bucketCounter.getBucketCounts().get(10000L).longValue());
    }

    @Test
    public void testUpdateTotalCount() {
        ManualClock manual = new ManualClock();
        BucketCounter bucketCounter = new BucketCounterImpl(10, 10, manual, true);
        bucketCounter.update();
        bucketCounter.update();
        Assert.assertEquals(2, bucketCounter.getCount());
    }

    @Test
    public void testNotUpdateTotalCount() {
        ManualClock manual = new ManualClock();
        BucketCounter bucketCounter = new BucketCounterImpl(10, 10, manual, false);
        bucketCounter.update();
        bucketCounter.update();
        Assert.assertEquals(0, bucketCounter.getCount());
    }

    @Test
    public void testLastUpdateTime() {
        ManualClock clock = new ManualClock();
        BucketCounter bucketCounter = new BucketCounterImpl(10, 10, clock, false);
        clock.addSeconds(10);
        bucketCounter.update();
        clock.addSeconds(10);
        Assert.assertEquals(10000L, bucketCounter.lastUpdateTime());
    }
}
