package com.javalab.httpclient;

/**
 * HTTP通信・JSON解析に関する失敗をまとめて表す非チェック例外。
 * ネットワークエラー・異常なステータスコード・JSON形式不正のいずれも
 * この例外に統一し、呼び出し側がcatch節を分岐させずに済むようにする。
 */
public class HttpClientException extends RuntimeException {

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
