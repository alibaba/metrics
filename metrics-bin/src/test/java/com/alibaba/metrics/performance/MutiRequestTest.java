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
package com.alibaba.metrics.performance;

import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class MutiRequestTest {

    private static AtomicLong count = new AtomicLong();
    private static CountDownLatch syncc = new CountDownLatch(20);

    @Ignore
    @Test
    public void mainProcess(){

        long totalStartTime = System.currentTimeMillis();

        for(int i = 0; i <= 0; i++){
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    request();
                }

            });

            thread.start();
        }

        try {
            syncc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long totalEndTime = System.currentTimeMillis();

        System.out.println("total time : " + (totalEndTime - totalStartTime));
        System.out.println("request num : " + count.get());
        System.out.println("qps is : " + (1.0d * count.get()) / (totalEndTime - totalStartTime) * 1000);

        try {
            Thread.sleep(1000000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void request(){

        for(int i = 0; i <= 0; i++){
            try {
                HttpRequester request = new HttpRequester();
                request.setDefaultContentEncoding("utf-8");
                //HttpRespons hr = request.sendGet(\"http://localhost:8006/metrics\");

                long timestamp = System.currentTimeMillis();

                int interval = 60000;
                long startTime = (timestamp / interval - 1) * interval - 180000;
                long endTime = timestamp / interval * interval - 1000;

//                long startTime = System.currentTimeMillis() - 40000;
//                long endTime = System.currentTimeMillis() - 20000;

                String postBody = "{\"zeroIgnore\":\"false\", \"startTime\":" + startTime + ", \"endTime\":" + endTime + ",\"limit\":0,\"precision\":60,\"queries\":[{\"key\":\"middleware.metrics.rest.url.m1\"},{\"key\":\"middleware.metrics.rest.url.m5\"},{\"key\":\"PRODUCT_PROVIDER_SERVICE.max\"}]}";
                HttpRespons hr = request.sendPost("http://localhost:8006/metrics/search", postBody);
                //HttpRespons hr = request.sendPost("http://11.239.164.32:8006/metrics/search", postBody);
                //HttpRespons hr = request.sendPost("http://localhost:8006/metrics/search", postBody);
                System.out.println(hr.getContent());
                count.incrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        syncc.countDown();

    }

}
