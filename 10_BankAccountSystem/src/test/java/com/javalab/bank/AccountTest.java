package com.javalab.bank;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    void depositThrowsExceptionForNullAmount() {
        // validatePositiveAmountがamount.compareTo()を呼ぶ前にnullチェックしておらず、
        // deposit(null)がNullPointerExceptionになっていた。
        assertThrows(InvalidAmountException.class, () -> account.deposit(null));
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
    void withdrawThrowsExceptionForNullAmount() {
        assertThrows(InvalidAmountException.class, () -> account.withdraw(null));
    }

    @Test
    void depositNormalizesAmountToTwoDecimalPlaces() {
        // "deposit 1000.5555"がそのまま残高になると通貨として不正な値(小数第3位以下)を
        // 保持してしまうため、スケール2(HALF_UP)に正規化されることを確認する。
        account.deposit(new BigDecimal("1000.5555"));

        assertEquals(0, new BigDecimal("1000.56").compareTo(account.getBalance()));
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
    void historyRecordsFixedTimestampFromInjectedClock() {
        // Transactionに取引日時がなく「いつの取引か」分からなかった問題への対応。
        // Clockを注入することで、取引日時が決定的に記録されることを確認する。
        LocalDateTime fixedTime = LocalDateTime.of(2026, 8, 1, 10, 30);
        Clock fixedClock = Clock.fixed(fixedTime.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        Account clockedAccount = new Account("Bob", fixedClock);

        clockedAccount.deposit(new BigDecimal("1000"));

        assertEquals(fixedTime, clockedAccount.getHistory().get(0).timestamp());
    }

    @Test
    void transferToMovesAmountFromSourceToDestination() {
        // 「複数口座+振込」という未消化の学習テーマへの対応。
        // 振込元から出金され、振込先に同額が入金される(2口座をまたぐ操作)ことを確認する。
        Account destination = new Account("Bob");
        account.deposit(new BigDecimal("1000"));

        account.transferTo(destination, new BigDecimal("300"));

        assertEquals(0, new BigDecimal("700").compareTo(account.getBalance()));
        assertEquals(0, new BigDecimal("300").compareTo(destination.getBalance()));
    }

    @Test
    void transferToDoesNotChangeEitherAccountWhenSourceBalanceIsInsufficient() {
        // 振込元の残高不足の場合、振込元・振込先のどちらも変更されない
        // (2口座をまたぐ不変条件: 合計残高が変わらない)ことを確認する。
        Account destination = new Account("Bob");
        account.deposit(new BigDecimal("100"));
        destination.deposit(new BigDecimal("500"));

        assertThrows(InsufficientBalanceException.class,
                () -> account.transferTo(destination, new BigDecimal("300")));

        assertEquals(0, new BigDecimal("100").compareTo(account.getBalance()));
        assertEquals(0, new BigDecimal("500").compareTo(destination.getBalance()));
    }

    @Test
    void historyReturnsUnmodifiableView() {
        // getHistory()が返すListはList.copyOf()によるコピーであり、外部から add() しても
        // Account内部の履歴には影響しない(カプセル化が破られていない)ことを確認する。
        account.deposit(new BigDecimal("1000"));
        List<Transaction> history = account.getHistory();

        assertThrows(UnsupportedOperationException.class,
                () -> history.add(new Transaction(
                        Transaction.Type.DEPOSIT, BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.now())));
        assertTrue(account.getHistory().size() == 1);
    }
}
