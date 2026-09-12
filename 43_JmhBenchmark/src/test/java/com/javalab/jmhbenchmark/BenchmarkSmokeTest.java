package com.javalab.jmhbenchmark;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * JMHベンチマークの配線(アノテーションプロセッサによるメタ情報生成・{@code Runner}からの発見)が
 * 壊れていないことを確認するスモークテスト。{@code fork(0)}(同一JVM内実行)・最小iterationで
 * 高速に実行することを優先しており、計測結果の精度は保証しない。実際の精度の高い計測は
 * `mvn package`でビルドした実行可能jar(`target/benchmarks.jar`)を手動実行して行う。
 */
class BenchmarkSmokeTest {

    @Test
    void stringConcatBenchmark_runsWithoutError() throws Exception {
        Options options = new OptionsBuilder()
                .include(StringConcatBenchmark.class.getSimpleName())
                .forks(0)
                .warmupIterations(1)
                .warmupTime(TimeValue.milliseconds(100))
                .measurementIterations(1)
                .measurementTime(TimeValue.milliseconds(100))
                .build();

        Collection<RunResult> results = new Runner(options).run();

        assertFalse(results.isEmpty());
    }

    @Test
    void stackBenchmark_runsWithoutError() throws Exception {
        Options options = new OptionsBuilder()
                .include(StackBenchmark.class.getSimpleName())
                .forks(0)
                .warmupIterations(1)
                .warmupTime(TimeValue.milliseconds(100))
                .measurementIterations(1)
                .measurementTime(TimeValue.milliseconds(100))
                .build();

        Collection<RunResult> results = new Runner(options).run();

        assertFalse(results.isEmpty());
    }
}
