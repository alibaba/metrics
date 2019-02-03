package com.alibaba.metrics.tomcat;

import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.ManualClock;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestHttpGaugeSet {

    private MBeanServer mBeanServer = mock(MBeanServer.class);

    @SuppressWarnings("unchecked")
    @Test
    public void testTomcatHttpMetrics() {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(22222);
        tomcat.addContext("", "/var/tmp");
        tomcat.addServlet("", "testServlet", TestServlet.class.getName());
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }

        ManualClock clock = new ManualClock();
        HttpGaugeSet gaugeSet = new HttpGaugeSet(mBeanServer, 10, TimeUnit.MILLISECONDS, clock);

        MetricManager.register("tomcat", MetricName.build("middleware.tomcat.http"), gaugeSet);
        MetricRegistry registry = MetricManager.getIMetricManager().getMetricRegistryByGroup("tomcat");

        try {
            when(mBeanServer.getAttribute(Mockito.any(ObjectName.class), Mockito.anyString()))
                    .thenAnswer(new Answer<Object>() {
                        @Override
                        public Object answer(InvocationOnMock invocation) throws Throwable {
                            if ("requestCount".equals(invocation.getArguments()[1])) {
                                return 10;
                            }
                            if ("processingTime".equals(invocation.getArguments()[1])) {
                                return 500L;
                            }
                            if ("errorCount".equals(invocation.getArguments()[1])) {
                                return 3;
                            }
                            if ("maxTime".equals(invocation.getArguments()[1])) {
                                return 100L;
                            }
                            if ("bytesReceived".equals(invocation.getArguments()[1])) {
                                return 1000L;
                            }
                            if ("bytesSent".equals(invocation.getArguments()[1])) {
                                return 2000L;
                            }
                            if ("requestFailureCount".equals(invocation.getArguments()[1])) {
                                return new Integer[]{1,2,3,4,5,6,7};
                            }
                            return "-1";
                        }
                    });
        } catch (Exception e) {
            Assert.fail("Should not throw exception here.");
        }

        clock.addMillis(20);

        SortedMap<MetricName, Gauge> gauge = registry.getGauges();
        Gauge<Long> reqCnt = gauge.get(MetricName.build("middleware.tomcat.http.request_count")
                .tagged("connector", "http-bio-22222"));
        Gauge<Long> processTime = gauge.get(MetricName.build("middleware.tomcat.http.request.processing_time")
                .tagged("connector", "http-bio-22222"));
        Gauge<Long> errCnt = gauge.get(MetricName.build("middleware.tomcat.http.request.error_count")
                .tagged("connector", "http-bio-22222"));
        Gauge<Long> maxTime = gauge.get(MetricName.build("middleware.tomcat.http.request.max_time")
                .tagged("connector", "http-bio-22222"));
        Gauge<Long> bytesSent = gauge.get(MetricName.build("middleware.tomcat.http.request.bytes_sent")
                .tagged("connector", "http-bio-22222"));
        Gauge<Long> bytesRecv = gauge.get(MetricName.build("middleware.tomcat.http.request.bytes_received")
                .tagged("connector", "http-bio-22222"));


        Assert.assertEquals(10L, reqCnt.getValue().longValue());
        Assert.assertEquals(500L, processTime.getValue().longValue());
        Assert.assertEquals(3L, errCnt.getValue().longValue());
        Assert.assertEquals(100L, maxTime.getValue().longValue());
        Assert.assertEquals(2000L, bytesSent.getValue().longValue());
        Assert.assertEquals(1000L, bytesRecv.getValue().longValue());
    }

    private class TestServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doGet(req, resp);
        }
    }

}
