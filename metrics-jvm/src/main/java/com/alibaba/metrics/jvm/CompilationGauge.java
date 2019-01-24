package com.alibaba.metrics.jvm;

import com.alibaba.metrics.CachedGauge;

import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

import static com.alibaba.metrics.Constants.NOT_AVAILABLE;

/**
 * A cached gauge for jvm compilation statistics
 */
public class CompilationGauge extends CachedGauge<Long> {

    /**
     * The compilation mbean
     */
    private final CompilationMXBean mxBean;

    /**
     * Last total compilation time
     */
    private long lastValue = 0;

    public CompilationGauge(long timeout, TimeUnit timeoutUnit) {
        super(timeout, timeoutUnit);
        mxBean = ManagementFactory.getCompilationMXBean();
    }

    public CompilationGauge(long timeout, TimeUnit timeoutUnit, CompilationMXBean mxBean) {
        super(timeout, timeoutUnit);
        this.mxBean = mxBean;
    }

    @Override
    protected Long loadValue() {
        if (mxBean != null && mxBean.isCompilationTimeMonitoringSupported()) {
            long currentValue = mxBean.getTotalCompilationTime();
            if (lastValue > 0) {
                long delta = currentValue - lastValue;
                if (delta > 0) {
                    return delta;
                }
            }
            lastValue = currentValue;
        }
        return NOT_AVAILABLE;
    }
}
