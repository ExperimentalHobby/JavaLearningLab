package com.javalab.jmhbenchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 文字列結合方式({@code +}演算子 vs {@link StringBuilder})の速度を比較するベンチマーク。
 * {@code partCount}個の短い文字列を結合するのにかかる時間・スループットを計測する。
 * {@code @Fork}/{@code @Warmup}/{@code @Measurement}をクラスに明示することで、
 * JMHの既定値による長時間実行を避けつつ、どの条件で測ったかをコードに残す(再現性の担保)。
 */
@State(Scope.Thread)
@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 2, warmups = 1)
@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
public class StringConcatBenchmark {

    /** 入力サイズを振ることで、+演算子(O(n^2))とStringBuilder(O(n))の計算量の違いを観測できるようにする。 */
    @Param({"10", "100", "1000"})
    private int partCount;

    private List<String> parts;

    @Setup
    public void setUp() {
        parts = new ArrayList<>();
        for (int i = 0; i < partCount; i++) {
            parts.add("item" + i);
        }
    }

    @Benchmark
    public void plusOperator(Blackhole bh) {
        bh.consume(StringConcatUtil.concatWithPlusOperator(parts));
    }

    @Benchmark
    public void stringBuilder(Blackhole bh) {
        bh.consume(StringConcatUtil.concatWithStringBuilder(parts));
    }
}
