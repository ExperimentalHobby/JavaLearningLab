package com.javalab.jdbccrud;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * 簡易データベースアプリのエントリーポイント。
 * 標準入力からコマンドを読み取り、SQLiteに保存されたタスクをCRUD操作する対話型REPLを提供する。
 */
public class Main {

    public static void main(String[] args) {
        // args[0]が指定されていればそのパスを、未指定ならデフォルトの"tasks.db"を使う。
        String dbPath = args.length > 0 ? args[0] : "tasks.db";
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            TaskRepository repository = new TaskRepository(connection);
            repository.initSchema();
            run(new Scanner(System.in), System.out, repository);
        } catch (SQLException e) {
            // DBに接続できない場合にスタックトレースをそのまま表示せず、
            // 他のアプリと同じくエラーメッセージを出して終了する。
            System.out.println("エラー: データベースに接続できませんでした: " + e.getMessage());
        }
    }

    /**
     * REPLループ本体。テストから{@link Scanner}/{@link PrintStream}/{@link TaskRepository}を
     * 差し替えられるよう分離している。
     * コマンド: {@code add <タイトル>} / {@code list} / {@code update <id> <タイトル>} /
     * {@code done <id>} / {@code delete <id>} / {@code addBatch <タイトル1>,<タイトル2>,...} / {@code exit}。
     * DBエラーや不正な入力はループを止めずエラー表示のみ行い、次のコマンド入力を継続する。
     * @param scanner コマンド読み取り元
     * @param out 結果出力先
     * @param repository タスクの永続化を担うリポジトリ
     */
    static void run(Scanner scanner, PrintStream out, TaskRepository repository) {
        out.println("簡易データベースアプリ。コマンド: add <タイトル> / list / update <id> <タイトル> / "
                + "done <id> / delete <id> / addBatch <タイトル1>,<タイトル2>,... / exit");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+", 2);
            String command = parts[0];
            try {
                switch (command) {
                    case "exit" -> {
                        return;
                    }
                    case "add" -> handleAdd(repository, parts, out);
                    case "list" -> handleList(repository, out);
                    case "update" -> handleUpdate(repository, parts, out);
                    case "done" -> handleDone(repository, parts, out);
                    case "delete" -> handleDelete(repository, parts, out);
                    case "addBatch" -> handleAddBatch(repository, parts, out);
                    default -> out.println("不明なコマンドです: " + line);
                }
            } catch (TaskRepositoryException | NumberFormatException e) {
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    private static void handleAdd(TaskRepository repository, String[] parts, PrintStream out) {
        if (parts.length != 2) {
            out.println("使い方: add <タイトル>");
            return;
        }
        long id = repository.insert(parts[1]);
        out.println("追加しました: id=" + id);
    }

    private static void handleList(TaskRepository repository, PrintStream out) {
        List<Task> tasks = repository.findAll();
        if (tasks.isEmpty()) {
            out.println("タスクはありません");
            return;
        }
        for (Task task : tasks) {
            out.println(task.id() + ": " + task.title() + (task.done() ? " [完了]" : ""));
        }
    }

    private static void handleUpdate(TaskRepository repository, String[] parts, PrintStream out) {
        if (parts.length != 2) {
            out.println("使い方: update <id> <タイトル>");
            return;
        }
        String[] idAndTitle = parts[1].split("\\s+", 2);
        if (idAndTitle.length != 2) {
            out.println("使い方: update <id> <タイトル>");
            return;
        }
        long id = Long.parseLong(idAndTitle[0]);
        boolean updated = repository.updateTitle(id, idAndTitle[1]);
        out.println(updated ? "更新しました: id=" + id : "該当するタスクが見つかりません: id=" + id);
    }

    private static void handleDone(TaskRepository repository, String[] parts, PrintStream out) {
        if (parts.length != 2) {
            out.println("使い方: done <id>");
            return;
        }
        long id = Long.parseLong(parts[1]);
        boolean updated = repository.updateDone(id, true);
        out.println(updated ? "完了にしました: id=" + id : "該当するタスクが見つかりません: id=" + id);
    }

    private static void handleDelete(TaskRepository repository, String[] parts, PrintStream out) {
        if (parts.length != 2) {
            out.println("使い方: delete <id>");
            return;
        }
        long id = Long.parseLong(parts[1]);
        boolean deleted = repository.delete(id);
        out.println(deleted ? "削除しました: id=" + id : "該当するタスクが見つかりません: id=" + id);
    }

    private static void handleAddBatch(TaskRepository repository, String[] parts, PrintStream out) {
        if (parts.length != 2) {
            out.println("使い方: addBatch <タイトル1>,<タイトル2>,...");
            return;
        }
        int count = repository.insertAll(List.of(parts[1].split(",")));
        out.println(count + "件のタスクを一括登録しました");
    }
}
