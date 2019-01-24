package com.alibaba.metrics.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EnableGauge注解对应的Gauge类型度量指标的注册入口类
 *
 */
public class GaugeRegistry {
    private static final String KEY_SEPARATOR = "*";
    private static final Logger logger = LoggerFactory.getLogger(GaugeRegistry.class);

    private static Set<String> registeredKeys = new HashSet<String>();

    public static void registerGauge(EnableGauge aliGaugeAnnotation, final Object bean, final Method method) {
        String group = aliGaugeAnnotation.group();
        String key = aliGaugeAnnotation.key();
        String tags = aliGaugeAnnotation.tags();
        MetricLevel level = aliGaugeAnnotation.level();
        logger.debug("EnableGauge group:{}, key:{}, tags:{}, level:{}", group, key, tags, level);
        String registeredKey = group + KEY_SEPARATOR + key + KEY_SEPARATOR + tags;

        if (registeredKeys.contains(registeredKey)) {
            throw new RuntimeException("不应该存在两个相同的EnableGauge注解，请仔细检查注解的属性值.group:" + group +
                ",key:" + key + ",tags:" + tags);
        }
        registeredKeys.add(registeredKey);

        Class<?> returnType = method.getReturnType();
        if (isInvalidType(returnType)) {
            throw new RuntimeException("标记有EnableGauge注解的方法返回值仅支持int、long、float、double及对应的包装类类型，但该方法的返回类型为:"
                + returnType.getSimpleName() + " class:" + bean.getClass().getSimpleName() + " method:" + method
                .getName());
        }

        Map<String, String> tagMap = MetricsAnnotationUtil.parseTagMap(tags);
        MetricManager.register(group, MetricName.build(key).tagged(tagMap).level(level), new Gauge<Object>() {

            private long lastUpdated = 0;

            @Override
            public Object getValue() {
                try {
                    Object result = method.invoke(bean);
                    if (result == null) {
                        logger.error("EnableGauge注解的方法调用失败，返回对象不应该为null. 类名:{},方法名:{}",
                                bean.getClass().getSimpleName(), method.getName());
                    }
                    return result;
                } catch (IllegalAccessException e) {
                    logger.error("EnableGauge注解的方法调用失败，必须为无参的public方法。类名:{},方法名:{}",
                            bean.getClass().getSimpleName(), method.getName());
                    return null;
                } catch (InvocationTargetException e) {
                    logger.error("EnableGaugee注解的方法调用失败，必须为无参的public方法。类名:{},方法名:{}",
                            bean.getClass().getSimpleName(), method.getName());
                    return null;
                } finally {
                    lastUpdated = System.currentTimeMillis();
                }
            }

            @Override
            public long lastUpdateTime() {
                return lastUpdated;
            }
        });

    }

    /**
     * 是否是非法的返回类型
     *
     * @param returnType
     * @return
     */
    private static boolean isInvalidType(Class<?> returnType) {
        return !returnType.equals(Integer.class) && !returnType.equals(int.class) && !returnType.equals(Long.class)
            && !returnType.equals(long.class) && !returnType.equals(Double.class) && !returnType.equals(double.class)
            && !returnType.equals(Float.class) && !returnType.equals(float.class);
    }
}
