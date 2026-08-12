package com.javalab.bank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 銀行口座。残高(balance)と取引履歴(history)はいずれもprivateで保護し、
 * {@link #deposit}/{@link #withdraw} を通じてのみ変更できるようにカプセル化している。
 */
public class Account {

    private final String ownerName;
    private BigDecimal balance = BigDecimal.ZERO;
    private final List<Transaction> history = new ArrayList<>();

    public Account(String ownerName) {
        this.ownerName = ownerName;
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
     * @param amount 入金額
     * @throws InvalidAmountException amountが0以下の場合
     */
    public void deposit(BigDecimal amount) {
        validatePositiveAmount(amount, "入金額");
        balance = balance.add(amount);
        history.add(new Transaction(Transaction.Type.DEPOSIT, amount, balance));
    }

    /**
     * 出金する。
     * @param amount 出金額
     * @throws InvalidAmountException amountが0以下の場合
     * @throws InsufficientBalanceException amountが残高を超える場合
     */
    public void withdraw(BigDecimal amount) {
        validatePositiveAmount(amount, "出金額");
        if (amount.compareTo(balance) > 0) {
            throw new InsufficientBalanceException(
                    "残高が不足しています: 残高=" + balance + ", 出金額=" + amount);
        }
        balance = balance.subtract(amount);
        history.add(new Transaction(Transaction.Type.WITHDRAWAL, amount, balance));
    }

    private static void validatePositiveAmount(BigDecimal amount, String label) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(label + "は0より大きい必要があります: " + amount);
        }
    }
}
