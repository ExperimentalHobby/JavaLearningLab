package com.javalab.designpatterns.decorator;

import com.javalab.designpatterns.strategy.CreditCardPayment;
import com.javalab.designpatterns.strategy.PaymentStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link DiscountedPayment} のDecoratorパターン実装を検証するテスト。
 * StrategyがpayメソッドをCreditCardPayment/PayPalPaymentで丸ごと差し替えるのに対し、
 * Decoratorは既存のPaymentStrategyをラップして割引という振る舞いを"追加"する点を確認する。
 */
class DiscountedPaymentTest {

    @Test
    void payAppliesDiscountBeforeDelegatingToWrappedStrategy() {
        PaymentStrategy discounted = new DiscountedPayment(new CreditCardPayment(), 10);

        String result = discounted.pay(1000);

        assertEquals("クレジットカードで900円を決済しました(10%割引適用)", result);
    }

    @Test
    void constructorThrowsIllegalArgumentExceptionForDiscountPercentOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new DiscountedPayment(new CreditCardPayment(), 101));
    }
}
