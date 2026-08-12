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

class FileOrganizerTest {

    @TempDir
    Path tempDir;

    @Test
    void organizeMovesFilesIntoCategorySubfolders() throws IOException {
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
        Path file = Files.createFile(tempDir.resolve("plainfile.txt"));

        assertThrows(FileOrganizerException.class, () -> FileOrganizer.organize(file));
    }

    @Test
    void organizeDoesNotMoveExistingSubdirectories() throws IOException {
        Path subDir = Files.createDirectory(tempDir.resolve("existing-subdir"));

        List<Path> moved = FileOrganizer.organize(tempDir);

        assertTrue(moved.isEmpty());
        assertTrue(Files.exists(subDir));
    }
}
