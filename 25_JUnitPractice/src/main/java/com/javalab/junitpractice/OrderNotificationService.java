package com.javalab.junitpractice;

import java.util.ArrayList;
import java.util.List;

/**
 * 注文確認メールの送信を行うサービス。{@link EmailSender}/{@link EmailValidator}をコンストラクタ注入することで、
 * テストではMockitoによるモック、本番では実際の実装(例: {@link ConsoleEmailSender}/{@link RegexEmailValidator})を
 * 差し替えられる。
 */
public class OrderNotificationService {

    private final EmailSender emailSender;
    private final EmailValidator emailValidator;

    public OrderNotificationService(EmailSender emailSender, EmailValidator emailValidator) {
        this.emailSender = emailSender;
        this.emailValidator = emailValidator;
    }

    /**
     * 注文確認メールを送信する。合計金額が0円以下、またはメールアドレスの形式が不正な注文は
     * 送信をスキップする(呼び出し側は戻り値でスキップ理由を判別できる)。
     * @param order 対象の注文
     * @return 送信結果
     * @throws OrderNotificationException メール送信に失敗した場合
     */
    public NotificationOutcome notifyOrderConfirmed(Order order) {
        if (order.total().signum() <= 0) {
            return NotificationOutcome.SKIPPED_NON_POSITIVE_TOTAL;
        }
        if (!emailValidator.isValid(order.customerEmail())) {
            return NotificationOutcome.SKIPPED_INVALID_EMAIL;
        }
        String subject = "ご注文ありがとうございます(注文番号: " + order.id() + ")";
        String body = "合計金額 " + order.total().toPlainString() + "円 のご注文を確認しました。";
        try {
            emailSender.send(order.customerEmail(), subject, body);
        } catch (RuntimeException e) {
            throw new OrderNotificationException("注文確認メールの送信に失敗しました(注文番号: " + order.id() + ")", e);
        }
        return NotificationOutcome.SENT;
    }

    /**
     * 複数の注文についてまとめて確認メールを送信する。1件の送信失敗が残りの送信を妨げないよう、
     * 失敗を集約して返す(送信失敗を呼び出し元に伝播させない)。
     * @param orders 対象の注文一覧
     * @return 送信数・スキップ数・失敗一覧を含む結果
     */
    public BatchNotificationResult notifyOrders(List<Order> orders) {
        int sentCount = 0;
        int skippedCount = 0;
        List<OrderNotificationException> failures = new ArrayList<>();
        for (Order order : orders) {
            try {
                NotificationOutcome outcome = notifyOrderConfirmed(order);
                if (outcome == NotificationOutcome.SENT) {
                    sentCount++;
                } else {
                    skippedCount++;
                }
            } catch (OrderNotificationException e) {
                failures.add(e);
            }
        }
        return new BatchNotificationResult(sentCount, skippedCount, failures);
    }
}
