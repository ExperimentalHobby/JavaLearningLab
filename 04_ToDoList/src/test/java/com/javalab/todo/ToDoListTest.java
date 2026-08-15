package com.javalab.todo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ToDoList} の追加・完了・削除・ファイル保存/読込を検証するテスト。
 * ファイルI/Oを伴うテストは{@code @TempDir}が提供する実際の一時ディレクトリを使い、
 * モックではなく本物のファイルシステムに対して読み書きすることで結合的に確認している。
 */
class ToDoListTest {

    private final ToDoList toDoList = new ToDoList();

    @TempDir
    Path tempDir;

    @Test
    void addAppendsNewUnfinishedTask() {
        toDoList.add("牛乳を買う");

        assertEquals(1, toDoList.getTasks().size());
        assertEquals("牛乳を買う", toDoList.getTasks().get(0).getDescription());
        assertFalse(toDoList.getTasks().get(0).isDone());
    }

    @Test
    void multipleAddsAreStoredInInsertionOrder() {
        // ArrayListによる管理のため、追加した順序がそのままインデックス順に保たれることを確認する。
        toDoList.add("牛乳を買う");
        toDoList.add("掃除する");

        assertEquals(2, toDoList.getTasks().size());
        assertEquals("牛乳を買う", toDoList.getTasks().get(0).getDescription());
        assertEquals("掃除する", toDoList.getTasks().get(1).getDescription());
    }

    @Test
    void completeMarksTaskAtIndexAsDone() {
        toDoList.add("牛乳を買う");

        toDoList.complete(0);

        assertTrue(toDoList.getTasks().get(0).isDone());
    }

    @Test
    void completeThrowsExceptionForInvalidIndex() {
        // タスクが1件も無い状態でインデックス0を指定するのは範囲外アクセスであり、
        // validateIndex()がToDoListExceptionをスローすることを確認する。
        assertThrows(ToDoListException.class, () -> toDoList.complete(0));
    }

    @Test
    void removeDeletesTaskAtIndexAndShrinksSize() {
        toDoList.add("牛乳を買う");
        toDoList.add("掃除する");

        toDoList.remove(0);

        // 先頭を削除すると後続の要素が繰り上がるため、残った要素が新しいインデックス0になる。
        assertEquals(1, toDoList.getTasks().size());
        assertEquals("掃除する", toDoList.getTasks().get(0).getDescription());
    }

    @Test
    void removeThrowsExceptionForInvalidIndex() {
        assertThrows(ToDoListException.class, () -> toDoList.remove(0));
    }

    @Test
    void saveToAndLoadFromRoundTripsTaskState() throws IOException {
        // 完了/未完了が混在した状態でファイルに保存し、別のToDoListインスタンスへ読み込んだ結果が
        // 元の状態(説明・完了フラグ・順序)と完全に一致することを確認する「ラウンドトリップテスト」。
        toDoList.add("牛乳を買う");
        toDoList.add("掃除する");
        toDoList.complete(1);
        File file = tempDir.resolve("todo.txt").toFile();

        toDoList.saveTo(file);
        ToDoList loaded = new ToDoList();
        loaded.loadFrom(file);

        assertEquals(2, loaded.getTasks().size());
        assertEquals("牛乳を買う", loaded.getTasks().get(0).getDescription());
        assertFalse(loaded.getTasks().get(0).isDone());
        assertEquals("掃除する", loaded.getTasks().get(1).getDescription());
        assertTrue(loaded.getTasks().get(1).isDone());
    }
}
