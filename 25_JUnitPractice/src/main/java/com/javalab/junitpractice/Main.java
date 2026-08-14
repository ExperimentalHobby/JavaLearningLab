package com.javalab.junitpractice;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 単体テスト練習(注文通知デモ)のエントリーポイント。
 * {@link OrderNotificationService}を{@link ConsoleEmailSender}と組み合わせて対話的に試せるREPLを提供する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    /**
     * REPLループ本体。テストから{@link Scanner}/{@link PrintStream}を差し替えられるよう分離している。
     * コマンド: {@code order <メールアドレス> <金額>}(注文確認メールを送信) / {@code exit}。
     * @param scanner コマンド読み取り元
     * @param out 結果出力先
     */
    static void run(Scanner scanner, PrintStream out) {
        OrderNotificationService service = new OrderNotificationService(new ConsoleEmailSender(out));
        AtomicLong nextId = new AtomicLong(1);
        out.println("単体テスト練習(注文通知デモ)。コマンド: order <メールアドレス> <金額> / exit");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");
            String command = parts[0];
            try {
                switch (command) {
                    case "exit" -> {
                        return;
                    }
                    case "order" -> {
                        Order order = new Order(nextId.getAndIncrement(), parts[1], new BigDecimal(parts[2]));
                        service.notifyOrderConfirmed(order);
                    }
                    default -> out.println("不明なコマンドです: " + line);
                }
            } catch (RuntimeException e) {
                out.println("エラー: " + e.getMessage());
            }
        }
    }
}
