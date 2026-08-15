package com.javalab.junitpractice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * {@link OrderNotificationService} を{@link EmailSender}のMockitoモックを使って検証するテスト。
 * このプロジェクトの他の学習フォルダは基本的に実オブジェクト(実DB・実サーバー等)でテストしているが、
 * メール送信は実行すると外部への副作用が大きく実リソースでのテストに向かないため、
 * あえてMockitoを導入して「モックでしか検証できないもの」を学ぶ題材にしている。
 * 実オブジェクトによる統合テストは{@link MainTest}が担当する。
 */
@ExtendWith(MockitoExtension.class)
class OrderNotificationServiceTest {

    @Mock
    private EmailSender emailSender;

    @Test
    void notifyOrderConfirmedSendsEmailWithCorrectArguments() {
        // emailSenderへ渡される宛先・件名・本文が期待通りに組み立てられているかを、
        // 実際に送信せずverify()で呼び出し内容だけを検証する(Mockitoの基本的な使い方)。
        OrderNotificationService service = new OrderNotificationService(emailSender);
        Order order = new Order(1L, "alice@example.com", new BigDecimal("1000"));

        service.notifyOrderConfirmed(order);

        verify(emailSender).send(
                eq("alice@example.com"),
                eq("ご注文ありがとうございます(注文番号: 1)"),
                eq("合計金額 1000円 のご注文を確認しました。"));
    }

    @Test
    void notifyOrderConfirmedWrapsEmailSenderFailureIntoOrderNotificationException() {
        // doThrow().when(...)で、emailSender.send()が呼ばれたら例外を投げるよう設定する
        // (Mockitoでモックに「例外を投げさせる」ためのスタブ設定)。
        // OrderNotificationServiceがこの例外を捕まえ、独自のOrderNotificationExceptionへ
        // ラップして再スローすることを確認する。
        OrderNotificationService service = new OrderNotificationService(emailSender);
        Order order = new Order(2L, "bob@example.com", new BigDecimal("500"));
        doThrow(new RuntimeException("SMTP接続に失敗しました"))
                .when(emailSender).send(anyString(), anyString(), anyString());

        assertThrows(OrderNotificationException.class, () -> service.notifyOrderConfirmed(order));
    }

    @Test
    void notifyOrderConfirmedSkipsSendingForOrderWithNonPositiveTotal() {
        // 合計金額0円の注文はメール送信自体が行われないという仕様を、
        // verify(emailSender, never())で「一度も呼ばれていないこと」として検証する。
        OrderNotificationService service = new OrderNotificationService(emailSender);
        Order order = new Order(3L, "carol@example.com", BigDecimal.ZERO);

        service.notifyOrderConfirmed(order);

        verify(emailSender, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void notifyAllSendsOnlyForOrdersWithPositiveTotal() {
        // 3件中1件(合計0円)がスキップされ、残り2件だけメール送信されることを
        // verify(emailSender, times(2))で呼び出し回数として検証する。
        OrderNotificationService service = new OrderNotificationService(emailSender);
        List<Order> orders = List.of(
                new Order(1L, "alice@example.com", new BigDecimal("1000")),
                new Order(2L, "bob@example.com", BigDecimal.ZERO),
                new Order(3L, "carol@example.com", new BigDecimal("500")));

        service.notifyOrders(orders);

        verify(emailSender, times(2)).send(anyString(), anyString(), anyString());
    }
}
