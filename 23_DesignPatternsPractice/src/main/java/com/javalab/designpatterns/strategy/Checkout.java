package com.javalab.designpatterns.strategy;

/**
 * 決済処理を行うContext。{@link PaymentStrategy}を注入・差し替えることで決済方法を切り替えられる。
 */
public class Checkout {

    private PaymentStrategy strategy;

    public Checkout(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * 決済方法を実行時に差し替える(Strategyパターンの要点: アルゴリズムをコンテキストから独立させ、
     * 実行時に切り替え可能にする)。
     * @param strategy 新しい決済方法
     */
    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * 現在設定されている決済方法で決済する。
     * @param amount 決済金額
     * @return 決済結果メッセージ
     */
    public String checkout(int amount) {
        return strategy.pay(amount);
    }
}
