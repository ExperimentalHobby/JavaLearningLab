package com.javalab.designpatterns.strategy;

/**
 * 決済方法を表すインターフェース。Strategyパターンのミニアプリ実装。
 */
public interface PaymentStrategy {

    String pay(int amount);
}
