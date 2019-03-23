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
package com.alibaba.metrics;

import org.junit.Assert;
import org.junit.Test;

public class RatioGaugeTest {

    @Test
    public void testRatioGauge() {

        MetricRegistry registry = new MetricRegistryImpl();
        final Counter total = registry.counter(MetricName.build("test.total"));
        final Counter success = registry.counter(MetricName.build("test.success"));
        total.inc(5);
        success.inc(3);
        RatioGauge successRateGuage = new RatioGauge() {
            @Override
            protected Ratio getRatio() {
                return Ratio.of(success.getCount(), total.getCount());
            }
        };

        registry.register(MetricName.build("test.success_rate"), successRateGuage);

        Assert.assertEquals(0.6d, successRateGuage.getValue().doubleValue(), 0.00001d);
    }

    public void testRatioGauge2() {
        final Counter total = MetricManager.getCounter("test", MetricName.build("test.total"));
        final Counter success = MetricManager.getCounter("test", MetricName.build("test.success"));
        total.inc(5);
        success.inc(3);
        RatioGauge successRateGuage = new RatioGauge() {
            @Override
            protected Ratio getRatio() {
                return Ratio.of(success.getCount(), total.getCount());
            }
        };

        MetricManager.register("test", MetricName.build("test.success_rate"), successRateGuage);

        Assert.assertEquals(0.6d, successRateGuage.getValue().doubleValue(), 0.00001d);
    }
}
