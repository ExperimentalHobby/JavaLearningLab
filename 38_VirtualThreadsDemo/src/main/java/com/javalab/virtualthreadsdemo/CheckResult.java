package com.javalab.virtualthreadsdemo;

/** 1つのURLへの疎通確認結果。 */
public record CheckResult(String url, int statusCode) {
}
