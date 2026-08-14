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

@ExtendWith(MockitoExtension.class)
class OrderNotificationServiceTest {

    @Mock
    private EmailSender emailSender;

    @Test
    void notifyOrderConfirmedSendsEmailWithCorrectArguments() {
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
        OrderNotificationService service = new OrderNotificationService(emailSender);
        Order order = new Order(2L, "bob@example.com", new BigDecimal("500"));
        doThrow(new RuntimeException("SMTP接続に失敗しました"))
                .when(emailSender).send(anyString(), anyString(), anyString());

        assertThrows(OrderNotificationException.class, () -> service.notifyOrderConfirmed(order));
    }

    @Test
    void notifyOrderConfirmedSkipsSendingForOrderWithNonPositiveTotal() {
        OrderNotificationService service = new OrderNotificationService(emailSender);
        Order order = new Order(3L, "carol@example.com", BigDecimal.ZERO);

        service.notifyOrderConfirmed(order);

        verify(emailSender, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void notifyAllSendsOnlyForOrdersWithPositiveTotal() {
        OrderNotificationService service = new OrderNotificationService(emailSender);
        List<Order> orders = List.of(
                new Order(1L, "alice@example.com", new BigDecimal("1000")),
                new Order(2L, "bob@example.com", BigDecimal.ZERO),
                new Order(3L, "carol@example.com", new BigDecimal("500")));

        service.notifyOrders(orders);

        verify(emailSender, times(2)).send(anyString(), anyString(), anyString());
    }
}
