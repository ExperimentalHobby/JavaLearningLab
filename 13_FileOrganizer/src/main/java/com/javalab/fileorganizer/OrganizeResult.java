package com.javalab.fileorganizer;

import java.nio.file.Path;
import java.util.List;

/**
 * {@link FileOrganizer#organize(Path)} の実行結果。
 * 1ファイルの移動失敗で処理全体を中断せず、成功分・失敗分の両方を呼び出し側が確認できるようにする
 * (失敗しても、そこまでに移動できたファイルがどれかを追跡できないという問題への対応)。
 * @param movedFiles 移動できたファイルの移動後パス一覧(ドライランの場合は移動予定のパス一覧)
 * @param failures 移動に失敗したファイルとその理由一覧
 */
public record OrganizeResult(List<Path> movedFiles, List<String> failures) {
}
