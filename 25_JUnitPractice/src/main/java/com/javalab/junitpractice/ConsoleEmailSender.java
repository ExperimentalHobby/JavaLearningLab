package com.javalab.junitpractice;

import java.io.PrintStream;

/**
 * {@link EmailSender}のデモ用実装。実際のSMTP接続は行わず、指定した出力先にメール内容を表示するだけの簡易版。
 */
public class ConsoleEmailSender implements EmailSender {

    private final PrintStream out;

    public ConsoleEmailSender(PrintStream out) {
        this.out = out;
    }

    @Override
    public void send(String to, String subject, String body) {
        out.println("[メール送信] To: " + to);
        out.println("件名: " + subject);
        out.println("本文: " + body);
    }
}
