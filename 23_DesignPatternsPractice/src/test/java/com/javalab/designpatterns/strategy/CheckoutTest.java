package com.javalab.designpatterns.strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link Checkout} のStrategyパターン実装を検証するテスト。
 * コンストラクタで注入した決済方法が使われること、および{@link Checkout#setStrategy}で
 * 実行時に決済方法を切り替えられることの両方を確認する。
 */
class CheckoutTest {

    @Test
    void checkoutUsesInjectedStrategyToPay() {
        Checkout checkout = new Checkout(new CreditCardPayment());

        String result = checkout.checkout(1000);

        assertEquals("クレジットカードで1000円を決済しました", result);
    }

    @Test
    void setStrategySwitchesPaymentMethodAtRuntime() {
        // 最初はクレジットカードで生成したCheckoutに対し、setStrategy()でPayPalへ切り替えると、
        // 以降のcheckout()呼び出しがPayPalの実装を使うことを確認する。
        Checkout checkout = new Checkout(new CreditCardPayment());

        checkout.setStrategy(new PayPalPayment());
        String result = checkout.checkout(500);

        assertEquals("PayPalで500円を決済しました", result);
    }
}
