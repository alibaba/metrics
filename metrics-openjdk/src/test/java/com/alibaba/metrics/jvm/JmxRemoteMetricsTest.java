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
package com.alibaba.metrics.jvm;

import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.MetricName;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

/**
 * 〈jmx remote metrics test.〉
 *
 * @create 2019/6/6
 */
public class JmxRemoteMetricsTest {
    private ObjectName mapped;
    private final MetricName MAPPED = MetricName.build("mapped");
    private final MetricName MAPPED_USED = MAPPED.resolve("used");

    /**
     * You can get the MBeanServerConnection in the following way:
     *
     *     Map<String, Object> environment = new HashMap<String, Object>();
     *     environment.put(JMXConnector.CREDENTIALS, new String[]{USERNAME, PASSWORD});
     *     JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");
     *     MBeanServerConnection mBeanServerConnection = JMXConnectorFactory.connect(jmxServiceURL, environment).getMBeanServerConnection();
     **/
    private final MBeanServerConnection mBeanServerConnection = mock(MBeanServerConnection.class);

    @Before
    public void setUp() throws Exception {
        this.mapped = new ObjectName("java.nio:type=BufferPool,name=mapped");
    }

    @Test
    public void testCollectionRemoteMetrics() throws Exception {
        BufferPoolMetricSet bufferPoolMetricSet = new BufferPoolMetricSet(mBeanServerConnection);
        final Gauge gauge = (Gauge) bufferPoolMetricSet.getMetrics().get(MAPPED_USED);
        when(mBeanServerConnection.getAttribute(mapped, "MemoryUsed")).thenReturn(100);
        assertThat(gauge.getValue()).isEqualTo(100);
    }
}