package com.javalab.fileorganizer;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * 対話式CLIファイル整理ツールのエントリーポイント。
 * 標準入力から{@code organize <ディレクトリパス>}コマンドを1行ずつ読み取り、
 * {@link FileOrganizer} で整理した結果を表示する。{@code exit} で終了する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    /**
     * REPLループ本体。{@link Scanner}/{@link PrintStream} を引数として受け取ることで、
     * テストから {@code StringReader}/{@code ByteArrayOutputStream} を注入できるようにしている。
     * @param scanner 入力読み取り元
     * @param out 出力先
     */
    static void run(Scanner scanner, PrintStream out) {
        out.println("ファイル整理ツールへようこそ。コマンド: organize <ディレクトリパス> / exit");

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
            } catch (FileOrganizerException | IllegalArgumentException e) {
                // 不正なパス・不明なコマンドはクラッシュさせず、エラー表示して次の入力へ進む。
                out.println("エラー: " + e.getMessage());
            } catch (IOException e) {
                out.println("エラー: ファイル操作に失敗しました: " + e.getMessage());
            }
        }
    }

    /**
     * 1行分のコマンドを解釈して実行する。
     * @param line 入力行
     * @param out 出力先
     * @throws IOException ファイル整理中のI/Oエラー
     * @throws FileOrganizerException 指定パスが不正な場合
     * @throws IllegalArgumentException コマンドが不明な場合
     */
    private static void handleCommand(String line, PrintStream out) throws IOException {
        if (line.startsWith("organize ")) {
            Path sourceDir = Path.of(line.substring(9).trim());
            List<Path> moved = FileOrganizer.organize(sourceDir);
            for (Path path : moved) {
                out.println("移動しました: " + path);
            }
            out.println(moved.size() + "件のファイルを整理しました");
        } else {
            throw new IllegalArgumentException("不明なコマンドです: " + line);
        }
    }
}
