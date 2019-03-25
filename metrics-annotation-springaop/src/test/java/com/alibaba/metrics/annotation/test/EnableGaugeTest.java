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

import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.annotation.MetricsAnnotationInterceptor;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map.Entry;
import java.util.SortedMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MetricsAnnotationInterceptor.class, TestConfig.class })
public class EnableGaugeTest {

    @Test
    public void test() {
        SortedMap<MetricName, Gauge> test = MetricManager.getIMetricManager().getGauges("test", MetricFilter.ALL);
        TestCase.assertTrue(test != null && test.size() == 9);

        for (Entry<MetricName, Gauge> entry : test.entrySet()) {
            String name = entry.getKey().getKey();
            Object value = entry.getValue().getValue();
            if(name.contains("test1")){
                TestCase.assertTrue(value.equals(3L));
            }
            if(name.contains("test2")){
                TestCase.assertTrue(value.equals(3L));
            }
            if(name.contains("test3")){
                TestCase.assertTrue(value.equals(3));
            }
            if(name.contains("test4")){
                TestCase.assertTrue(value.equals(3));
            }
            if(name.contains("test5")){
                TestCase.assertTrue(value.equals(3.9));
            }
            if(name.contains("test6")){
                TestCase.assertTrue(value.equals(3.9));
            }
            if(name.contains("test7")){
                TestCase.assertTrue(value.equals(3.9f));
            }
            if(name.contains("test8")){
                TestCase.assertTrue(value.equals(3.9f));
            }
            if(name.contains("test9")){
                TestCase.assertTrue(value == null);
            }
        }
    }
}
