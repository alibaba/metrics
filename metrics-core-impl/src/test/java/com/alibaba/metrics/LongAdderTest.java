package com.alibaba.metrics;

import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class LongAdderTest {

    @Ignore
    @Test
    public void testLongAdder() throws InterruptedException {
        final LongAdder count = new LongAdder();
        for (int i = 0; i < 1000; i++) {
            Thread a = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        count.add(1);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            a.start();
        }

        Thread b = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    count.reset();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        b.start();

        new CountDownLatch(1).await();
    }
}
