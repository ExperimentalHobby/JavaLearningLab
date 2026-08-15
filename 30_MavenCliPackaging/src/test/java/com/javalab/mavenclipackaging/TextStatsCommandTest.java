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
        assertTrue(result.contains("2"));
        assertTrue(result.contains("5"));
        assertTrue(result.contains("23"));
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
        assertTrue(result.contains("\"lines\":2"));
        assertTrue(result.contains("\"words\":5"));
        assertTrue(result.contains("\"chars\":23"));
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
}
