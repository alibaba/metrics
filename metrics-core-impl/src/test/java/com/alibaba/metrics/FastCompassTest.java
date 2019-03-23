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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FastCompassTest {

    @Test
    public void testFastCompass() {
        ManualClock clock = new ManualClock();
        FastCompass fastCompass = new FastCompassImpl(60, 10, clock, 10);
        fastCompass.record(10, "success");
        fastCompass.record(20, "error");
        fastCompass.record(15, "success");
        clock.addSeconds(60);
        // verify count
        assertThat(fastCompass.getMethodCountPerCategory()).containsKey("success");
        assertThat(fastCompass.getMethodCountPerCategory(0L).get("success").get(0L)).isEqualTo(2L);
        assertThat(fastCompass.getMethodCountPerCategory()).containsKey("error");
        assertThat(fastCompass.getMethodCountPerCategory(0L).get("error").get(0L)).isEqualTo(1L);
        // verify rt
        assertThat(fastCompass.getMethodRtPerCategory()).containsKey("success");
        assertThat(fastCompass.getMethodRtPerCategory(0L).get("success").get(0L)).isEqualTo(25L);
        assertThat(fastCompass.getMethodRtPerCategory()).containsKey("error");
        assertThat(fastCompass.getMethodRtPerCategory(0L).get("error").get(0L)).isEqualTo(20L);
        // total count
        long totalCount = fastCompass.getMethodCountPerCategory(0L).get("success").get(0L) +
                fastCompass.getMethodCountPerCategory(0L).get("error").get(0L);
        assertThat(totalCount).isEqualTo(3L);
        // average rt
        long avgRt = (fastCompass.getMethodRtPerCategory(0L).get("success").get(0L) +
                fastCompass.getMethodRtPerCategory(0L).get("error").get(0L)) / totalCount;
        assertThat(avgRt).isEqualTo(15L);
        // verify count and rt
        assertThat(fastCompass.getCountAndRtPerCategory()).containsKey("success");
        assertThat(fastCompass.getCountAndRtPerCategory(0L).get("success").get(0L)).isEqualTo((2L << 38) + 25);
        assertThat(fastCompass.getCountAndRtPerCategory()).containsKey("error");
        assertThat(fastCompass.getCountAndRtPerCategory(0L).get("error").get(0L)).isEqualTo((1L << 38) + 20);
    }

    @Test
    public void testBinaryAdd() {
        long a1 = (1L << 38) + 10;
        long a2 = (1L << 38) + 20;
        assertThat((a1 + a2) >> 38).isEqualTo(2);
    }

    @Test
    public void testMaxSubCategoryCount() {
        ManualClock clock = new ManualClock();
        FastCompass fastCompass = new FastCompassImpl(60, 10, clock, 2);
        fastCompass.record(10, "success");
        fastCompass.record(20, "error1");
        fastCompass.record(15, "error2");
        assertThat(fastCompass.getMethodRtPerCategory().keySet().size()).isEqualTo(2);
    }
}
