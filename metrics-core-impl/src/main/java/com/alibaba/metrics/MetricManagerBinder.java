package com.alibaba.metrics;

public class MetricManagerBinder {

    private static final MetricManagerBinder instance = new MetricManagerBinder();

    private IMetricManager manager;

    private MetricManagerBinder() {
        manager = new AliMetricManager();
    }

    public static MetricManagerBinder getSingleton() {
        return instance;
    }

    public IMetricManager getMetricManager() {
        return manager;
    }
}
