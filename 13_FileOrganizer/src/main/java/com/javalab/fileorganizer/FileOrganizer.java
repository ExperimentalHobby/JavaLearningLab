package com.javalab.fileorganizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * ディレクトリ直下のファイルを拡張子に応じたサブフォルダへ整理する。
 * すべてjava.nio.file.Path/Filesで実装し、レガシーなjava.io.Fileは使用しない。
 */
public class FileOrganizer {

    /**
     * sourceDir直下のファイル(サブディレクトリは対象外)を{@link FileCategorizer}で分類し、
     * カテゴリ名のサブフォルダを作成した上で移動する。
     * @param sourceDir 整理対象のディレクトリ
     * @return 移動後の各ファイルのパス一覧
     * @throws IOException ファイル操作(一覧取得・ディレクトリ作成・移動)に失敗した場合
     * @throws FileOrganizerException sourceDirが存在しない、またはディレクトリでない場合
     */
    public static List<Path> organize(Path sourceDir) throws IOException {
        if (!Files.exists(sourceDir)) {
            throw new FileOrganizerException("指定されたパスが存在しません: " + sourceDir);
        }
        if (!Files.isDirectory(sourceDir)) {
            throw new FileOrganizerException("指定されたパスはディレクトリではありません: " + sourceDir);
        }
        List<Path> movedFiles = new ArrayList<>();
        // Files.listは直下のエントリのみを返す(サブディレクトリの中身までは再帰しない)ため、
        // 既存のサブフォルダを誤って移動対象にしてしまう心配がない。
        try (Stream<Path> entries = Files.list(sourceDir)) {
            for (Path entry : entries.toList()) {
                if (Files.isRegularFile(entry)) {
                    String category = FileCategorizer.categoryOf(entry);
                    Path categoryDir = sourceDir.resolve(category);
                    Files.createDirectories(categoryDir);
                    Path destination = categoryDir.resolve(entry.getFileName());
                    Files.move(entry, destination, StandardCopyOption.REPLACE_EXISTING);
                    movedFiles.add(destination);
                }
            }
        }
        return movedFiles;
    }
}
