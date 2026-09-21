package com.javalab.virtualthreadsdemo;

/**
 * 1つのURLへの疎通確認結果。
 * 失敗時も例外を投げず、この結果として記録することで他のURLへの影響を防ぐ
 * ({@code 15_MultiThreadDownloader}の{@code DownloadResult}と同じ設計パターン)。
 * @param url 対象URL
 * @param success 疎通に成功したか
 * @param statusCode HTTPステータスコード(失敗時は-1)
 * @param errorMessage 失敗時のエラーメッセージ(成功時はnull)
 */
public record CheckResult(String url, boolean success, int statusCode, String errorMessage) {

    public static CheckResult success(String url, int statusCode) {
        return new CheckResult(url, true, statusCode, null);
    }

    public static CheckResult failure(String url, String errorMessage) {
        return new CheckResult(url, false, -1, errorMessage);
    }
}
