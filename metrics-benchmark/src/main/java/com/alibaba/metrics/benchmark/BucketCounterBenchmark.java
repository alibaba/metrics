package com.alibaba.metrics.benchmark;

import com.alibaba.metrics.BucketCounter;
import com.alibaba.metrics.BucketCounterImpl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class BucketCounterBenchmark {

    private BucketCounter bucketCounter = new BucketCounterImpl(1, false);

    @Benchmark
    public BucketCounter testBucketCounterPerformance() {
        bucketCounter.update();
        return bucketCounter;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + BucketCounterBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(3)
                .measurementIterations(5)
                .threads(32)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
