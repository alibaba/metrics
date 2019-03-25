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
package com.alibaba.metrics.tomcat;

import javax.management.InstanceAlreadyExistsException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Set;

public class JMXUtils {

    /**
     * 注册一个MBean
     */
    public static ObjectName register(String name, Object mbean) {
        try {
            ObjectName objectName = new ObjectName(name);
            MBeanServer mbeanServer = getMBeanServer();
            try {
                mbeanServer.registerMBean(mbean, objectName);
            } catch (InstanceAlreadyExistsException ex) {
                mbeanServer.unregisterMBean(objectName);
                mbeanServer.registerMBean(mbean, objectName);
            }
            return objectName;
        } catch (JMException e) {
            throw new IllegalArgumentException(name, e);
        }
    }

    /**
     * 取消一个MBean
     */
    public static void unregister(String name) {
        try {
            MBeanServer mbeanServer = getMBeanServer();
            mbeanServer.unregisterMBean(new ObjectName(name));
        } catch (JMException e) {
            throw new IllegalArgumentException(name, e);
        }
    }

    /**
     * 生成一个ObjectName，如果出错，返回null
     */
    public static ObjectName createObjectName(String pattern) {
        try {
            return new ObjectName(pattern);
        } catch (Exception ex) {
            // Ignore.
        }

        return null;
    }

    /**
     * 获取类型Pattern列表
     */
    public static ObjectName[] getObjectNames(ObjectName pattern) {
        ObjectName[] result = new ObjectName[0];
        Set<ObjectName> objectNames = getMBeanServer().queryNames(pattern, null);
        if (objectNames != null && !objectNames.isEmpty()) {
            result = objectNames.toArray(new ObjectName[objectNames.size()]);
        }
        return result;
    }

    public static MBeanServer getMBeanServer() {
        MBeanServer mBeanServer = null;
        if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
            mBeanServer = MBeanServerFactory.findMBeanServer(null).get(0);
        } else {
            mBeanServer = ManagementFactory.getPlatformMBeanServer();
        }
        return mBeanServer;
    }
}
