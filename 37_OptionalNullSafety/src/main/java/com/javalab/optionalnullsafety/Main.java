package com.javalab.optionalnullsafety;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * 社員名簿検索ツールのエントリーポイント。
 * 標準入力からコマンド(add/find/email/list/exit)を1行ずつ読み取り、結果を標準出力に表示する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    static void run(Scanner scanner, PrintStream out) {
        EmployeeRepository repository = new EmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        out.println("社員名簿検索ツールへようこそ。"
                + "コマンド: add <ID> <氏名> <メール> / find <IDまたはメール> / email <ID> / list / exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                handleCommand(repository, service, line, out);
            } catch (EmployeeNotFoundException | IllegalArgumentException e) {
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    private static void handleCommand(
            EmployeeRepository repository, EmployeeService service, String line, PrintStream out) {
        if (line.equals("list")) {
            repository.findAll().forEach(e -> out.println(e.id() + " " + e.name() + " " + e.email()));
        } else if (line.startsWith("add ")) {
            String[] parts = line.substring(4).trim().split("\\s+", 3);
            if (parts.length != 3) {
                throw new IllegalArgumentException("入力形式が不正です: " + line);
            }
            repository.add(new Employee(parts[0], parts[1], parts[2]));
            out.println("登録しました: " + parts[0]);
        } else if (line.startsWith("find ")) {
            String idOrEmail = line.substring(5).trim();
            service.findByIdOrEmail(idOrEmail)
                    .ifPresentOrElse(
                            e -> out.println(e.id() + " " + e.name() + " " + e.email()),
                            () -> out.println("該当する社員が見つかりません: " + idOrEmail));
        } else if (line.startsWith("email ")) {
            String id = line.substring(6).trim();
            out.println(service.emailOf(id));
        } else {
            throw new IllegalArgumentException("不明なコマンドです: " + line);
        }
    }
}
