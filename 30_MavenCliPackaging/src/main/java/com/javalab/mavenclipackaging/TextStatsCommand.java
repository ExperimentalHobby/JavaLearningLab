package com.javalab.mavenclipackaging;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * テキストファイルの行数・単語数・文字数を表示するCLIコマンド。
 * 実行可能fat jarとして配布し、REPLではなくOSシェルから直接呼び出す形式を体験するための題材。
 */
@Command(name = "textstat", mixinStandardHelpOptions = true, versionProvider = AppVersionProvider.class)
public class TextStatsCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "統計を計算する対象のテキストファイル")
    private Path file;

    @Option(names = {"-f", "--format"}, description = "出力形式(text または json)")
    private String format = "text";

    @Spec
    private CommandSpec spec;

    private final TextStatisticsCalculator calculator = new TextStatisticsCalculator();

    @Override
    public Integer call() throws Exception {
        if (!Files.exists(file)) {
            spec.commandLine().getErr().println("ファイルが見つかりません: " + file);
            return 1;
        }
        String content = Files.readString(file, StandardCharsets.UTF_8);
        TextStatistics stats = calculator.calculate(content);
        spec.commandLine().getOut().println(format(stats));
        return 0;
    }

    private String format(TextStatistics stats) {
        if ("json".equals(format)) {
            return "{\"lines\":" + stats.lines() + ",\"words\":" + stats.words() + ",\"chars\":" + stats.chars() + "}";
        }
        return "lines: " + stats.lines() + ", words: " + stats.words() + ", chars: " + stats.chars();
    }
}
