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
package com.alibaba.metrics.annotation.test;

import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.Timer;
import com.alibaba.metrics.annotation.MetricsAnnotationInterceptor;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MetricsAnnotationInterceptor.class, TestConfig.class})
public class EnableTimerTest {

    @Autowired
    private MetricsAnnotationTestService metricsTestService;

    @Test
    public void test() {
        this.metricsTestService.testTimer1();

        Timer timer = MetricManager.getTimer("test",
            MetricName.build("ascp.upcp-scitem.metrics-annotation.timer.test1")
                .tagged("purpose", "test"));
        TestCase.assertEquals(1, timer.getCount());

        this.metricsTestService.testTimer1();
        this.metricsTestService.testTimer1();

        TestCase.assertEquals(3, timer.getCount());
    }
}
