package com.javalab.httpclient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 指定URLへHTTP通信を行い、JSONレスポンスを取得・解析するクライアント。
 * 通信・解析の失敗は{@link HttpClientException}に統一して呼び出し元へ伝える。
 */
public class JsonHttpFetcher {

    // 応答が返らないサーバーに接続後いつまでも待たされないよう、リクエストごとのタイムアウトを設定する。
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    // 接続不能なホスト・ポートに対して無期限に待たされないよう、接続タイムアウトを明示的に設定する。
    // リダイレクト(3xx)は自動的に追従する。
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    // 実在のAPIはUserに無いフィールドを含むことがほとんどのため、未知プロパティで
    // 解析エラーにならないようFAIL_ON_UNKNOWN_PROPERTIESを無効化する。
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * 指定URLへHTTP GETを行い、レスポンスボディを文字列として返す。
     * @param url 取得先URL
     * @return レスポンスボディ(JSON文字列)
     * @throws HttpClientException URLが不正な場合、通信に失敗した場合、またはステータスコードが2xx以外の場合
     */
    public String fetchJson(String url) {
        return fetchJson(url, Map.of());
    }

    /**
     * {@link #fetchJson(String)}と同様だが、リクエストヘッダを指定できる。
     * @param url 取得先URL
     * @param headers 付与するリクエストヘッダ(ヘッダ名→値)
     * @return レスポンスボディ(JSON文字列)
     * @throws HttpClientException URLが不正な場合、通信に失敗した場合、またはステータスコードが2xx以外の場合
     */
    public String fetchJson(String url, Map<String, String> headers) {
        HttpRequest.Builder builder = newRequestBuilder(url).GET();
        headers.forEach(builder::header);
        return send(builder.build());
    }

    /**
     * 指定URLへHTTP POSTを行い、レスポンスボディを文字列として返す。
     * リクエストボディはJSON文字列として扱い、{@code Content-Type: application/json}を付与する。
     * @param url 送信先URL
     * @param jsonBody リクエストボディ(JSON文字列)
     * @return レスポンスボディ(JSON文字列)
     * @throws HttpClientException URLが不正な場合、通信に失敗した場合、またはステータスコードが2xx以外の場合
     */
    public String postJson(String url, String jsonBody) {
        HttpRequest request = newRequestBuilder(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();
        return send(request);
    }

    /**
     * 指定URLからJSONを取得し、{@link User}レコードへ変換する。
     * @param url 取得先URL
     * @return 解析済みの{@link User}
     * @throws HttpClientException URLが不正な場合、通信に失敗した場合、またはJSON形式が不正な場合
     */
    public User fetchUser(String url) {
        String json = fetchJson(url);
        try {
            return objectMapper.readValue(json, User.class);
        } catch (IOException e) {
            throw new HttpClientException("JSONの解析に失敗しました: " + e.getMessage(), e);
        }
    }

    /**
     * ベースURLにクエリパラメータを付与したURL文字列を組み立てる。
     * キー・値はいずれも{@link URLEncoder}でURLエンコードする。
     * @param baseUrl ベースとなるURL(既に{@code ?}を含んでいてもよい)
     * @param queryParams クエリパラメータ(キー→値)。順序を保証したい場合は{@code LinkedHashMap}等を渡す
     * @return クエリパラメータを付与したURL文字列(queryParamsが空の場合はbaseUrlそのまま)
     */
    public static String buildUrl(String baseUrl, Map<String, String> queryParams) {
        if (queryParams.isEmpty()) {
            return baseUrl;
        }
        String query = queryParams.entrySet().stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(Collectors.joining("&"));
        return baseUrl + (baseUrl.contains("?") ? "&" : "?") + query;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * URLを検証しつつ、タイムアウトを設定済みの{@link HttpRequest.Builder}を作る。
     * {@code URI.create}・{@code HttpRequest.newBuilder}が投げる{@link IllegalArgumentException}
     * (不正な文字を含むURL、未対応スキーム等)を{@link HttpClientException}に変換する。
     */
    private HttpRequest.Builder newRequestBuilder(String url) {
        try {
            return HttpRequest.newBuilder(URI.create(url)).timeout(REQUEST_TIMEOUT);
        } catch (IllegalArgumentException e) {
            throw new HttpClientException("不正なURLです: " + url, e);
        }
    }

    private String send(HttpRequest request) {
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new HttpClientException("HTTP通信に失敗しました: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpClientException("HTTP通信が中断されました", e);
        }
        int status = response.statusCode();
        // 200だけでなく2xx全体(201 Created, 204 No Content等)を成功として扱う。
        if (status < 200 || status >= 300) {
            throw new HttpClientException("HTTPステータスが異常です: " + status);
        }
        return response.body();
    }
}
