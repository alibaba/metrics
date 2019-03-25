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

import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.annotation.MetricsAnnotationInterceptor;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MetricsAnnotationInterceptor.class, TestConfig.class })
public class EnableFastCompassTest {

    @Autowired
    private MetricsAnnotationTestService metricsTestService;

    @Test
    public void test() {
        this.metricsTestService.testFastCompass1();

        FastCompass fastCompass = MetricManager.getFastCompass("test",
            MetricName.build("ascp.upcp-scitem.metrics-annotation.fastCompass.test1")
                .tagged("purpose", "test"));

        Map<String, Map<Long, Long>> countAndRtPerCategory = fastCompass.getCountAndRtPerCategory();
        Map<Long, Long> success = countAndRtPerCategory.get("success");
        TestCase.assertNotNull(success);

        Map<Long, Long> exception = countAndRtPerCategory.get("exception");
        TestCase.assertNull(exception);

        try {
            this.metricsTestService.testFastCompass1();
            this.metricsTestService.testFastCompass2();
            TestCase.fail();
        } catch (Exception e) {
            //ignore
        }

        countAndRtPerCategory = fastCompass.getCountAndRtPerCategory();
        success = countAndRtPerCategory.get("success");
        TestCase.assertNotNull(success);

        exception = countAndRtPerCategory.get("exception");
        TestCase.assertNotNull(exception);
    }
}
