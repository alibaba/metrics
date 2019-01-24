package com.alibaba.metrics.jvm;

import org.junit.Test;

import java.lang.management.CompilationMXBean;
import java.util.concurrent.TimeUnit;

import static com.alibaba.metrics.Constants.NOT_AVAILABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompilationGaugeTest {

    private final CompilationMXBean cm = mock(CompilationMXBean.class);
    private final CompilationGauge gauge = new CompilationGauge(1L, TimeUnit.SECONDS, cm);

    @Test
    public void compilationGauge() throws Exception {
        when(cm.isCompilationTimeMonitoringSupported()).thenReturn(true);
        when(cm.getTotalCompilationTime()).thenReturn(100L);
        assertThat(gauge.getValue()).isEqualTo(NOT_AVAILABLE);
        when(cm.getTotalCompilationTime()).thenReturn(120L);
        Thread.sleep(1200);
        assertThat(gauge.getValue()).isEqualTo(20L);
    }
}
