package com.javalab.todo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Task} のファイル形式変換({@link Task#toFileLine()} / {@link Task#fromFileLine(String)})を検証するテスト。
 * この2つは対になっており、書き出した内容を再度読み込んで元の状態に戻せる(ラウンドトリップできる)
 * ことが重要なので、完了/未完了の両方について書き出し・復元それぞれを確認する。
 */
class TaskTest {

    @Test
    void toFileLineForUnfinishedTask() {
        Task task = new Task("買い物に行く");

        assertEquals("[ ] 買い物に行く", task.toFileLine());
    }

    @Test
    void toFileLineForFinishedTask() {
        Task task = new Task("買い物に行く");
        task.markDone();

        assertEquals("[x] 買い物に行く", task.toFileLine());
    }

    @Test
    void fromFileLineRestoresFinishedTask() {
        // "[x] " プレフィックスから完了状態を、それ以降の文字列から説明文を復元できることを確認する。
        Task task = Task.fromFileLine("[x] 掃除する");

        assertEquals("掃除する", task.getDescription());
        assertTrue(task.isDone());
    }

    @Test
    void fromFileLineRestoresUnfinishedTask() {
        Task task = Task.fromFileLine("[ ] 洗濯する");

        assertEquals("洗濯する", task.getDescription());
        assertFalse(task.isDone());
    }

    @Test
    void toDisplayLineIsIndependentFromFileLineFormat() {
        // printTasksが永続化用のtoFileLine()を画面表示に流用していると、保存形式と表示形式が
        // 結合してしまう。表示専用のtoDisplayLine()を持つことを確認する
        // (現時点では見た目はtoFileLine()と同じだが、以後どちらかを変更しても他方に影響しない)。
        Task task = new Task("買い物に行く");

        assertEquals("[ ] 買い物に行く", task.toDisplayLine());
    }

    @Test
    void fromFileLineThrowsExceptionForEmptyLine() {
        // 空行はline.substring(4)が無条件に呼ばれるとStringIndexOutOfBoundsExceptionで
        // クラッシュしていた。手編集されたtodo.txt等を安全にloadできるよう、
        // 空行や短すぎる行はToDoListExceptionとして扱う。
        assertThrows(ToDoListException.class, () -> Task.fromFileLine(""));
    }

    @Test
    void fromFileLineThrowsExceptionForLineWithoutValidPrefix() {
        // "[x] "/"[ ] "のいずれのプレフィックスも持たない行は不正な形式として扱う。
        assertThrows(ToDoListException.class, () -> Task.fromFileLine("不正な行"));
    }
}
