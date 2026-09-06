package com.javalab.reflectionannotation;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * 独自アノテーション+リフレクションによる簡易バリデーションのエントリーポイント。
 * 標準入力からコマンド(register/exit)を1行ずつ読み取り、結果を標準出力に表示する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    static void run(Scanner scanner, PrintStream out) {
        out.println("リフレクション検証ツールへようこそ。コマンド: register <氏名> <年齢> <メール> / exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                handleCommand(line, out);
            } catch (NumberFormatException e) {
                out.println("エラー: 年齢の形式が不正です");
            } catch (IllegalArgumentException e) {
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    private static void handleCommand(String line, PrintStream out) {
        if (!line.startsWith("register ")) {
            throw new IllegalArgumentException("不明なコマンドです: " + line);
        }

        String[] parts = line.substring(9).trim().split("\\s+", 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException("入力形式が不正です: " + line);
        }

        UserForm form = new UserForm(parts[0], Integer.parseInt(parts[1]), parts[2]);
        List<ValidationViolation> violations = Validator.validate(form);

        if (violations.isEmpty()) {
            out.println("登録成功: " + form.name());
        } else {
            violations.forEach(v -> out.println("違反: " + v.message()));
        }
    }
}
