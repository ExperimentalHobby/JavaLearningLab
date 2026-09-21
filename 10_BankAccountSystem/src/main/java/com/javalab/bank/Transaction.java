package com.javalab.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 1回の入出金取引を表すイミュータブルな値オブジェクト。
 * @param type 取引種別
 * @param amount 取引金額
 * @param balanceAfter 取引後の残高スナップショット
 * @param timestamp 取引が行われた日時
 */
public record Transaction(Type type, BigDecimal amount, BigDecimal balanceAfter, LocalDateTime timestamp) {

    public enum Type {
        DEPOSIT,
        WITHDRAWAL
    }
}
