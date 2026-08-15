package com.javalab.designpatterns.strategy;

/**
 * PayPal決済(Strategyパターンにおける具象戦略の1つ)。
 */
public class PayPalPayment implements PaymentStrategy {

    @Override
    public String pay(int amount) {
        return "PayPalで" + amount + "円を決済しました";
    }
}
