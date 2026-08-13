package com.javalab.designpatterns.strategy;

public class CreditCardPayment implements PaymentStrategy {

    @Override
    public String pay(int amount) {
        return "クレジットカードで" + amount + "円を決済しました";
    }
}
