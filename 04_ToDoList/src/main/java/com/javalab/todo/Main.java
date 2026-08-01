package com.javalab.todo;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * 対話式CLI ToDoリストのエントリーポイント。
 * 標準入力からコマンド({@code add}/{@code done}/{@code remove}/{@code list}/{@code save}/{@code load})を
 * 1行ずつ読み取り、結果を標準出力に表示する。{@code exit} で終了する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out, new File("todo.txt"));
    }

    /**
     * REPLループ本体。{@link Scanner}/{@link PrintStream} に加え、保存・読込先のファイルも
     * 引数で受け取ることで、テストから一時ファイルを注入できるようにしている。
     * @param scanner 入力読み取り元
     * @param out 出力先
     * @param file save/loadコマンドの対象ファイル
     */
    static void run(Scanner scanner, PrintStream out, File file) {
        ToDoList toDoList = new ToDoList();

        out.println("ToDoリストへようこそ。コマンド: add <説明> / done <番号> / remove <番号> / list / save / load / exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                handleCommand(toDoList, line, out, file);
            } catch (ToDoListException e) {
                // 不正な番号・不明なコマンドはクラッシュさせず、エラー表示して次の入力へ進む。
                out.println("エラー: " + e.getMessage());
            } catch (IOException e) {
                out.println("エラー: ファイル操作に失敗しました: " + e.getMessage());
            }
        }
    }

    /**
     * 1行分のコマンドを解釈して実行する。
     * @param toDoList 操作対象のToDoリスト
     * @param line 入力行
     * @param out 出力先
     * @param file save/loadコマンドの対象ファイル
     * @throws IOException save/load時のファイル操作に失敗した場合
     * @throws ToDoListException コマンドが不明、または番号が不正な場合
     */
    private static void handleCommand(ToDoList toDoList, String line, PrintStream out, File file)
            throws IOException {
        if (line.equals("list")) {
            printTasks(toDoList, out);
        } else if (line.equals("save")) {
            toDoList.saveTo(file);
            out.println("保存しました");
        } else if (line.equals("load")) {
            toDoList.loadFrom(file);
            out.println("読み込みました");
        } else if (line.startsWith("add ")) {
            toDoList.add(line.substring(4));
        } else if (line.startsWith("done ")) {
            toDoList.complete(parseIndex(line.substring(5)));
        } else if (line.startsWith("remove ")) {
            toDoList.remove(parseIndex(line.substring(7)));
        } else {
            throw new ToDoListException("不明なコマンドです: " + line);
        }
    }

    private static int parseIndex(String indexText) {
        try {
            return Integer.parseInt(indexText);
        } catch (NumberFormatException e) {
            throw new ToDoListException("番号の形式が不正です: " + indexText);
        }
    }

    private static void printTasks(ToDoList toDoList, PrintStream out) {
        List<Task> tasks = toDoList.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            out.println(i + ": " + tasks.get(i).toFileLine());
        }
    }
}
