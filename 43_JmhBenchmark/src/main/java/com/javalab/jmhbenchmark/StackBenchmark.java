package com.javalab.jmhbenchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

/**
 * スタック実装(自前実装の単方向連結リスト vs {@link ArrayDeque} vs 同期化された{@link Stack})の
 * push→pop速度を比較するベンチマーク。{@code elementCount}要素をpushしてから全てpopするのにかかる
 * 時間・スループットを計測する。
 * {@code @Fork}/{@code @Warmup}/{@code @Measurement}をクラスに明示することで、
 * JMHの既定値(5フォーク×5回ウォームアップ×5回計測、各1秒)による長時間実行を避けつつ、
 * どの条件で測ったかをコードに残す(再現性の担保)。
 */
@State(Scope.Thread)
@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 2, warmups = 1)
@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
public class StackBenchmark {

    /** 入力サイズを振ることで、実装間の計算量の違い(定数倍の差か、オーダーの差か)を観測できるようにする。 */
    @Param({"10", "100", "1000"})
    private int elementCount;

    @Benchmark
    public void simpleLinkedStack(Blackhole bh) {
        SimpleLinkedStack<Integer> stack = new SimpleLinkedStack<>();
        for (int i = 0; i < elementCount; i++) {
            stack.push(i);
        }
        int sum = 0;
        while (!stack.isEmpty()) {
            sum += stack.pop();
        }
        bh.consume(sum);
    }

    @Benchmark
    public void arrayDeque(Blackhole bh) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < elementCount; i++) {
            stack.push(i);
        }
        int sum = 0;
        while (!stack.isEmpty()) {
            sum += stack.pop();
        }
        bh.consume(sum);
    }

    /** {@code java.util.Stack}は内部で同期化されており、単一スレッドの用途ではオーバーヘッドになる比較対象。 */
    @Benchmark
    public void legacyStack(Blackhole bh) {
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < elementCount; i++) {
            stack.push(i);
        }
        int sum = 0;
        while (!stack.isEmpty()) {
            sum += stack.pop();
        }
        bh.consume(sum);
    }
}
