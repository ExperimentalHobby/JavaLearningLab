package com.javalab.bank;

import java.math.BigDecimal;

/**
 * 1回の入出金取引を表すイミュータブルな値オブジェクト。
 * @param type 取引種別
 * @param amount 取引金額
 * @param balanceAfter 取引後の残高スナップショット
 */
public record Transaction(Type type, BigDecimal amount, BigDecimal balanceAfter) {

    public enum Type {
        DEPOSIT,
        WITHDRAWAL
    }
}
