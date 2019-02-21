package com.alibaba.metrics.jvm;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import com.alibaba.metrics.CachedGauge;

/**
* @author boshu
* @version create time：2019年1月18日 上午11:38:35
* 
*/
public class FileDescriptorGauge extends CachedGauge<Long>{

    private final OperatingSystemMXBean os;
    
    public FileDescriptorGauge(long timeout, TimeUnit timeoutUnit) {
        super(timeout, timeoutUnit);
        os = ManagementFactory.getOperatingSystemMXBean();
    }

    public FileDescriptorGauge(long timeout, TimeUnit timeoutUnit, OperatingSystemMXBean os) {
        super(timeout, timeoutUnit);
        this.os = os;
    }

    @Override
    protected Long loadValue() {
        try {
            return invoke("getOpenFileDescriptorCount");
        } catch (Throwable e) {
            return 0L;
        } 
    }

    private long invoke(String name) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Method method = os.getClass().getDeclaredMethod(name);
        method.setAccessible(true);
        return (Long) method.invoke(os);
    }
}
