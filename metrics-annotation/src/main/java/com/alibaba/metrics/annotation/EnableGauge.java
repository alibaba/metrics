package com.alibaba.metrics.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.alibaba.metrics.MetricLevel;

/**
 * Gauge类型对应的方法注解
 *
 * 以标记方法的返回值作为瞬态值
 *
 * 方法必须是public且无参数
 *
 * 方法返回值支持int、long、float、double及对应的包装类类型
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableGauge {
    /**
     * Metric的group
     *
     * @return
     */
    String group();

    /**
     * MetricName中的key，请参考Dubbo-Metrics的命名规范进行命名
     *
     * @return
     */
    String key();

    /**
     * MetricName中的tag，请参考Dubbo-Metrics的命名规范进行命名
     *
     * 注意：以该注解方式使用Metrics，只能使用静态tag，无法实现根据参数或者返回值进行区别的动态参数，请直接使用Dubbo-Metrics的API实现
     *
     * @return
     */
    String tags() default "";

    /**
     * Metric的等级,默认为{@link MetricLevel#NORMAL},请参考Dubbo-Metrics的命名规范
     *
     * @return
     */
    MetricLevel level() default MetricLevel.NORMAL;
}
