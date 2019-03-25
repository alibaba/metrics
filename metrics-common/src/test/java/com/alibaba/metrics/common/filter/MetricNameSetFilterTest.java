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

import com.alibaba.metrics.Compass;
import com.alibaba.metrics.Histogram;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class MetricNameSetFilterTest {

    @Test
    public void testAccurateMatch() {

        Set<String> names = new HashSet<String>();
        names.add("middleware.product.provider.qps");
        names.add("shared.carts.my_cart.m1");
        names.add("shared.carts.my_cart1.stddev");
        names.add("shared.carts.my_cart2.error.count");

        MetricNameSetFilter filter = new MetricNameSetFilter(names);

        MetricName mn = MetricName.build("shared.carts.my_cart");
        MetricName mn1 = MetricName.build("shared.carts.my_cart1");
        MetricName mn2 = MetricName.build("shared.carts.my_cart2");

        Meter meter = MetricManager.getMeter("carts", mn);
        Histogram histogram = MetricManager.getHistogram("carts", mn1);
        Compass compass = MetricManager.getCompass("carts", mn2);

        Assert.assertTrue(filter.matches(mn, meter));
        Assert.assertTrue(filter.matches(mn1, histogram));
        Assert.assertTrue(filter.matches(mn2, compass));

    }

    @Test
    public void testMetricNameKeyMatch() {

        Set<String> names = new HashSet<String>();
        names.add("middleware.product.provider.qps");
        names.add("shared.carts.my_cart");
        names.add("shared.carts.my_cart1");
        names.add("shared.carts.my_cart2");

        MetricNameSetFilter filter = new MetricNameSetFilter(names);

        MetricName mn = MetricName.build("shared.carts.my_cart");
        MetricName mn1 = MetricName.build("shared.carts.my_cart1");
        MetricName mn2 = MetricName.build("shared.carts.my_cart2");

        Meter meter = MetricManager.getMeter("carts", mn);
        Histogram histogram = MetricManager.getHistogram("carts", mn1);
        Compass compass = MetricManager.getCompass("carts", mn2);

        Assert.assertTrue(filter.matches(mn, meter));
        Assert.assertTrue(filter.matches(mn1, histogram));
        Assert.assertTrue(filter.matches(mn2, compass));

    }

    @Test
    public void testMetricNamePrefixMatch() {
        Set<String> names = new HashSet<String>();
        names.add("shared.carts.my_cart");

        MetricNameSetFilter filter = new MetricNameSetFilter(names);

        MetricName mn = MetricName.build("shared.carts.my_cart.m1");

        Assert.assertTrue(filter.matches(mn, null));

        Set<String> names2 = new HashSet<String>();
        names2.add("shared.carts.my_cart.m15");

        MetricNameSetFilter filter2 = new MetricNameSetFilter(names2);

        MetricName mn2 = MetricName.build("shared.carts.my_cart.m1");

        Assert.assertFalse(filter2.matches(mn2, null));
    }

    @Test
    public void testMatchClusterHistogram() {
        Set<String> names = new HashSet<String>();
        names.add("shared.carts.rt");
        MetricNameSetFilter filter = new MetricNameSetFilter(names);
        MetricName mn = MetricName.build("shared.carts.rt.cluster_percentile");
        Assert.assertTrue(filter.matches(mn, null));
    }
}
