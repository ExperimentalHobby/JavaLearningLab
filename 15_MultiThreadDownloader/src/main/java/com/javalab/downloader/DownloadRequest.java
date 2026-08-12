package com.javalab.downloader;

import java.net.URI;
import java.nio.file.Path;

/**
 * ダウンロード対象のURLと保存先パスの組を表す不変な値オブジェクト。
 * @param url ダウンロード元URL
 * @param destination 保存先ファイルパス
 */
public record DownloadRequest(URI url, Path destination) {
}
