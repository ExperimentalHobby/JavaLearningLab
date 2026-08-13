package com.javalab.designpatterns.strategy;

/**
 * 決済処理を行うContext。{@link PaymentStrategy}を注入・差し替えることで決済方法を切り替えられる。
 */
public class Checkout {

    private PaymentStrategy strategy;

    public Checkout(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public String checkout(int amount) {
        return strategy.pay(amount);
    }
}
