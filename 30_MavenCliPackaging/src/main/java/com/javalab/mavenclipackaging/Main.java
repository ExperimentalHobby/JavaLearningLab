package com.javalab.mavenclipackaging;

import picocli.CommandLine;

/**
 * テキスト統計CLIツールのエントリーポイント。
 * REPLではなく通常のコマンドライン引数を受け取り、実行可能fat jarとして配布する形式を体験する。
 */
public class Main {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new TextStatsCommand()).execute(args);
        System.exit(exitCode);
    }
}
