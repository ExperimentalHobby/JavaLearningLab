package com.javalab.todo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream, File)} のREPLループを結合テストするクラス。
 */
class MainTest {

    @TempDir
    Path tempDir;

    @Test
    void invalidCommandShowsErrorAndContinuesWithoutCrashing() {
        // "foo" はadd/done/remove/list/save/loadのいずれにも一致しない不正コマンド。
        // エラー表示のみで継続し、直後の "add 牛乳を買う" が正常に処理されることを確認する。
        Scanner scanner = new Scanner(new StringReader("foo\nadd 牛乳を買う\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);
        File file = tempDir.resolve("todo.txt").toFile();

        Main.run(scanner, out, file);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
    }

    @Test
    void addListDoneSequenceWorksCorrectly() {
        // add→list→done→listという一連のコマンドで、1回目のlistでは未完了([ ])、
        // done後の2回目のlistでは完了済み([x])と表示が変わることを確認する。
        Scanner scanner = new Scanner(new StringReader("add 牛乳を買う\nlist\ndone 0\nlist\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);
        File file = tempDir.resolve("todo.txt").toFile();

        Main.run(scanner, out, file);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("0: [ ] 牛乳を買う"));
        assertTrue(output.contains("0: [x] 牛乳を買う"));
    }
}
