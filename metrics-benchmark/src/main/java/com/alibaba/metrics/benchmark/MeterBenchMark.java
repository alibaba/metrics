package com.alibaba.metrics.benchmark;

import com.alibaba.metrics.MeterImpl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class MeterBenchMark {

    private com.alibaba.metrics.Meter aliMeter = new MeterImpl();
    private com.codahale.metrics.Meter dropwizardMeter = new com.codahale.metrics.Meter();

    @Benchmark
    public Object testDropwizardMeter() {
        dropwizardMeter.mark();
        return dropwizardMeter;
    }


    @Benchmark
    public Object testAliMetricsMeter() {
        aliMeter.mark();
        return aliMeter;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + MeterBenchMark.class.getSimpleName() + ".*")
                .warmupIterations(3)
                .measurementIterations(5)
                .threads(32)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
