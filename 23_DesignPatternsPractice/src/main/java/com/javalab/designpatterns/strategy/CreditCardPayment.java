package com.javalab.designpatterns.strategy;

/**
 * クレジットカード決済(Strategyパターンにおける具象戦略の1つ)。
 */
public class CreditCardPayment implements PaymentStrategy {

    @Override
    public String pay(int amount) {
        return "クレジットカードで" + amount + "円を決済しました";
    }
}
