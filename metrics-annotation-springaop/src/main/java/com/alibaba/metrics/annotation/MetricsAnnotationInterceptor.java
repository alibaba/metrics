package com.alibaba.metrics.annotation;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 除Gauge类型度量指标注解外的其他注解对应的拦截器
 *
 */
@Aspect
@EnableAspectJAutoProxy
@Configuration
public class MetricsAnnotationInterceptor {

    @Autowired
    private MetricsAroundAdvice aroundAdvice;

    @Around("@annotation(com.alibaba.metrics.annotation.EnableCounter)"
        + "|| @annotation(com.alibaba.metrics.annotation.EnableMeter)"
        + "|| @annotation(com.alibaba.metrics.annotation.EnableHistogram)"
        + "|| @annotation(com.alibaba.metrics.annotation.EnableTimer)"
        + "|| @annotation(com.alibaba.metrics.annotation.EnableFastCompass)"
        + "|| @annotation(com.alibaba.metrics.annotation.EnableCompass)")
    public Object wrapWithTryCatch(ProceedingJoinPoint joinPoint) throws Throwable {
        return this.aroundAdvice.wrapWithTryCatch(joinPoint);
    }


    @Bean
    public MetricsAroundAdvice cache() {
        return new MetricsAroundAdvice();
    }

    @Bean
    public GaugeRegistry4SpringContext gaugeRegistry4SpringContext() {
        return new GaugeRegistry4SpringContext();
    }
}
