package com.javalab.junitpractice;

import java.util.List;

/**
 * 注文確認メールの送信を行うサービス。{@link EmailSender}をコンストラクタ注入することで、
 * テストではMockitoによるモック、本番では実際の送信実装(例: {@link ConsoleEmailSender})を差し替えられる。
 */
public class OrderNotificationService {

    private final EmailSender emailSender;

    public OrderNotificationService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * 注文確認メールを送信する。合計金額が0円以下の注文は送信をスキップする。
     * @param order 対象の注文
     * @throws OrderNotificationException メール送信に失敗した場合
     */
    public void notifyOrderConfirmed(Order order) {
        if (order.total().signum() <= 0) {
            return;
        }
        String subject = "ご注文ありがとうございます(注文番号: " + order.id() + ")";
        String body = "合計金額 " + order.total().toPlainString() + "円 のご注文を確認しました。";
        try {
            emailSender.send(order.customerEmail(), subject, body);
        } catch (RuntimeException e) {
            throw new OrderNotificationException("注文確認メールの送信に失敗しました(注文番号: " + order.id() + ")", e);
        }
    }

    /**
     * 複数の注文についてまとめて確認メールを送信する。
     * @param orders 対象の注文一覧
     */
    public void notifyOrders(List<Order> orders) {
        for (Order order : orders) {
            notifyOrderConfirmed(order);
        }
    }
}
