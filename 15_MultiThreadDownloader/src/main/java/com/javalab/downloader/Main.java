package com.javalab.downloader;

import java.io.PrintStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * マルチスレッドダウンローダーのエントリーポイント。
 * 標準入力からコマンドを読み取り、複数URLを並行ダウンロードする対話型REPLを提供する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    /**
     * REPLループ本体。テストから{@link Scanner}/{@link PrintStream}を差し替えられるよう分離している。
     * コマンド: {@code download <URL1,URL2,...> <保存先ディレクトリ>}(並行ダウンロードし結果表示) / {@code exit}。
     * ダウンロード失敗はループを止めず、失敗した1件のみエラー表示して次のコマンド入力を継続する。
     * @param scanner コマンド読み取り元
     * @param out 結果出力先
     */
    static void run(Scanner scanner, PrintStream out) {
        MultiThreadDownloader downloader = new MultiThreadDownloader();
        out.println("マルチスレッドダウンローダー。コマンド: download <URL1,URL2,...> <保存先ディレクトリ> / exit");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+", 3);
            String command = parts[0];
            if (command.equals("exit")) {
                break;
            } else if (command.equals("download") && parts.length == 3) {
                handleDownload(downloader, parts[1], parts[2], out);
            } else {
                out.println("不明なコマンドです: " + line);
            }
        }
    }

    private static void handleDownload(MultiThreadDownloader downloader, String urlsCsv, String destDir, PrintStream out) {
        String[] urls = urlsCsv.split(",");
        List<DownloadRequest> requests = new ArrayList<>();
        Set<String> usedFileNames = new HashSet<>();
        for (int i = 0; i < urls.length; i++) {
            URI uri;
            try {
                uri = URI.create(urls[i]);
            } catch (IllegalArgumentException e) {
                // 不正なURLはこの1件だけエラー表示し、他のURLのダウンロードは継続する。
                out.println("失敗: " + urls[i] + " (不正なURLです: " + e.getMessage() + ")");
                continue;
            }
            // 異なるURLでもパスの末尾が同じだと保存ファイル名が衝突し、後勝ちで上書きされてしまうため、
            // 既に使われているファイル名なら"name (2).ext"のように連番を付けて重複を避ける。
            String fileName = uniqueFileName(fileNameOf(uri, i), usedFileNames);
            requests.add(new DownloadRequest(uri, Path.of(destDir, fileName)));
        }
        if (requests.isEmpty()) {
            return;
        }

        // 進捗(件数・完了率)を表示するため、完了するたびに呼ばれるコールバックを渡す。
        int total = requests.size();
        AtomicInteger completedCount = new AtomicInteger();
        List<DownloadResult> results = downloader.downloadAll(requests, requests.size(), result -> {
            int completed = completedCount.incrementAndGet();
            out.printf("進捗: %d/%d (%.0f%%) 完了 - %s%n", completed, total, completed * 100.0 / total, result.request().url());
        });

        for (DownloadResult result : results) {
            if (result.success()) {
                out.println("成功: " + result.request().url() + " -> " + result.request().destination()
                        + " (" + result.bytesWritten() + "バイト)");
            } else {
                out.println("失敗: " + result.request().url() + " (" + result.errorMessage() + ")");
            }
        }
    }

    private static String uniqueFileName(String fileName, Set<String> usedFileNames) {
        if (usedFileNames.add(fileName)) {
            return fileName;
        }
        String baseName = fileName;
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            baseName = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        }
        for (int suffix = 2; ; suffix++) {
            String candidate = baseName + " (" + suffix + ")" + extension;
            if (usedFileNames.add(candidate)) {
                return candidate;
            }
        }
    }

    private static String fileNameOf(URI uri, int index) {
        String path = uri.getPath();
        if (path == null || path.isEmpty() || path.equals("/")) {
            return "file" + index;
        }
        String name = path.substring(path.lastIndexOf('/') + 1);
        return name.isEmpty() ? "file" + index : name;
    }
}
