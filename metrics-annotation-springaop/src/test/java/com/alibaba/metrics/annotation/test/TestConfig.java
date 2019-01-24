package com.alibaba.metrics.annotation.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public MetricsAnnotationTestService metricsAnnotationTestService() {
        return new MetricsAnnotationTestService();
    }
}
