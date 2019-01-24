package com.alibaba.metrics.reporter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

public class ScheduledAndDelayTest {

	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	private ScheduledFuture futureTask;

	private int i = 1;

	private Runnable task = new Runnable() {
		@Override
		public void run() {



			try {

				System.out.println("start time : " + System.currentTimeMillis());
				if (i < 3){
					i = i + 1;
					Thread.sleep(19000);
				}else{
					Thread.sleep(1000);
				}

				System.out.println("end time : " + System.currentTimeMillis());
				System.out.println("-------------------------");
			} catch (Exception e) {

			}

		}
	};


	@Ignore
	@Test
	public void scheduledAndDelayTest() throws InterruptedException{
		System.out.println("init time : " + System.currentTimeMillis());
		futureTask = executor.scheduleAtFixedRate(task, 0, 7, TimeUnit.SECONDS);
		new CountDownLatch(1).await();
	}
}
