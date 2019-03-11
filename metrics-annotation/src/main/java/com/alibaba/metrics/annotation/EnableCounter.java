package com.alibaba.metrics.annotation;

import com.alibaba.metrics.MetricLevel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Counter类型对应的方法注解
 *
 * 方法成功执行后调用对应Counter度量指标的由inc属性决定的inc或dec方法
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableCounter {
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
     * 格式：key1:value1,key2:value2,key3:value3
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
     * 每次目标方法被成功调用后执行的计数操作
     *
     * 默认值为1，表示+1操作，若传入-3表示每次减3，若传入3表示每次加3; 不能为0
     *
     * @return
     */
    int inc() default 1;
}
