package com.javalab.calculator;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link Calculator} のメモリ機能(M+/M-/MR/MC)を検証するテスト。
 * 四則演算とは別クラスに分けているのは、メモリは内部状態(フィールド)を持つため、
 * 「状態を持たない演算」と「状態を持つメモリ操作」でテストの関心事を分離するため。
 */
class CalculatorMemoryTest {

    private final Calculator calculator = new Calculator();

    @Test
    void initialMemoryIsZero() {
        // 何も操作していない初期状態でメモリが0であることを確認する(MCしなくても安全な初期値)。
        assertEquals(0, BigDecimal.ZERO.compareTo(calculator.memoryRecall()));
    }

    @Test
    void memoryAddAddsValueToMemory() {
        calculator.memoryAdd(new BigDecimal("5"));

        assertEquals(0, new BigDecimal("5").compareTo(calculator.memoryRecall()));
    }

    @Test
    void memorySubtractSubtractsValueFromMemory() {
        // M+で5を足した後にM-で2を引くと、メモリは5-2=3になることを確認する。
        // 複数回の操作を組み合わせても状態が正しく積み上がることを検証している。
        calculator.memoryAdd(new BigDecimal("5"));
        calculator.memorySubtract(new BigDecimal("2"));

        assertEquals(0, new BigDecimal("3").compareTo(calculator.memoryRecall()));
    }

    @Test
    void memoryClearResetsMemoryToZero() {
        // メモリに値が入っている状態からMCを実行すると、確実に0へ戻ることを確認する。
        calculator.memoryAdd(new BigDecimal("5"));
        calculator.memoryClear();

        assertEquals(0, BigDecimal.ZERO.compareTo(calculator.memoryRecall()));
    }
}
