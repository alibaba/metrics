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

import javax.management.AttributeNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JmxAttributeGaugeTest {
    private final MBeanServer mBeanServer = mock(MBeanServer.class);
    private final ObjectName objectName = mock(ObjectName.class);
    private final JmxAttributeGauge gauge = new JmxAttributeGauge(mBeanServer, objectName, "attr");
    private final Object value = mock(Object.class);

    @Test
    public void returnsAJmxAttribute() throws Exception {
        when(mBeanServer.getAttribute(objectName, "attr")).thenReturn(value);

        assertThat(gauge.getValue())
                .isEqualTo(value);
    }

    @Test
    public void returnsNullIfThereIsAnException() throws Exception {
        when(mBeanServer.getAttribute(objectName, "attr")).thenThrow(new AttributeNotFoundException());

        assertThat(gauge.getValue())
                .isNull();
    }
}
