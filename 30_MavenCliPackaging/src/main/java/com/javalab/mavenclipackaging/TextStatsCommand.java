package com.javalab.mavenclipackaging;

import picocli.CommandLine.Command;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;
import picocli.CommandLine.TypeConversionException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * テキストファイルの行数・単語数・文字数を表示するCLIコマンド。
 * 実行可能fat jarとして配布し、REPLではなくOSシェルから直接呼び出す形式を体験するための題材。
 */
@Command(name = "textstat", mixinStandardHelpOptions = true, versionProvider = AppVersionProvider.class)
public class TextStatsCommand implements Callable<Integer> {

    /** 出力形式。専用のコンバータ({@link OutputFormatConverter})で検証し、未対応の値は明示的にエラーにする。 */
    public enum OutputFormat {
        TEXT, JSON
    }

    /**
     * {@code --format}の値を{@link OutputFormat}へ変換する。大文字小文字を区別せずに受け付け、
     * 未対応の値は{@link TypeConversionException}(picocliが分かりやすいエラーメッセージと
     * 終了コード2に変換してくれる)を送出する。
     */
    static class OutputFormatConverter implements ITypeConverter<OutputFormat> {
        @Override
        public OutputFormat convert(String value) {
            try {
                return OutputFormat.valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new TypeConversionException(
                        "不正な出力形式です: " + value + "(text または json を指定してください)");
            }
        }
    }

    @Parameters(index = "0..*", description = "統計を計算する対象のテキストファイル(省略時は標準入力から読み込む)")
    private List<Path> files = List.of();

    @Option(names = {"-f", "--format"}, description = "出力形式(text または json)", converter = OutputFormatConverter.class)
    private OutputFormat format = OutputFormat.TEXT;

    @Spec
    private CommandSpec spec;

    private final TextStatisticsCalculator calculator = new TextStatisticsCalculator();

    @Override
    public Integer call() throws Exception {
        if (files.isEmpty()) {
            return callForStandardInput();
        }
        if (files.size() == 1) {
            return callForSingleFile(files.get(0));
        }
        return callForMultipleFiles();
    }

    private Integer callForStandardInput() throws Exception {
        String content = new String(System.in.readAllBytes(), StandardCharsets.UTF_8);
        TextStatistics stats = calculator.calculate(content);
        spec.commandLine().getOut().println(format(stats));
        return 0;
    }

    private Integer callForSingleFile(Path file) throws IOException {
        PrintWriter err = spec.commandLine().getErr();
        if (!Files.exists(file)) {
            err.println("ファイルが見つかりません: " + file);
            return 1;
        }
        String content;
        try {
            content = Files.readString(file, StandardCharsets.UTF_8);
        } catch (MalformedInputException e) {
            // UTF-8以外のファイルを読むとFiles.readStringがMalformedInputExceptionを投げ、
            // picocliがスタックトレースを出力してしまう。Files.existsと同様に、
            // 意味のあるエラーメッセージ+終了コードで返す。
            err.println("ファイルの文字コードが不正です(UTF-8以外で保存されている可能性があります): " + file);
            return 1;
        }
        TextStatistics stats = calculator.calculate(content);
        spec.commandLine().getOut().println(format(stats));
        return 0;
    }

    /**
     * 複数ファイルを一括指定された場合、ファイルごとの結果に加え合計も表示する。
     */
    private Integer callForMultipleFiles() throws IOException {
        PrintWriter out = spec.commandLine().getOut();
        PrintWriter err = spec.commandLine().getErr();
        int totalLines = 0;
        int totalWords = 0;
        int totalChars = 0;
        for (Path file : files) {
            if (!Files.exists(file)) {
                err.println("ファイルが見つかりません: " + file);
                return 1;
            }
            String content;
            try {
                content = Files.readString(file, StandardCharsets.UTF_8);
            } catch (MalformedInputException e) {
                err.println("ファイルの文字コードが不正です(UTF-8以外で保存されている可能性があります): " + file);
                return 1;
            }
            TextStatistics stats = calculator.calculate(content);
            out.println(file + ": " + format(stats));
            totalLines += stats.lines();
            totalWords += stats.words();
            totalChars += stats.chars();
        }
        out.println("total: " + format(new TextStatistics(totalLines, totalWords, totalChars)));
        return 0;
    }

    private String format(TextStatistics stats) {
        if (format == OutputFormat.JSON) {
            return "{\"lines\":" + stats.lines() + ",\"words\":" + stats.words() + ",\"chars\":" + stats.chars() + "}";
        }
        return "lines: " + stats.lines() + ", words: " + stats.words() + ", chars: " + stats.chars();
    }
}
