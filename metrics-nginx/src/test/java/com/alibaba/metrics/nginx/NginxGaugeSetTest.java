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
package com.alibaba.metrics.nginx;

import com.alibaba.metrics.Constants;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.ManualClock;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NginxGaugeSetTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testNginxGaugeTest() {
        String data1 = "Active connections: 5\n" +
                "server accepts handled requests request_time\n" +
                " 2009437 2009437 2003352 28635579\n" +
                "Reading: 0 Writing: 1 Waiting: 4\n";

        ManualClock clock = new ManualClock();

        TestNginxGaugeSet nginxGaugeSet = new TestNginxGaugeSet(clock);

        Map<MetricName, Metric> metrics = nginxGaugeSet.getMetrics();

        Assert.assertEquals(8, metrics.keySet().size());

        clock.addMillis(20);
        nginxGaugeSet.setData(data1);

        Gauge<Double> connAccepted = (Gauge<Double>)metrics.get(MetricName.build("conn.accepted"));
        Gauge<Double> connHandled = (Gauge<Double>)metrics.get(MetricName.build("conn.handled"));
        Gauge<Double> reqQps = (Gauge<Double>)metrics.get(MetricName.build("request.qps"));
        Gauge<Double> reqAvgRt = (Gauge<Double>)metrics.get(MetricName.build("request.avg_rt"));
        Gauge<Long> connActive = (Gauge<Long>)metrics.get(MetricName.build("conn.active"));
        Gauge<Long> connReading = (Gauge<Long>)metrics.get(MetricName.build("conn.reading"));
        Gauge<Long> connWriting = (Gauge<Long>)metrics.get(MetricName.build("conn.writing"));
        Gauge<Long> connWaiting = (Gauge<Long>)metrics.get(MetricName.build("conn.waiting"));

        Assert.assertEquals(0.0d, connAccepted.getValue(), 0.0001d);
        Assert.assertEquals(0.0d, connHandled.getValue(), 0.0001d);
        Assert.assertEquals(0.0d, reqQps.getValue(), 0.0001d);
        Assert.assertEquals(0.0d, reqAvgRt.getValue(), 0.0001d);
        Assert.assertEquals(5L, connActive.getValue().longValue());
        Assert.assertEquals(0L, connReading.getValue().longValue());
        Assert.assertEquals(1L, connWriting.getValue().longValue());
        Assert.assertEquals(4L, connWaiting.getValue().longValue());

        clock.addMillis(20);

        String data2 = "Active connections: 10\n" +
                "server accepts handled requests request_time\n" +
                " 2009637 2009617 2003502 28638579\n" +
                "Reading: 5 Writing: 3 Waiting: 2\n";
        nginxGaugeSet.setData(data2);

        Assert.assertEquals(10000.0d, connAccepted.getValue(), 0.0001d);
        Assert.assertEquals(1.0d * (2009617 - 2009437)/0.02, connHandled.getValue(), 0.0001d);
        Assert.assertEquals(1.0d * (2003502 - 2003352)/0.02, reqQps.getValue(), 0.0001d);
        Assert.assertEquals(1.0d * (28638579 - 28635579) / (2003502 - 2003352), reqAvgRt.getValue(), 0.0001d);
        Assert.assertEquals(10L, connActive.getValue().longValue());
        Assert.assertEquals(5L, connReading.getValue().longValue());
        Assert.assertEquals(3L, connWriting.getValue().longValue());
        Assert.assertEquals(2L, connWaiting.getValue().longValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNginxWithSSL() {
        String data1 = "Active connections: 1\n" +
                "server accepts handled requests request_time\n" +
                " 65357 65357 56197 37549\n" +
                "ssl new reused(SID+ticket)\n" +
                " 0 0\n" +
                "Reading: 0 Writing: 1 Waiting: 0\n" +
                "SSL_Requests: 0 SSL_Handshake: 0 SSL_Handshake_Time: 0";

        ManualClock clock = new ManualClock();

        TestNginxGaugeSet nginxGaugeSet = new TestNginxGaugeSet(clock);

        Map<MetricName, Metric> metrics = nginxGaugeSet.getMetrics();

        Assert.assertEquals(8, metrics.keySet().size());

        clock.addMillis(20);
        nginxGaugeSet.setData(data1);

        Gauge<Double> connAccepted = (Gauge<Double>)metrics.get(MetricName.build("conn.accepted"));
        Gauge<Double> connHandled = (Gauge<Double>)metrics.get(MetricName.build("conn.handled"));
        Gauge<Double> reqQps = (Gauge<Double>)metrics.get(MetricName.build("request.qps"));
        Gauge<Double> reqAvgRt = (Gauge<Double>)metrics.get(MetricName.build("request.avg_rt"));
        Gauge<Long> connActive = (Gauge<Long>)metrics.get(MetricName.build("conn.active"));
        Gauge<Long> connReading = (Gauge<Long>)metrics.get(MetricName.build("conn.reading"));
        Gauge<Long> connWriting = (Gauge<Long>)metrics.get(MetricName.build("conn.writing"));
        Gauge<Long> connWaiting = (Gauge<Long>)metrics.get(MetricName.build("conn.waiting"));

        Assert.assertEquals(0.0d, connAccepted.getValue(), 0.0001d);
        Assert.assertEquals(0.0d, connHandled.getValue(), 0.0001d);
        Assert.assertEquals(0.0d, reqQps.getValue(), 0.0001d);
        Assert.assertEquals(0.0d, reqAvgRt.getValue(), 0.0001d);
        Assert.assertEquals(1L, connActive.getValue().longValue());
        Assert.assertEquals(0L, connReading.getValue().longValue());
        Assert.assertEquals(1L, connWriting.getValue().longValue());
        Assert.assertEquals(0L, connWaiting.getValue().longValue());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNginxWithChunckedData() {
        String data0 = "Active connections: 1\n" +
                "server accepts handled requests request_time\n" +
                " 65357 65357 56197 37549\n" +
                "ssl new reused(SID+ticket)\n" +
                " 0 0\n" +
                "Reading: 0 Writing: 1 Waiting: 0\n" +
                "SSL_Requests: 0 SSL_Handshake: 0 SSL_Handshake_Time: 0";


        String data = "HTTP/1.1 200 OK\n" +
                "Server: Tengine\n" +
                "Date: Wed, 30 Aug 2017 09:04:20 GMT\n" +
                "Content-Type: text/plain\n" +
                "Transfer-Encoding: chunked\n" +
                "Connection: close\n" +
                "Vary: Accept-Encoding\n" +
                "\n" +
                "2\n" +
                "\n" +
                "\n" +
                "ef\n" +
                "Active connections: 645\n" +
                "server accepts handled requests request_time\n" +
                " 169071 169071 177412 6789886\n" +
                "ssl new reused(SID+ticket)\n" +
                " 0 0\n" +
                "Reading: 0 Writing: 1 Waiting: 644\n" +
                "SSL_Requests: 0 SSL_Handshake: 0 SSL_Handshake_Time: 0\n" +
                "SSL_failed: 0\n" +
                "\n" +
                "0";

        ManualClock clock = new ManualClock();

        TestNginxGaugeSet nginxGaugeSet = new TestNginxGaugeSet(clock);

        Map<MetricName, Metric> metrics = nginxGaugeSet.getMetrics();

        Assert.assertEquals(8, metrics.keySet().size());

        clock.addMillis(1000);
        nginxGaugeSet.setData(data0);

        Gauge<Double> connAccepted = (Gauge<Double>)metrics.get(MetricName.build("conn.accepted"));
        Gauge<Double> connHandled = (Gauge<Double>)metrics.get(MetricName.build("conn.handled"));
        Gauge<Double> reqQps = (Gauge<Double>)metrics.get(MetricName.build("request.qps"));
        Gauge<Double> reqAvgRt = (Gauge<Double>)metrics.get(MetricName.build("request.avg_rt"));
        Gauge<Long> connActive = (Gauge<Long>)metrics.get(MetricName.build("conn.active"));
        Gauge<Long> connReading = (Gauge<Long>)metrics.get(MetricName.build("conn.reading"));
        Gauge<Long> connWriting = (Gauge<Long>)metrics.get(MetricName.build("conn.writing"));
        Gauge<Long> connWaiting = (Gauge<Long>)metrics.get(MetricName.build("conn.waiting"));

        Assert.assertEquals(0.0d, connAccepted.getValue(), 0.0001d);

        clock.addMillis(1000);
        nginxGaugeSet.setData(data);

        Assert.assertEquals(169071.0d - 65357.0d, connAccepted.getValue(), 0.0001d);
        Assert.assertEquals( 169071.0d - 65357.0d, connHandled.getValue(), 0.0001d);
        Assert.assertEquals(177412.0d - 56197.0d, reqQps.getValue(), 0.0001d);
        Assert.assertEquals((6789886.0d - 37549.0d) / (177412.0d - 56197.0d), reqAvgRt.getValue(), 0.0001d);
        Assert.assertEquals(645L, connActive.getValue().longValue());
        Assert.assertEquals(0L, connReading.getValue().longValue());
        Assert.assertEquals(1L, connWriting.getValue().longValue());
        Assert.assertEquals(644L, connWaiting.getValue().longValue());

    }


    @SuppressWarnings("unchecked")
    @Test
    public void testNginxWithNoData() {
        String data0 = "Active connections: 1\n" +
                "server accepts handled requests request_time\n" +
                " 65355 65355 56195 37545\n" +
                "ssl new reused(SID+ticket)\n" +
                " 0 0\n" +
                "Reading: 0 Writing: 1 Waiting: 0\n" +
                "SSL_Requests: 0 SSL_Handshake: 0 SSL_Handshake_Time: 0";

        String data1 = "Active connections: 1\n" +
                "server accepts handled requests request_time\n" +
                " 65357 65357 56197 37549\n" +
                "ssl new reused(SID+ticket)\n" +
                " 0 0\n" +
                "Reading: 0 Writing: 1 Waiting: 0\n" +
                "SSL_Requests: 0 SSL_Handshake: 0 SSL_Handshake_Time: 0";


        String data2 = "";

        ManualClock clock = new ManualClock();

        TestNginxGaugeSet nginxGaugeSet = new TestNginxGaugeSet(clock);

        Map<MetricName, Metric> metrics = nginxGaugeSet.getMetrics();

        Assert.assertEquals(8, metrics.keySet().size());

        clock.addMillis(1000);
        nginxGaugeSet.setData(data0);

        Gauge<Double> connAccepted = (Gauge<Double>)metrics.get(MetricName.build("conn.accepted"));
        Gauge<Double> connHandled = (Gauge<Double>)metrics.get(MetricName.build("conn.handled"));
        Gauge<Double> reqQps = (Gauge<Double>)metrics.get(MetricName.build("request.qps"));
        Gauge<Double> reqAvgRt = (Gauge<Double>)metrics.get(MetricName.build("request.avg_rt"));
        Gauge<Long> connActive = (Gauge<Long>)metrics.get(MetricName.build("conn.active"));
        Gauge<Long> connReading = (Gauge<Long>)metrics.get(MetricName.build("conn.reading"));
        Gauge<Long> connWriting = (Gauge<Long>)metrics.get(MetricName.build("conn.writing"));
        Gauge<Long> connWaiting = (Gauge<Long>)metrics.get(MetricName.build("conn.waiting"));

        Assert.assertEquals(0.0d, connAccepted.getValue(), 0.0001d);

        clock.addMillis(1000);
        nginxGaugeSet.setData(data1);

        Assert.assertEquals(2.0d, connAccepted.getValue(), 0.0001d);
        Assert.assertEquals(0L, connWaiting.getValue().longValue());

        clock.addMillis(1000);
        nginxGaugeSet.setData(data2);

        Assert.assertEquals(0d, connAccepted.getValue(), 0.0001d);
        Assert.assertEquals(0d, connHandled.getValue(), 0.0001d);
        Assert.assertEquals(0d, reqQps.getValue(), 0.0001d);
        Assert.assertEquals(0d, reqAvgRt.getValue(), 0.0001d);
        Assert.assertEquals(0L, connActive.getValue().longValue());
        Assert.assertEquals(0L, connReading.getValue().longValue());
        Assert.assertEquals(0L, connWriting.getValue().longValue());
        Assert.assertEquals(0L, connWaiting.getValue().longValue());

    }


    @SuppressWarnings("unchecked")
    @Test
    public void testNginxRestart() {
        String data0 = "Active connections: 1\n" +
                "server accepts handled requests request_time\n" +
                " 65355 65355 56195 37545\n" +
                "ssl new reused(SID+ticket)\n" +
                " 0 0\n" +
                "Reading: 0 Writing: 1 Waiting: 0\n" +
                "SSL_Requests: 0 SSL_Handshake: 0 SSL_Handshake_Time: 0";

        String data1 = "Active connections: 1\n" +
                "server accepts handled requests request_time\n" +
                " 2 3 4 5\n" +
                "ssl new reused(SID+ticket)\n" +
                " 0 0\n" +
                "Reading: 1 Writing: 2 Waiting: 3\n" +
                "SSL_Requests: 0 SSL_Handshake: 0 SSL_Handshake_Time: 0";


        ManualClock clock = new ManualClock();

        TestNginxGaugeSet nginxGaugeSet = new TestNginxGaugeSet(clock);

        Map<MetricName, Metric> metrics = nginxGaugeSet.getMetrics();

        Assert.assertEquals(8, metrics.keySet().size());

        clock.addMillis(1000);
        nginxGaugeSet.setData(data0);

        Gauge<Double> connAccepted = (Gauge<Double>)metrics.get(MetricName.build("conn.accepted"));
        Gauge<Double> connHandled = (Gauge<Double>)metrics.get(MetricName.build("conn.handled"));
        Gauge<Double> reqQps = (Gauge<Double>)metrics.get(MetricName.build("request.qps"));
        Gauge<Double> reqAvgRt = (Gauge<Double>)metrics.get(MetricName.build("request.avg_rt"));
        Gauge<Long> connActive = (Gauge<Long>)metrics.get(MetricName.build("conn.active"));
        Gauge<Long> connReading = (Gauge<Long>)metrics.get(MetricName.build("conn.reading"));
        Gauge<Long> connWriting = (Gauge<Long>)metrics.get(MetricName.build("conn.writing"));
        Gauge<Long> connWaiting = (Gauge<Long>)metrics.get(MetricName.build("conn.waiting"));

        Assert.assertEquals(0.0d, connAccepted.getValue(), 0.0001d);

        clock.addMillis(1000);
        nginxGaugeSet.setData(data1);

        Assert.assertEquals(Constants.NOT_AVAILABLE, connAccepted.getValue(), 0.0001d);
        Assert.assertEquals(Constants.NOT_AVAILABLE, connHandled.getValue(), 0.0001d);
        Assert.assertEquals(Constants.NOT_AVAILABLE, reqQps.getValue(), 0.0001d);
        Assert.assertEquals(Constants.NOT_AVAILABLE, reqAvgRt.getValue(), 0.0001d);
        Assert.assertEquals(1L, connActive.getValue().longValue());
        Assert.assertEquals(1L, connReading.getValue().longValue());
        Assert.assertEquals(2L, connWriting.getValue().longValue());
        Assert.assertEquals(3L, connWaiting.getValue().longValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAverageRtIsZero() {
        String data0 = "Active connections: 1\n" +
                "server accepts handled requests request_time\n" +
                " 65355 65355 56195 37545\n" +
                "ssl new reused(SID+ticket)\n" +
                " 0 0\n" +
                "Reading: 0 Writing: 1 Waiting: 0\n" +
                "SSL_Requests: 0 SSL_Handshake: 0 SSL_Handshake_Time: 0";

        String data1 = "Active connections: 1\n" +
                "server accepts handled requests request_time\n" +
                " 65355 65355 56195 37545\n" +
                "ssl new reused(SID+ticket)\n" +
                " 0 0\n" +
                "Reading: 1 Writing: 2 Waiting: 3\n" +
                "SSL_Requests: 0 SSL_Handshake: 0 SSL_Handshake_Time: 0";


        ManualClock clock = new ManualClock();

        TestNginxGaugeSet nginxGaugeSet = new TestNginxGaugeSet(clock);

        Map<MetricName, Metric> metrics = nginxGaugeSet.getMetrics();

        Assert.assertEquals(8, metrics.keySet().size());

        clock.addMillis(1000);
        nginxGaugeSet.setData(data0);

        Gauge<Double> connAccepted = (Gauge<Double>)metrics.get(MetricName.build("conn.accepted"));
        Gauge<Double> reqQps = (Gauge<Double>)metrics.get(MetricName.build("request.qps"));
        Gauge<Double> reqAvgRt = (Gauge<Double>)metrics.get(MetricName.build("request.avg_rt"));

        Assert.assertEquals(0.0d, connAccepted.getValue(), 0.0001d);

        clock.addMillis(1000);
        nginxGaugeSet.setData(data1);

        Assert.assertEquals(0.0d, reqQps.getValue(), 0.0001d);
        Assert.assertEquals(0.0d, reqAvgRt.getValue(), 0.0001d);
    }


    /**
     * Only for testing purpose
     */
    private class TestNginxGaugeSet extends NginxGaugeSet {

        private String data = "";

        public TestNginxGaugeSet(ManualClock clock) {
            super("localhost", 80, "/test", "test", 10, TimeUnit.MILLISECONDS, clock);
        }

        @Override
        protected String[] fetchNginxMetrics() {
            return data.split("\n");
        }

        public void setData(String data) {
            this.data = data;
        }
    }


}
