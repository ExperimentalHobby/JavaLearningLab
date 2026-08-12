package com.javalab.httpclient;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * 指定URLへHTTP GETを行い、JSONレスポンスを取得・解析するクライアント。
 * 通信・解析の失敗は{@link HttpClientException}に統一して呼び出し元へ伝える。
 */
public class JsonHttpFetcher {

    // 接続不能なホスト・ポートに対して無期限に待たされないよう、接続タイムアウトを明示的に設定する。
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 指定URLへHTTP GETを行い、レスポンスボディを文字列として返す。
     * @param url 取得先URL
     * @return レスポンスボディ(JSON文字列)
     * @throws HttpClientException 通信に失敗した場合、またはステータスコードが200以外の場合
     */
    public String fetchJson(String url) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new HttpClientException("HTTP通信に失敗しました: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpClientException("HTTP通信が中断されました", e);
        }
        if (response.statusCode() != 200) {
            throw new HttpClientException("HTTPステータスが異常です: " + response.statusCode());
        }
        return response.body();
    }

    /**
     * 指定URLからJSONを取得し、{@link User}レコードへ変換する。
     * @param url 取得先URL
     * @return 解析済みの{@link User}
     * @throws HttpClientException 通信に失敗した場合、またはJSON形式が不正な場合
     */
    public User fetchUser(String url) {
        String json = fetchJson(url);
        try {
            return objectMapper.readValue(json, User.class);
        } catch (IOException e) {
            throw new HttpClientException("JSONの解析に失敗しました: " + e.getMessage(), e);
        }
    }
}
