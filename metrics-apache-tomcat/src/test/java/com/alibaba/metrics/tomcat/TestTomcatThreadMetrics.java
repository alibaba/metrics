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
package com.alibaba.metrics.tomcat;

import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TestTomcatThreadMetrics {

    @Test
    public void testTomcatThreadMetrics() {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(22222);
        tomcat.addContext("", "/tmp");
        tomcat.addServlet("", "testServlet", TestServlet.class.getName());
        MetricManager.register("tomcat", MetricName.build("middleware.tomcat.thread"), new ThreadGaugeSet());
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }

        MetricRegistry registry = MetricManager.getIMetricManager().getMetricRegistryByGroup("tomcat");
        Assert.assertNotNull(registry.getGauges().get(MetricName.build("middleware.tomcat.thread.busy_count")));
        Assert.assertNotNull(registry.getGauges().get(MetricName.build("middleware.tomcat.thread.total_count")));
        Assert.assertNotNull(registry.getGauges().get(MetricName.build("middleware.tomcat.thread.min_pool_size")));
        Assert.assertNotNull(registry.getGauges().get(MetricName.build("middleware.tomcat.thread.max_pool_size")));
        Assert.assertNotNull(registry.getGauges().get(MetricName.build("middleware.tomcat.thread.thread_pool_queue_size")));
    }

    private class TestServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doGet(req, resp);
        }
    }
}
