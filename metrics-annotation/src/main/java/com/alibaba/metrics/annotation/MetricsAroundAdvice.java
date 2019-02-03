package com.alibaba.metrics.annotation;

import com.alibaba.metrics.Compass;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.Histogram;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.ReservoirType;
import com.alibaba.metrics.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 除Gauge类型度量指标注解外的其他注解对应的切面逻辑
 *
 */
public class MetricsAroundAdvice {

    private static final Logger logger = LoggerFactory.getLogger(MetricsAroundAdvice.class);

    public Object wrapWithTryCatch(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        EnableCounter counterAnnotation = method.getAnnotation(EnableCounter.class);
        EnableMeter meterAnnotation = method.getAnnotation(EnableMeter.class);
        EnableHistogram histogramAnnotation = method.getAnnotation(EnableHistogram.class);
        EnableTimer timerAnnotation = method.getAnnotation(EnableTimer.class);
        EnableFastCompass fastCompassAnnotation = method.getAnnotation(EnableFastCompass.class);
        EnableCompass compassAnnotation = method.getAnnotation(EnableCompass.class);

        Timer.Context timerContext = this.beforeTimerAnnotation(timerAnnotation);
        Compass.Context compassContext = this.beforeCompassAnnotation(compassAnnotation);

        Object result = null;
        Throwable throwable = null;
        long start = System.currentTimeMillis();
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            throwable = t;
        }
        long duration = System.currentTimeMillis() - start;

        this.afterCompassAnnotation(compassContext, throwable);
        this.afterTimerAnnotation(timerContext);
        this.afterCounterAnnotation(counterAnnotation);
        this.afterMeterAnnotation(meterAnnotation);
        this.afterHistogramAnnotation(histogramAnnotation, result);
        this.afterFastCompassAnnotation(fastCompassAnnotation, throwable, duration);

        if (throwable != null) {
            throw throwable;
        }
        return result;
    }

    private void afterCompassAnnotation(Compass.Context compassContext, Throwable throwable) {
        if (compassContext != null) {
            if (throwable == null) {
                compassContext.success();
            } else {
                compassContext.error("exception");
            }
            compassContext.stop();
        }
    }

    private void afterTimerAnnotation(Timer.Context timerContext) {
        if (timerContext != null) {
            timerContext.stop();
        }
    }

    private Compass.Context beforeCompassAnnotation(EnableCompass compassAnnotation) {
        if (compassAnnotation == null) {
            return null;
        }
        String group = compassAnnotation.group();
        String key = compassAnnotation.key();
        String tags = compassAnnotation.tags();
        MetricLevel level = compassAnnotation.level();
        ReservoirType reservoir = compassAnnotation.reservoir();
        logger.debug("EnableCompass group:{}, key:{}, tags:{}, level:{}, reservoir:{}", group, key, tags, level,
            reservoir);

        Map<String, String> tagMap = MetricsAnnotationUtil.parseTagMap(tags);
        Compass compass = MetricManager.getCompass(group, MetricName.build(key).tagged(tagMap).level(level), reservoir);

        return compass.time();
    }

    private void afterFastCompassAnnotation(EnableFastCompass fastCompassAnnotation, Throwable throwable,
                                            long duration) {
        if (fastCompassAnnotation == null) {
            return;
        }
        String group = fastCompassAnnotation.group();
        String key = fastCompassAnnotation.key();
        String tags = fastCompassAnnotation.tags();
        MetricLevel level = fastCompassAnnotation.level();
        logger.debug("EnableFastCompass group:{}, key:{}, tags:{}, level:{}", group, key, tags, level);

        Map<String, String> tagMap = MetricsAnnotationUtil.parseTagMap(tags);
        FastCompass fastCompass = MetricManager.getFastCompass(group, MetricName.build(key).tagged(tagMap)
            .level(level));

        if (throwable == null) {
            fastCompass.record(duration, "success");
        } else {
            fastCompass.record(duration, "exception");
        }
    }

    private Timer.Context beforeTimerAnnotation(EnableTimer timerAnnotation) {
        if (timerAnnotation == null) {
            return null;
        }
        String group = timerAnnotation.group();
        String key = timerAnnotation.key();
        String tags = timerAnnotation.tags();
        MetricLevel level = timerAnnotation.level();
        ReservoirType reservoir = timerAnnotation.reservoir();
        logger.debug("EnableTimer group:{}, key:{}, tags:{}, level:{}, reservoir:{}", group, key, tags, level,
            reservoir);

        Map<String, String> tagMap = MetricsAnnotationUtil.parseTagMap(tags);
        Timer timer = MetricManager.getTimer(group, MetricName.build(key).tagged(tagMap).level(level), reservoir);

        return timer.time();
    }

    private void afterHistogramAnnotation(EnableHistogram histogramAnnotation, Object result) {
        if (histogramAnnotation == null) {
            return;
        }
        String group = histogramAnnotation.group();
        String key = histogramAnnotation.key();
        String tags = histogramAnnotation.tags();
        MetricLevel level = histogramAnnotation.level();
        ReservoirType reservoir = histogramAnnotation.reservoir();
        logger.debug("EnableHistogram group:{}, key:{}, tags:{}, level:{}, reservoir:{}", group, key, tags, level,
            reservoir);

        Map<String, String> tagMap = MetricsAnnotationUtil.parseTagMap(tags);
        Histogram histogram = MetricManager.getHistogram(group, MetricName.build(key).tagged(tagMap).level(level));

        if (result == null) {
            histogram.update(0);
        }
        if (result instanceof Integer) {
            histogram.update((Integer)result);
        }
        if (result instanceof Long) {
            histogram.update((Long)result);
        }
    }

    private void afterMeterAnnotation(EnableMeter meterAnnotation) {
        if (meterAnnotation == null) {
            return;
        }
        String group = meterAnnotation.group();
        String key = meterAnnotation.key();
        String tags = meterAnnotation.tags();
        MetricLevel level = meterAnnotation.level();
        int num = meterAnnotation.num();
        logger.debug("EnableMeter group:{}, key:{}, tags:{}, level:{}, num:{}", group, key, tags, level, num);

        Map<String, String> tagMap = MetricsAnnotationUtil.parseTagMap(tags);
        Meter meter = MetricManager.getMeter(group, MetricName.build(key).tagged(tagMap).level(level));

        meter.mark(num);
    }

    private void afterCounterAnnotation(EnableCounter counterAnnotation) {
        if (counterAnnotation == null) {
            return;
        }
        String group = counterAnnotation.group();
        String key = counterAnnotation.key();
        String tags = counterAnnotation.tags();
        MetricLevel level = counterAnnotation.level();
        int inc = counterAnnotation.inc();
        logger.debug("EnableCounter group:{}, key:{}, tags:{}, level:{}, inc:{}", group, key, tags, level, inc);

        Map<String, String> tagMap = MetricsAnnotationUtil.parseTagMap(tags);
        Counter counter = MetricManager.getCounter(group, MetricName.build(key).tagged(tagMap)
            .level(level));

        if (inc != 0) {
            counter.inc(inc);
        }
    }

}
