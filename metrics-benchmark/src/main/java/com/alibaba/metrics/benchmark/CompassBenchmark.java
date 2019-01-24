package com.alibaba.metrics.benchmark;

import com.alibaba.metrics.Compass;
import com.alibaba.metrics.CompassImpl;
import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.FastCompassImpl;
import com.alibaba.metrics.ReservoirType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class CompassBenchmark {

    private Compass compass = new CompassImpl(1, ReservoirType.BUCKET);
    private FastCompass fastCompass = new FastCompassImpl(1);

    @Benchmark
    public Object testCompassPerformance() {
        compass.update(1, TimeUnit.MILLISECONDS);
        return compass;
    }

    @Benchmark
    public Object testFastCompassPerformance() {
        fastCompass.record(1, "success");
        return fastCompass;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + CompassBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(3)
                .measurementIterations(5)
                .threads(32)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
