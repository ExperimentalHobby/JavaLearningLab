package com.javalab.fileorganizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
     * @return 実行結果(移動できたファイル一覧・失敗したファイルとその理由一覧)
     * @throws IOException sourceDir直下の一覧取得に失敗した場合
     * @throws FileOrganizerException sourceDirが存在しない、またはディレクトリでない場合
     */
    public static OrganizeResult organize(Path sourceDir) throws IOException {
        return organize(sourceDir, false);
    }

    /**
     * {@link #organize(Path)}と同様だが、{@code dryRun}が{@code true}の場合は実際には
     * ディレクトリ作成・ファイル移動を行わず、移動予定の対応関係のみを結果として返す
     * (実行前に影響範囲を確認したい場合に使う)。
     * <p>
     * 1ファイルの移動に失敗しても処理全体を中断せず、残りのファイルの処理を継続する。
     * そのため、戻り値の{@link OrganizeResult#movedFiles()}が空でなくても
     * {@link OrganizeResult#failures()}も同時に空でない(部分的にしか完了していない)ことがある。
     * @param sourceDir 整理対象のディレクトリ
     * @param dryRun trueの場合、実際のファイル操作を行わずシミュレーションのみ行う
     * @return 実行結果(dryRun時は「移動予定」の一覧)
     * @throws IOException sourceDir直下の一覧取得に失敗した場合
     * @throws FileOrganizerException sourceDirが存在しない、またはディレクトリでない場合
     */
    public static OrganizeResult organize(Path sourceDir, boolean dryRun) throws IOException {
        if (!Files.exists(sourceDir)) {
            throw new FileOrganizerException("指定されたパスが存在しません: " + sourceDir);
        }
        if (!Files.isDirectory(sourceDir)) {
            throw new FileOrganizerException("指定されたパスはディレクトリではありません: " + sourceDir);
        }
        List<Path> movedFiles = new ArrayList<>();
        List<String> failures = new ArrayList<>();
        // Files.listは直下のエントリのみを返す(サブディレクトリの中身までは再帰しない)ため、
        // 既存のサブフォルダを誤って移動対象にしてしまう心配がない。
        try (Stream<Path> entries = Files.list(sourceDir)) {
            for (Path entry : entries.toList()) {
                if (Files.isRegularFile(entry)) {
                    moveOne(entry, sourceDir, dryRun, movedFiles, failures);
                }
            }
        }
        return new OrganizeResult(movedFiles, failures);
    }

    private static void moveOne(
            Path entry, Path sourceDir, boolean dryRun, List<Path> movedFiles, List<String> failures) {
        String category = FileCategorizer.categoryOf(entry);
        Path categoryDir = sourceDir.resolve(category);
        if (Files.exists(categoryDir) && !Files.isDirectory(categoryDir)) {
            // カテゴリ名と同じ名前の通常ファイルが既に存在すると、createDirectoriesが失敗する。
            // このファイルの移動だけ失敗として記録し、他のファイルの処理は継続する。
            failures.add(entry + ": カテゴリフォルダ(" + categoryDir + ")と同名の通常ファイルが存在するため移動できません");
            return;
        }
        try {
            Path destination = resolveDestination(categoryDir, entry.getFileName().toString(), dryRun);
            if (!dryRun) {
                Files.createDirectories(categoryDir);
                Files.move(entry, destination);
            }
            movedFiles.add(destination);
        } catch (IOException e) {
            failures.add(entry + ": " + e.getMessage());
        }
    }

    /**
     * 移動先のパスを決定する。{@code categoryDir}に同名ファイルが既にある場合は、
     * {@code StandardCopyOption.REPLACE_EXISTING}による無警告の上書きを避けるため、
     * {@code "name (2).ext"}のように末尾へ連番を付けたリネーム退避先を探す。
     * ドライラン時は実際にファイルが存在するかどうかのみで判定する(何も書き込まないため、
     * 同一ドライラン内で複数の同名ファイルがあっても連番の衝突までは検証しない)。
     */
    private static Path resolveDestination(Path categoryDir, String fileName, boolean dryRun) {
        Path candidate = categoryDir.resolve(fileName);
        if (!Files.exists(candidate)) {
            return candidate;
        }
        String baseName = fileName;
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            baseName = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        }
        for (int suffix = 2; ; suffix++) {
            candidate = categoryDir.resolve(baseName + " (" + suffix + ")" + extension);
            if (!Files.exists(candidate)) {
                return candidate;
            }
        }
    }
}
