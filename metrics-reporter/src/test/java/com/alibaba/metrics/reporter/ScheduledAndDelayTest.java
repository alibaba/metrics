/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.metrics.reporter;

import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
