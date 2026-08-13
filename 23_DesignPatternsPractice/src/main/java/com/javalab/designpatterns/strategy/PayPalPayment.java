package com.javalab.designpatterns.strategy;

public class PayPalPayment implements PaymentStrategy {

    @Override
    public String pay(int amount) {
        return "PayPalで" + amount + "円を決済しました";
    }
}
