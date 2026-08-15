package com.javalab.fileorganizer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link FileOrganizer#organize(Path)} のファイル移動ロジックを検証するテスト。
 * {@code @TempDir}が提供する実際の一時ディレクトリに対してファイルを作成・移動しており、
 * モックではなく本物のファイルシステム操作(java.nio.file)を通して確認している。
 */
class FileOrganizerTest {

    @TempDir
    Path tempDir;

    @Test
    void organizeMovesFilesIntoCategorySubfolders() throws IOException {
        // "photo.jpg"がimagesカテゴリと判定され、tempDir/images/photo.jpgへ実際に移動し、
        // 元の場所には残っていないことを確認する。
        Files.createFile(tempDir.resolve("photo.jpg"));

        FileOrganizer.organize(tempDir);

        assertTrue(Files.exists(tempDir.resolve("images").resolve("photo.jpg")));
        assertFalse(Files.exists(tempDir.resolve("photo.jpg")));
    }

    @Test
    void organizeThrowsExceptionForNonExistentDirectory() {
        Path missing = tempDir.resolve("does-not-exist");

        assertThrows(FileOrganizerException.class, () -> FileOrganizer.organize(missing));
    }

    @Test
    void organizeThrowsExceptionWhenPathIsNotADirectory() throws IOException {
        // ディレクトリではなく通常のファイルを渡した場合も、organize対象として不正であるため
        // FileOrganizerExceptionになることを確認する。
        Path file = Files.createFile(tempDir.resolve("plainfile.txt"));

        assertThrows(FileOrganizerException.class, () -> FileOrganizer.organize(file));
    }

    @Test
    void organizeDoesNotMoveExistingSubdirectories() throws IOException {
        // organize()はFiles.isRegularFile()で通常ファイルのみを対象にしており、
        // 既存のサブディレクトリ自体は移動対象にもファイルの中身の走査対象にもならないことを確認する。
        Path subDir = Files.createDirectory(tempDir.resolve("existing-subdir"));

        List<Path> moved = FileOrganizer.organize(tempDir);

        assertTrue(moved.isEmpty());
        assertTrue(Files.exists(subDir));
    }
}
