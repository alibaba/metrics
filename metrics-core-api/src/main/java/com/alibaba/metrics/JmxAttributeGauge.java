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

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * A {@link Gauge} implementation which queries an {@link MBeanServerConnection} for an attribute of an object.
 */
public class JmxAttributeGauge implements Gauge<Object> {
    private final MBeanServerConnection mBeanServerConn;
    private final ObjectName objectName;
    private final String attributeName;
    private long lastUpdate = System.currentTimeMillis();

    /**
     * Creates a new JmxAttributeGauge.
     *
     * @param objectName    the name of the object
     * @param attributeName the name of the object's attribute
     */
    public JmxAttributeGauge(ObjectName objectName, String attributeName) {
        this(ManagementFactory.getPlatformMBeanServer(), objectName, attributeName);
    }

    /**
     * Creates a new JmxAttributeGauge.
     *
     * @param mBeanServerConn  the {@link MBeanServerConnection}
     * @param objectName       the name of the object
     * @param attributeName    the name of the object's attribute
     */
    public JmxAttributeGauge(MBeanServerConnection mBeanServerConn, ObjectName objectName, String attributeName) {
        this.mBeanServerConn = mBeanServerConn;
        this.objectName = objectName;
        this.attributeName = attributeName;
    }

    @Override
    public Object getValue() {
        try {
            Object result = mBeanServerConn.getAttribute(objectName, attributeName);
            lastUpdate = System.currentTimeMillis();
            return result;
        } catch (IOException e) {
            return null;
        } catch (JMException e) {
            return null;
        }
    }

    @Override
    public long lastUpdateTime() {
        return lastUpdate;
    }
}
