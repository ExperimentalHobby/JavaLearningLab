package com.javalab.designpatterns.decorator;

import com.javalab.designpatterns.strategy.PaymentStrategy;

/**
 * 既存の{@link PaymentStrategy}をラップし、決済金額に割引を適用してから委譲するDecorator。
 * Strategyがアルゴリズム(決済方法)を丸ごと差し替えるのに対し、Decoratorは既存の実装を
 * 継承せず合成(コンストラクタで受け取ってラップする)によって振る舞いを追加する点が異なる。
 * {@link PaymentStrategy}を実装しているため、既存のCheckoutにそのまま渡すことができる
 * (デコレータ対象と同じインターフェースを実装するのがDecoratorパターンの要点)。
 */
public class DiscountedPayment implements PaymentStrategy {

    private final PaymentStrategy delegate;
    private final int discountPercent;

    /**
     * @param delegate ラップする決済方法
     * @param discountPercent 割引率(0〜100)
     * @throws IllegalArgumentException discountPercentが0〜100の範囲外の場合
     */
    public DiscountedPayment(PaymentStrategy delegate, int discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("割引率は0〜100の範囲で指定してください: " + discountPercent);
        }
        this.delegate = delegate;
        this.discountPercent = discountPercent;
    }

    @Override
    public String pay(int amount) {
        int discountedAmount = amount - amount * discountPercent / 100;
        return delegate.pay(discountedAmount) + "(" + discountPercent + "%割引適用)";
    }
}
