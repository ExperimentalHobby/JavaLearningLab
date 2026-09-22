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
        OrderNotificationService service =
                new OrderNotificationService(new ConsoleEmailSender(out), new RegexEmailValidator());
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
                    case "order" -> handleOrder(service, nextId, parts, out);
                    default -> out.println("不明なコマンドです: " + line);
                }
            } catch (IllegalArgumentException | OrderNotificationException e) {
                // IllegalArgumentExceptionは引数検証・BigDecimalの数値変換失敗、
                // OrderNotificationExceptionはメール送信失敗から送出される。
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    private static void handleOrder(OrderNotificationService service, AtomicLong nextId, String[] parts, PrintStream out) {
        if (parts.length != 3) {
            throw new IllegalArgumentException("使用方法: order <メールアドレス> <金額>");
        }
        Order order = new Order(nextId.getAndIncrement(), parts[1], new BigDecimal(parts[2]));
        NotificationOutcome outcome = service.notifyOrderConfirmed(order);
        // 修正前はスキップ時に何も表示されず、利用者には「何も起きなかった」ようにしか見えなかった。
        switch (outcome) {
            case SKIPPED_NON_POSITIVE_TOTAL ->
                    out.println("注文番号" + order.id() + "は合計金額が0円以下のため送信をスキップしました");
            case SKIPPED_INVALID_EMAIL ->
                    out.println("注文番号" + order.id() + "はメールアドレスの形式が不正なため送信をスキップしました");
            case SENT -> {
                // ConsoleEmailSenderが送信内容を出力済みのため、ここでは追加表示不要。
            }
        }
    }
}
