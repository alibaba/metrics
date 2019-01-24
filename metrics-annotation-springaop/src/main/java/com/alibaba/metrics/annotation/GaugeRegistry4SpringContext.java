package com.alibaba.metrics.annotation;

import java.lang.reflect.Method;
import java.util.Map;

import com.alibaba.metrics.MetricManager;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 扫描spring容器中的所有bean，识别标记有{@linkplain com.alibaba.metrics.annotation.EnableGauge}注解的方法，并将其注册至
 * {@linkplain MetricManager}
 *
 * 注意：被注解的方法必须为public的方法
 *
 */
public class GaugeRegistry4SpringContext implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        //仅在root application context启动后执行
        if (applicationContext.getParent() != null) {
            return;
        }
        Map<String, Object> beansOfType = applicationContext.getBeansOfType(Object.class);
        for (Object bean : beansOfType.values()) {
            if (bean == null) {
                return;
            }
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            Method[] methods = targetClass.getMethods();
            if (methods == null || methods.length == 0) {
                return;
            }
            for (Method method : methods) {
                EnableGauge aliGaugeAnnotation = method.getAnnotation(EnableGauge.class);
                if (aliGaugeAnnotation == null) {
                    continue;
                }
                GaugeRegistry.registerGauge(aliGaugeAnnotation, bean, method);
            }
        }
    }

}
