package com.javalab.todo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
