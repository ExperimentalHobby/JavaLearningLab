package com.javalab.junitpractice;

/**
 * メール送信を表すインターフェース。実運用ではSMTP/SES等の外部サービスを叩く実装を想定する。
 * 実際に送信すると副作用が大きく実リソースでのテストに向かないため、テストではMockitoでモック化する。
 */
public interface EmailSender {

    void send(String to, String subject, String body);
}
