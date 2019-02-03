package com.alibaba.metrics.reporter.file;

import com.alibaba.metrics.common.MetricObject;
import org.apache.commons.lang3.time.StopWatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class FormatBenchmark {

    static Random random = new Random();

    public static void main(String[] args) throws InterruptedException {

        SimpleTextMetricFormat simpleTextMetricFormat = new SimpleTextMetricFormat();
        JsonMetricFormat jsonMetricFormat = new JsonMetricFormat();


        // 代码预热
        for(int i = 0; i < 2; ++i) {
//            FormatBenchmark.test(simpleTextMetricFormat);
//            FormatBenchmark.test(jsonMetricFormat);

            FormatBenchmark.testBytes(jsonMetricFormat);
            FormatBenchmark.testBytes(simpleTextMetricFormat);
        }

        TimeUnit.SECONDS.sleep(5);
        // 正式测试

        System.err.println("=====  start ========");
//        for(int i = 0; i < 5; ++i) {
//            FormatBenchmark.test(simpleTextMetricFormat);
//            TimeUnit.SECONDS.sleep(5);
//            FormatBenchmark.test(jsonMetricFormat);
//            TimeUnit.SECONDS.sleep(5);
//        }


        for(int i = 0; i < 5; ++i) {
            FormatBenchmark.testBytes(jsonMetricFormat);
            TimeUnit.SECONDS.sleep(5);
            FormatBenchmark.testBytes(simpleTextMetricFormat);
            TimeUnit.SECONDS.sleep(5);
        }
    }


    private static void test(MetricFormat format) {
        System.out.println("====== test toString=========");
        System.out.println("format:" + format.getClass().getSimpleName());
        RollingFileAppender appender = RollingFileAppender.builder()
                .name("metrics/metrics.log")
                .fileSize(1024 * 1024 * 300)
                .build();


        Map<String, String> tags = new HashMap<String, String>();

        tags.put("host", "127.0.0.1");
        tags.put("appName", "hello" + random.nextInt());
        tags.put("oooooooooooo", "pppppppppp" + random.nextInt());
        tags.put("level" + random.nextInt(), "aaaaaaaa");

//        MetricObject metricObject = MetricObject.named("abc" + random.nextInt()).withTimestamp(System.currentTimeMillis())
//                .withValue("1321lkj12kl4jsdklfjdsf" + random.nextInt()).withTags(tags ).build();


        int counter = 1000000;
        long lengthCounter = 0;

        StopWatch watch = new StopWatch();

        watch.start();
        for(int i = 0; i < counter; ++i) {
            MetricObject metricObject = MetricObject.named("abc" + random.nextInt()).withTimestamp(random.nextLong())
                    .withValue("1321lkj12kl4jsdklfjdsf" + random.nextInt()).withTags(tags ).build();

            String string = format.format(metricObject);
            lengthCounter += string.length();

//            appender.append(string);
        }
        watch.stop();
        System.out.println(watch);
        System.out.println("length:" + lengthCounter/counter);

        System.out.println("qps:" + counter/(watch.getTime()/1000.0));
    }


    private static void testBytes(MetricFormat format) {
        System.out.println("====test toBytes ===========");
        System.out.println("format:" + format.getClass().getSimpleName());
        RollingFileAppender appender = RollingFileAppender.builder()
                .name("metrics/metrics.log")
                .fileSize(1024 * 1024 * 300)
                .build();


        Map<String, String> tags = new HashMap<String, String>();

        tags.put("host", "127.0.0.1");
        tags.put("appName", "hello" + random.nextInt());
        tags.put("oooooooooooo", "pppppppppp" + random.nextInt());
        tags.put("level" + random.nextInt(), "aaaaaaaa");

//        MetricObject metricObject = MetricObject.named("abc" + random.nextInt()).withTimestamp(System.currentTimeMillis())
//                .withValue("1321lkj12kl4jsdklfjdsf" + random.nextInt()).withTags(tags ).build();


        int counter = 1000000;
        long lengthCounter = 0;

        StopWatch watch = new StopWatch();

        watch.start();
        for(int i = 0; i < counter; ++i) {
            MetricObject metricObject = MetricObject.named("abc" + random.nextInt()).withTimestamp(random.nextLong())
                    .withValue("1321lkj12kl4jsdklfjdsf" + random.nextInt()).withTags(tags ).build();

            byte[] data = format.formatToBytes(metricObject);
            lengthCounter += data.length;

//            appender.append(data);
        }
        watch.stop();
        System.out.println(watch);
        System.out.println("length:" + lengthCounter/counter);

        System.out.println("qps:" + counter/(watch.getTime()/1000.0));
    }

}
