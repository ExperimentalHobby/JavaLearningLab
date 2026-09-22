package com.javalab.mavenclipackaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link TextStatsCommand} のpicocliコマンド実装(ファイル統計表示・出力形式切替・
 * バージョン表示・異常終了コード)を検証するテスト。
 * 出力はpicocliの{@link CommandLine#setOut}で差し替えたPrintWriterを介して検証しており、
 * 標準出力への直接書き込みではなくpicocliの出力ストリーム経由で行うことでテスト容易性を高めている。
 */
class TextStatsCommandTest {

    @TempDir
    Path tempDir;

    @Test
    void executePrintsStatisticsForGivenFile() throws Exception {
        Path file = tempDir.resolve("sample.txt");
        Files.writeString(file, "Hello world\nJava is fun", StandardCharsets.UTF_8);
        CommandLine cmd = new CommandLine(new TextStatsCommand());
        StringWriter out = new StringWriter();
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute(file.toString());

        assertEquals(0, exitCode);
        String result = out.toString();
        // "Hello world\nJava is fun"は改行が1個(末尾に改行なし)。
        // wc -lと同じ「改行文字の出現回数」で数えるため行数は1になる。
        assertTrue(result.contains("lines: 1"));
        assertTrue(result.contains("words: 5"));
        assertTrue(result.contains("chars: 23"));
    }

    @Test
    void executePrintsJsonFormatWhenFormatOptionIsJson() throws Exception {
        Path file = tempDir.resolve("sample.txt");
        Files.writeString(file, "Hello world\nJava is fun", StandardCharsets.UTF_8);
        CommandLine cmd = new CommandLine(new TextStatsCommand());
        StringWriter out = new StringWriter();
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("--format", "json", file.toString());

        assertEquals(0, exitCode);
        String result = out.toString().trim();
        assertTrue(result.startsWith("{"));
        // "Hello world\nJava is fun"は改行が1個(末尾に改行なし)。
        // wc -lと同じ「改行文字の出現回数」で数えるため行数は1になる。
        assertTrue(result.contains("\"lines\":1"));
        assertTrue(result.contains("\"words\":5"));
        assertTrue(result.contains("\"chars\":23"));
    }

    @Test
    void executeReturnsNonZeroExitCodeForUnsupportedFormatValue() throws Exception {
        // --format xmlのような未対応の値が、黙ってtext形式にフォールバックされてしまっていた問題への対応。
        Path file = tempDir.resolve("sample.txt");
        Files.writeString(file, "Hello world\nJava is fun", StandardCharsets.UTF_8);
        CommandLine cmd = new CommandLine(new TextStatsCommand());
        cmd.setErr(new PrintWriter(new StringWriter()));

        int exitCode = cmd.execute("--format", "xml", file.toString());

        assertEquals(2, exitCode);
    }

    @Test
    void executeAcceptsFormatValueRegardlessOfCase() throws Exception {
        Path file = tempDir.resolve("sample.txt");
        Files.writeString(file, "Hello world\nJava is fun", StandardCharsets.UTF_8);
        CommandLine cmd = new CommandLine(new TextStatsCommand());
        StringWriter out = new StringWriter();
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("--format", "JSON", file.toString());

        assertEquals(0, exitCode);
        assertTrue(out.toString().trim().startsWith("{"));
    }

    @Test
    void executeReadsFromStandardInputWhenNoFileArgumentGiven() throws Exception {
        // 標準入力(パイプ)からの読み込みに未対応だった問題への対応。
        CommandLine cmd = new CommandLine(new TextStatsCommand());
        StringWriter out = new StringWriter();
        cmd.setOut(new PrintWriter(out));
        java.io.InputStream original = System.in;
        try {
            System.setIn(new java.io.ByteArrayInputStream(
                    "Hello world\nJava is fun".getBytes(StandardCharsets.UTF_8)));

            int exitCode = cmd.execute();

            assertEquals(0, exitCode);
            String result = out.toString();
            assertTrue(result.contains("lines: 1"));
            assertTrue(result.contains("words: 5"));
            assertTrue(result.contains("chars: 23"));
        } finally {
            System.setIn(original);
        }
    }

    @Test
    void executeShowsPerFileStatisticsAndTotalForMultipleFiles() throws Exception {
        // 複数ファイルの一括指定に未対応だった問題への対応。
        Path fileA = tempDir.resolve("a.txt");
        Path fileB = tempDir.resolve("b.txt");
        Files.writeString(fileA, "Hello world\nJava is fun", StandardCharsets.UTF_8);
        Files.writeString(fileB, "one two three", StandardCharsets.UTF_8);
        CommandLine cmd = new CommandLine(new TextStatsCommand());
        StringWriter out = new StringWriter();
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute(fileA.toString(), fileB.toString());

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains(fileA + ": lines: 1, words: 5, chars: 23"));
        assertTrue(result.contains(fileB + ": lines: 0, words: 3, chars: 13"));
        assertTrue(result.contains("total: lines: 1, words: 8, chars: 36"));
    }

    @Test
    void executeReturnsMeaningfulErrorForNonUtf8File() throws Exception {
        // UTF-8以外のファイルを読むとFiles.readStringがMalformedInputExceptionを投げ、
        // picocliがスタックトレースを出力してしまっていた問題への対応。
        Path file = tempDir.resolve("sjis.txt");
        // Shift_JISでエンコードすると、UTF-8としては不正なバイト列になる日本語を書き込む。
        Files.write(file, "テスト".getBytes(java.nio.charset.Charset.forName("Shift_JIS")));
        CommandLine cmd = new CommandLine(new TextStatsCommand());
        StringWriter err = new StringWriter();
        cmd.setErr(new PrintWriter(err));

        int exitCode = cmd.execute(file.toString());

        assertEquals(1, exitCode);
        assertTrue(err.toString().contains("文字コード"));
    }

    @Test
    void executeReturnsNonZeroExitCodeWhenFileDoesNotExist() {
        Path missingFile = tempDir.resolve("missing.txt");
        CommandLine cmd = new CommandLine(new TextStatsCommand());
        cmd.setErr(new PrintWriter(new StringWriter()));

        int exitCode = cmd.execute(missingFile.toString());

        assertEquals(1, exitCode);
    }

    @Test
    void executePrintsPomVersionForVersionOption() {
        CommandLine cmd = new CommandLine(new TextStatsCommand());
        StringWriter out = new StringWriter();
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("--version");

        assertEquals(0, exitCode);
        // src/main/resources/app.propertiesはpom.xmlのresource filteringにより
        // ${project.version}(1.0-SNAPSHOT)へ置換されてビルドされる。
        assertTrue(out.toString().contains("1.0-SNAPSHOT"));
    }

    @Test
    void executePrintsUsageForHelpOption() {
        CommandLine cmd = new CommandLine(new TextStatsCommand());
        StringWriter out = new StringWriter();
        cmd.setOut(new PrintWriter(out));
        // picocliのAnsi.AUTOは実行環境(MSYS/Git Bash等)の環境変数だけでANSI対応を判定し、
        // setOut()でStringWriterへリダイレクトしていても考慮しない。そのため環境によっては
        // "Usage: "の直後に装飾用のエスケープシーケンスが挿入され、"Usage: textstat"という
        // プレーンテキストの部分一致に失敗する(文字コードの問題ではない)。
        // テストでは常に一貫した結果を得るため明示的にANSIを無効化する。
        cmd.setColorScheme(new CommandLine.Help.ColorScheme.Builder(CommandLine.Help.Ansi.OFF).build());

        int exitCode = cmd.execute("--help");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("Usage: textstat"));
        assertTrue(result.contains("--format"));
        assertTrue(result.contains("出力形式"));
    }
}
