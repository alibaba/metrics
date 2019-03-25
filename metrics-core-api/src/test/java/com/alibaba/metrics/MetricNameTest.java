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

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MetricNameTest {

    @Test
    public void testEmpty() throws Exception {
        assertThat(MetricName.EMPTY.getTags()).isEqualTo(MetricName.EMPTY_TAGS);
        assertThat(MetricName.EMPTY.getKey()).isEqualTo(null);
        assertThat(new MetricName().getTags()).isEqualTo(MetricName.EMPTY_TAGS);

        assertThat(MetricName.EMPTY).isEqualTo(new MetricName());
        assertThat(MetricName.build()).isEqualTo(MetricName.EMPTY);
        assertThat(MetricName.EMPTY.resolve(null)).isEqualTo(MetricName.EMPTY);
    }

    @Test
    public void testEmptyResolve() throws Exception {
        final MetricName name = new MetricName();
        assertThat(name.resolve("foo")).isEqualTo(new MetricName("foo"));
    }

    @Test
    public void testResolveToEmpty() throws Exception {
        final MetricName name = new MetricName("foo");
        assertThat(name.resolve(null)).isEqualTo(new MetricName("foo"));
    }

    @Test
    public void testResolve() throws Exception {
        final MetricName name = new MetricName("foo");
        assertThat(name.resolve("bar")).isEqualTo(new MetricName("foo.bar"));
    }

    @Test
    public void testResolveWithTags() {
        final MetricName name = new MetricName("foo").tagged("key", "value");
        assertThat(name.resolve("bar")).isEqualTo(new MetricName("foo.bar").tagged("key", "value"));
    }

    @Test
    public void testResolveWithoutTags() {
        final MetricName name = new MetricName("foo").tagged("key", "value");
        assertThat(name.resolve("bar", false)).isEqualTo(new MetricName("foo.bar"));
    }

    @Test
    public void testResolveBothEmpty() throws Exception {
        final MetricName name = new MetricName(null);
        assertThat(name.resolve(null)).isEqualTo(new MetricName());
    }

    @Test
    public void testAddTagsVarious() {
        final Map<String, String> refTags = new HashMap<String, String>();
        refTags.put("foo", "bar");
        final MetricName test = MetricName.EMPTY.tagged("foo", "bar");
        final MetricName test2 = MetricName.EMPTY.tagged(refTags);

        assertThat(test).isEqualTo(new MetricName(null, refTags));
        assertThat(test.getTags()).isEqualTo(refTags);

        assertThat(test2).isEqualTo(new MetricName(null, refTags));
        assertThat(test2.getTags()).isEqualTo(refTags);
    }

    @Test
    public void testTaggedMoreArguments() {
        final Map<String, String> refTags = new HashMap<String, String>();
        refTags.put("foo", "bar");
        refTags.put("baz", "biz");
        assertThat(MetricName.EMPTY.tagged("foo", "bar", "baz", "biz").getTags()).isEqualTo(refTags);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTaggedNotPairs() {
        MetricName.EMPTY.tagged("foo");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTaggedNotPairs2() {
        MetricName.EMPTY.tagged("foo", "bar", "baz");
    }

    @Test
    public void testCompareTo() {
        final MetricName a = MetricName.EMPTY.tagged("foo", "bar");
        final MetricName b = MetricName.EMPTY.tagged("foo", "baz");

        assertThat(a.compareTo(b)).isLessThan(0);
        assertThat(b.compareTo(a)).isGreaterThan(0);
        assertThat(b.compareTo(b)).isEqualTo(0);
        assertThat(b.resolve("key").compareTo(b)).isLessThan(0);
        assertThat(b.compareTo(b.resolve("key"))).isGreaterThan(0);
    }

    @Test
    public void testTaggedWithLevel() {
        MetricName name = MetricName.build("test").level(MetricLevel.CRITICAL);
        MetricName tagged = name.tagged("foo", "bar");
        assertThat(tagged.getMetricLevel()).isEqualTo(MetricLevel.CRITICAL);
    }

    @Test
    public void testJoinWithLevel() {
        MetricName name = MetricName.build("test").level(MetricLevel.CRITICAL);
        MetricName tagged = MetricName.join(name, MetricName.build("abc"));
        assertThat(tagged.getMetricLevel()).isEqualTo(MetricLevel.CRITICAL);
    }

    @Test
    public void testResolveWithLevel() {
        final MetricName name = new MetricName("foo").level(MetricLevel.CRITICAL).tagged("key", "value");
        assertThat(name.resolve("bar")).isEqualTo(new MetricName("foo.bar").tagged("key", "value").level(MetricLevel.CRITICAL));
    }
}
