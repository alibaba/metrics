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
package com.alibaba.metrics.common.filter;

import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import org.junit.Assert;
import org.junit.Test;

public class CompositeMetricFilterTest {

    @Test
    public void testNoFilters() {

        CompositeMetricFilter filter = new CompositeMetricFilter(MetricFilter.ALL);

        Assert.assertArrayEquals(null, filter.filters);

        MetricName name = MetricName.build("test");

        Assert.assertTrue(filter.matches(name, MetricManager.getCounter("ttt", name)));

    }


    @Test
    public void testOneFilter() {

        MetricFilter filter2 = new MetricFilter() {
            @Override
            public boolean matches(MetricName name, Metric metric) {
                return name.getKey().equals("test");
            }
        };

        CompositeMetricFilter filter = new CompositeMetricFilter(MetricFilter.ALL, filter2);

        Assert.assertArrayEquals(new MetricFilter[]{filter2}, filter.filters);

        MetricName name = MetricName.build("test");

        Assert.assertTrue(filter.matches(name, MetricManager.getCounter("ttt", name)));

        MetricName name2 = MetricName.build("test2");

        Assert.assertFalse(filter.matches(name2, MetricManager.getCounter("ttt", name2)));

    }
}
