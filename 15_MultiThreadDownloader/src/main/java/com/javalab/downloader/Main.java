package com.javalab.downloader;

import java.io.PrintStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        for (int i = 0; i < urls.length; i++) {
            URI uri = URI.create(urls[i]);
            requests.add(new DownloadRequest(uri, Path.of(destDir, fileNameOf(uri, i))));
        }

        List<DownloadResult> results = downloader.downloadAll(requests, requests.size());
        for (DownloadResult result : results) {
            if (result.success()) {
                out.println("成功: " + result.request().url() + " -> " + result.request().destination()
                        + " (" + result.bytesWritten() + "バイト)");
            } else {
                out.println("失敗: " + result.request().url() + " (" + result.errorMessage() + ")");
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
