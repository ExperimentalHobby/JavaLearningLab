package com.javalab.bank;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        account.deposit(new BigDecimal("1000"));
        List<Transaction> history = account.getHistory();

        assertThrows(UnsupportedOperationException.class,
                () -> history.add(new Transaction(Transaction.Type.DEPOSIT, BigDecimal.ONE, BigDecimal.ONE)));
        assertTrue(account.getHistory().size() == 1);
    }
}
