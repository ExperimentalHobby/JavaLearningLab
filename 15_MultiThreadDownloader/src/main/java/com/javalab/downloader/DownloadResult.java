package com.javalab.downloader;

/**
 * 1件のダウンロード結果を表す不変な値オブジェクト。
 * 失敗時も例外を投げず、この結果として記録することで他のダウンロードへの影響を防ぐ。
 * @param request 対応するダウンロード指示
 * @param success ダウンロードが成功したか
 * @param bytesWritten 書き込んだバイト数(失敗時は0)
 * @param errorMessage 失敗時のエラーメッセージ(成功時はnull)
 */
public record DownloadResult(DownloadRequest request, boolean success, long bytesWritten, String errorMessage) {
}
