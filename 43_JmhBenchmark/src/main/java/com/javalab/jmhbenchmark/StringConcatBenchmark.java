package com.javalab.jmhbenchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 文字列結合方式({@code +}演算子 vs {@link StringBuilder})の速度を比較するベンチマーク。
 * 1000個の短い文字列を結合するのにかかる時間を計測する。
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class StringConcatBenchmark {

    private List<String> parts;

    @Setup
    public void setUp() {
        parts = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            parts.add("item" + i);
        }
    }

    @Benchmark
    public String plusOperator() {
        return StringConcatUtil.concatWithPlusOperator(parts);
    }

    @Benchmark
    public String stringBuilder() {
        return StringConcatUtil.concatWithStringBuilder(parts);
    }
}
