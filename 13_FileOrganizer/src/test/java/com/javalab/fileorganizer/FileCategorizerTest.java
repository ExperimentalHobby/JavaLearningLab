package com.javalab.fileorganizer;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link FileCategorizer#categoryOf(Path)} の拡張子ベースの分類ロジックを検証するテスト。
 * ファイルは実際に作成せず、Pathオブジェクトのファイル名だけで判定できることも確認できる
 * (ファイルシステムへのアクセスを伴わない純粋なロジックであるため)。
 */
class FileCategorizerTest {

    @Test
    void categorizesImageExtension() {
        assertEquals("images", FileCategorizer.categoryOf(Path.of("photo.jpg")));
    }

    @Test
    void categorizesTextExtension() {
        assertEquals("text", FileCategorizer.categoryOf(Path.of("notes.txt")));
    }

    @Test
    void categorizesDocumentExtension() {
        assertEquals("documents", FileCategorizer.categoryOf(Path.of("report.pdf")));
    }

    @Test
    void categorizesUnknownExtensionAsOthers() {
        // ".xyz"はどのカテゴリにも登録されていない拡張子のため、デフォルトの"others"になる。
        assertEquals("others", FileCategorizer.categoryOf(Path.of("data.xyz")));
    }

    @Test
    void categorizesFileWithoutExtensionAsOthers() {
        // 拡張子が無いファイル名(ドットを含まない)は、extensionOf()が空文字を返し、
        // 結果的に"others"に分類されることを確認する。
        assertEquals("others", FileCategorizer.categoryOf(Path.of("noextension")));
    }
}
