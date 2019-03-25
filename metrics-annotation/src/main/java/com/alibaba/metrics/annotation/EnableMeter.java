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
package com.alibaba.metrics.annotation;

import com.alibaba.metrics.MetricLevel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meter类型对应的方法注解
 *
 * 方法成功执行后调用对应Meter度量指标的mark方法
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableMeter {
    /**
     * Metric的group
     *
     * @return
     */
    String group();

    /**
     * MetricName中的key，请参考Metrics的命名规范进行命名
     *
     * @return
     */
    String key();

    /**
     * MetricName中的tag，请参考Metrics的命名规范进行命名
     *
     * 注意：以该注解方式使用Metrics，只能使用静态tag，无法实现根据参数或者返回值进行区别的动态参数，请直接使用Metrics的API实现
     *
     * @return
     */
    String tags() default "";

    /**
     * Metric的等级,默认为{@link MetricLevel#NORMAL},请参考Metrics的命名规范
     *
     * @return
     */
    MetricLevel level() default MetricLevel.NORMAL;

    /**
     * 被标记的次数，默认为1, 不能为0
     *
     * @return
     */
    int num() default 1;
}
