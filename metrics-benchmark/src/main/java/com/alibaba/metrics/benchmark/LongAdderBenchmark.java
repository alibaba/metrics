package com.alibaba.metrics.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.atomic.AtomicLong;

@State(Scope.Benchmark)
public class LongAdderBenchmark {

    @Benchmark
    public Object testMetricsLongAdderPerformance() {
        final com.alibaba.metrics.LongAdder count = new com.alibaba.metrics.LongAdder();
        count.add(1);
        return count;
    }

    @Benchmark
    public Object testAtomicLongPerformance() {
        final AtomicLong count = new AtomicLong();
        count.addAndGet(1);
        return count;
    }

/// This requires JDK 1.8
//    @Benchmark
//    public Object testJDKLongAdderPerformance() {
//        final java.util.concurrent.atomic.LongAdder count = new java.util.concurrent.atomic.LongAdder();
//        count.add(1);
//        return count;
//    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + LongAdderBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(3)
                .measurementIterations(5)
                .threads(32)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
