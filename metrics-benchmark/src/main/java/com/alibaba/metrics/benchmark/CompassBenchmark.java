/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
