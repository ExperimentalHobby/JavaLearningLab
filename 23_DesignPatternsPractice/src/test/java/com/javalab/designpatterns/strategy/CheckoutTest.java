package com.javalab.designpatterns.strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckoutTest {

    @Test
    void checkoutUsesInjectedStrategyToPay() {
        Checkout checkout = new Checkout(new CreditCardPayment());

        String result = checkout.checkout(1000);

        assertEquals("クレジットカードで1000円を決済しました", result);
    }

    @Test
    void setStrategySwitchesPaymentMethodAtRuntime() {
        Checkout checkout = new Checkout(new CreditCardPayment());

        checkout.setStrategy(new PayPalPayment());
        String result = checkout.checkout(500);

        assertEquals("PayPalで500円を決済しました", result);
    }
}
