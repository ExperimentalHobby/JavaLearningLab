package com.javalab.bank;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Account} の入出金・残高不足検証・取引履歴を検証するテスト。
 * カプセル化(残高・履歴を直接いじれず、必ずdeposit/withdraw経由でしか変更できない)を
 * 崩さずに正しく動作していることを、履歴の不変ビュー確認も含めて検証する。
 */
class AccountTest {

    private final Account account = new Account("Alice");

    @Test
    void depositIncreasesBalance() {
        account.deposit(new BigDecimal("1000"));

        assertEquals(0, new BigDecimal("1000").compareTo(account.getBalance()));
    }

    @Test
    void depositThrowsExceptionForZeroOrNegativeAmount() {
        assertThrows(InvalidAmountException.class, () -> account.deposit(BigDecimal.ZERO));
    }

    @Test
    void withdrawDecreasesBalance() {
        account.deposit(new BigDecimal("1000"));

        account.withdraw(new BigDecimal("300"));

        assertEquals(0, new BigDecimal("700").compareTo(account.getBalance()));
    }

    @Test
    void withdrawThrowsExceptionWhenAmountExceedsBalance() {
        // 残高1000に対して2000の出金を試みると、残高不足としてInsufficientBalanceExceptionになる。
        account.deposit(new BigDecimal("1000"));

        assertThrows(InsufficientBalanceException.class,
                () -> account.withdraw(new BigDecimal("2000")));
    }

    @Test
    void withdrawThrowsExceptionForZeroOrNegativeAmount() {
        assertThrows(InvalidAmountException.class, () -> account.withdraw(BigDecimal.ZERO));
    }

    @Test
    void historyRecordsDepositAndWithdrawalWithBalanceSnapshot() {
        // 各取引の種別(DEPOSIT/WITHDRAWAL)・金額に加えて、「その取引が行われた時点での残高」
        // (balanceAfter)も正しく記録されることを確認する。
        account.deposit(new BigDecimal("1000"));
        account.withdraw(new BigDecimal("300"));

        List<Transaction> history = account.getHistory();

        assertEquals(2, history.size());
        assertEquals(Transaction.Type.DEPOSIT, history.get(0).type());
        assertEquals(0, new BigDecimal("1000").compareTo(history.get(0).amount()));
        assertEquals(0, new BigDecimal("1000").compareTo(history.get(0).balanceAfter()));
        assertEquals(Transaction.Type.WITHDRAWAL, history.get(1).type());
        assertEquals(0, new BigDecimal("300").compareTo(history.get(1).amount()));
        assertEquals(0, new BigDecimal("700").compareTo(history.get(1).balanceAfter()));
    }

    @Test
    void historyReturnsUnmodifiableView() {
        // getHistory()が返すListはList.copyOf()によるコピーであり、外部から add() しても
        // Account内部の履歴には影響しない(カプセル化が破られていない)ことを確認する。
        account.deposit(new BigDecimal("1000"));
        List<Transaction> history = account.getHistory();

        assertThrows(UnsupportedOperationException.class,
                () -> history.add(new Transaction(Transaction.Type.DEPOSIT, BigDecimal.ONE, BigDecimal.ONE)));
        assertTrue(account.getHistory().size() == 1);
    }
}
