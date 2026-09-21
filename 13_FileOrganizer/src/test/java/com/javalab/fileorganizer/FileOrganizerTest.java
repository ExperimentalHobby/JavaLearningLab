package com.javalab.fileorganizer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        OrganizeResult result = FileOrganizer.organize(tempDir);

        assertTrue(Files.exists(tempDir.resolve("images").resolve("photo.jpg")));
        assertFalse(Files.exists(tempDir.resolve("photo.jpg")));
        assertEquals(1, result.movedFiles().size());
        assertTrue(result.failures().isEmpty());
    }

    @Test
    void organizeRenamesInsteadOfOverwritingWhenDestinationAlreadyExists() throws IOException {
        // 整理先(tempDir/images/photo.jpg)に既に同名ファイルが存在する場合、
        // 以前はStandardCopyOption.REPLACE_EXISTINGで警告なく上書きしてデータが失われていた。
        // "photo (2).jpg"のようにリネーム退避して両方のファイルが残ることを確認する。
        Files.createDirectories(tempDir.resolve("images"));
        Files.writeString(tempDir.resolve("images").resolve("photo.jpg"), "existing");
        Files.writeString(tempDir.resolve("photo.jpg"), "new");

        OrganizeResult result = FileOrganizer.organize(tempDir);

        assertEquals("existing", Files.readString(tempDir.resolve("images").resolve("photo.jpg")));
        assertEquals("new", Files.readString(tempDir.resolve("images").resolve("photo (2).jpg")));
        assertEquals(1, result.movedFiles().size());
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

        OrganizeResult result = FileOrganizer.organize(tempDir);

        assertTrue(result.movedFiles().isEmpty());
        assertTrue(Files.exists(subDir));
    }

    @Test
    void organizeRecordsFailureWhenCategoryNameCollidesWithExistingRegularFile() throws IOException {
        // sourceDir直下にカテゴリ名と同じ名前の通常ファイル(拡張子なし)があると、
        // 以前はFiles.createDirectoriesが例外を投げてorganize全体が失敗していた。
        // 該当ファイルの移動は失敗として記録し、処理全体は継続することを確認する。
        // "images"ファイル自身も拡張子なしのため分類先は"others"になるが、"others"も
        // 通常ファイルでブロックしておくことで、処理順序に関わらず"images"の妨害が
        // 自己解消されない(決定的な)テストにしている。
        Files.createFile(tempDir.resolve("images")); // ディレクトリではなく通常ファイル
        Files.createFile(tempDir.resolve("others")); // "images"自身の退避先も塞いでおく
        Files.createFile(tempDir.resolve("photo.jpg"));
        Files.createFile(tempDir.resolve("notes.txt"));

        OrganizeResult result = FileOrganizer.organize(tempDir);

        assertEquals(1, result.movedFiles().size());
        assertTrue(Files.exists(tempDir.resolve("text").resolve("notes.txt")));
        // "images"ファイル自身(拡張子なしのため分類先は"others"だが自らブロック)+
        // "others"ファイル自身(同じ理由で自己ブロック)+ photo.jpg の3件が失敗する。
        assertEquals(3, result.failures().size());
        assertTrue(Files.exists(tempDir.resolve("photo.jpg"))); // 移動できず元の場所に残る
    }

    @Test
    void organizeContinuesProcessingRemainingFilesAfterOneFailure() throws IOException {
        // 1ファイルの移動失敗で処理全体を中断せず、残りのファイルを処理し続けることを確認する
        // (失敗の可視化のため、戻り値のfailures()に理由が記録される)。
        Files.createFile(tempDir.resolve("images"));
        Files.createFile(tempDir.resolve("others"));
        Files.createFile(tempDir.resolve("a.jpg"));
        Files.createFile(tempDir.resolve("b.txt"));
        Files.createFile(tempDir.resolve("c.pdf"));

        OrganizeResult result = FileOrganizer.organize(tempDir);

        assertEquals(2, result.movedFiles().size()); // b.txt, c.pdf
        assertEquals(3, result.failures().size()); // "images"自身, "others"自身, a.jpg
    }

    @Test
    void dryRunDoesNotMoveAnyFiles() throws IOException {
        // ドライランは実際にはファイルを動かさず、結果だけ確認できることを確認する。
        Files.createFile(tempDir.resolve("photo.jpg"));

        OrganizeResult result = FileOrganizer.organize(tempDir, true);

        assertTrue(Files.exists(tempDir.resolve("photo.jpg")));
        assertFalse(Files.exists(tempDir.resolve("images").resolve("photo.jpg")));
        assertEquals(1, result.movedFiles().size());
        assertEquals(tempDir.resolve("images").resolve("photo.jpg"), result.movedFiles().get(0));
    }
}
