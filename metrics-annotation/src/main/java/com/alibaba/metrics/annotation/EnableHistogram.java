package com.alibaba.metrics.annotation;

import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.ReservoirType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Histogram类型对应的方法注解
 *
 * 方法的返回值类型必须为int、long、Integer、Long，若为null，当做0处理
 *
 * 方法被成功调用后,使用方法的返回值作为参数调用对应Histogram度量指标的update方法
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableHistogram {
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
     * Metrics的ReservoirType，默认为指数衰减随机采样
     *
     * @return
     */
    ReservoirType reservoir() default ReservoirType.EXPONENTIALLY_DECAYING;
}
