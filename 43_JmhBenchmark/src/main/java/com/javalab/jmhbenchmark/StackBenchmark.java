package com.javalab.jmhbenchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

/**
 * スタック実装(自前実装の単方向連結リスト vs {@link ArrayDeque} vs 同期化された{@link Stack})の
 * push→pop速度を比較するベンチマーク。1000要素をpushしてから全てpopするのにかかる時間を計測する。
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class StackBenchmark {

    private static final int ELEMENT_COUNT = 1000;

    @Benchmark
    public int simpleLinkedStack() {
        SimpleLinkedStack<Integer> stack = new SimpleLinkedStack<>();
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            stack.push(i);
        }
        int sum = 0;
        while (!stack.isEmpty()) {
            sum += stack.pop();
        }
        return sum;
    }

    @Benchmark
    public int arrayDeque() {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            stack.push(i);
        }
        int sum = 0;
        while (!stack.isEmpty()) {
            sum += stack.pop();
        }
        return sum;
    }

    /** {@code java.util.Stack}は内部で同期化されており、単一スレッドの用途ではオーバーヘッドになる比較対象。 */
    @Benchmark
    public int legacyStack() {
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            stack.push(i);
        }
        int sum = 0;
        while (!stack.isEmpty()) {
            sum += stack.pop();
        }
        return sum;
    }
}
