package com.javalab.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 銀行口座。残高(balance)と取引履歴(history)はいずれもprivateで保護し、
 * {@link #deposit}/{@link #withdraw}/{@link #transferTo} を通じてのみ変更できるようにカプセル化している。
 */
public class Account {

    // 通貨として妥当なスケール(小数第2位まで)に正規化する。
    private static final int CURRENCY_SCALE = 2;

    private final String ownerName;
    private final Clock clock;
    private BigDecimal balance = BigDecimal.ZERO;
    private final List<Transaction> history = new ArrayList<>();

    public Account(String ownerName) {
        this(ownerName, Clock.systemDefaultZone());
    }

    /**
     * @param ownerName 口座名義人名
     * @param clock 取引日時の記録に使う時計(テストでは固定時刻の{@link Clock}を注入できる)
     */
    public Account(String ownerName, Clock clock) {
        this.ownerName = ownerName;
        this.clock = clock;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * @return 取引履歴の変更不可なビュー。外部から{@code add}等で内部状態を書き換えられないようにしている
     */
    public List<Transaction> getHistory() {
        return List.copyOf(history);
    }

    /**
     * 入金する。
     * @param amount 入金額(スケール2・{@link RoundingMode#HALF_UP}に正規化して記録する)
     * @throws InvalidAmountException amountがnull、または0以下の場合
     */
    public void deposit(BigDecimal amount) {
        validatePositiveAmount(amount, "入金額");
        BigDecimal normalized = normalize(amount);
        balance = balance.add(normalized);
        history.add(new Transaction(Transaction.Type.DEPOSIT, normalized, balance, LocalDateTime.now(clock)));
    }

    /**
     * 出金する。
     * @param amount 出金額(スケール2・{@link RoundingMode#HALF_UP}に正規化して記録する)
     * @throws InvalidAmountException amountがnull、または0以下の場合
     * @throws InsufficientBalanceException amountが残高を超える場合
     */
    public void withdraw(BigDecimal amount) {
        validatePositiveAmount(amount, "出金額");
        BigDecimal normalized = normalize(amount);
        if (normalized.compareTo(balance) > 0) {
            throw new InsufficientBalanceException(
                    "残高が不足しています: 残高=" + balance + ", 出金額=" + normalized);
        }
        balance = balance.subtract(normalized);
        history.add(new Transaction(Transaction.Type.WITHDRAWAL, normalized, balance, LocalDateTime.now(clock)));
    }

    /**
     * この口座から{@code destination}へ振り込む。出金→入金の2ステップだが、まず出金側で
     * 残高不足を検証してから両方の口座を更新するため、残高不足の場合はどちらの口座も
     * 変更されない(2口座をまたぐ不変条件を保つ)。
     * @param destination 振込先口座
     * @param amount 振込額
     * @throws InvalidAmountException amountがnull、または0以下の場合
     * @throws InsufficientBalanceException amountがこの口座の残高を超える場合
     */
    public void transferTo(Account destination, BigDecimal amount) {
        withdraw(amount);
        destination.deposit(amount);
    }

    private static BigDecimal normalize(BigDecimal amount) {
        return amount.setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
    }

    private static void validatePositiveAmount(BigDecimal amount, String label) {
        if (amount == null) {
            throw new InvalidAmountException(label + "はnullにできません");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(label + "は0より大きい必要があります: " + amount);
        }
    }
}
