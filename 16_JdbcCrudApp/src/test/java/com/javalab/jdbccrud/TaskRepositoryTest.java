package com.javalab.jdbccrud;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link TaskRepository} のCRUD操作を検証するテスト。
 * モックを使わず、SQLiteのインメモリDB({@code jdbc:sqlite::memory:})に対して
 * 実際にSQLを発行することで、生成したSQLが正しく動作することまで確認する。
 */
class TaskRepositoryTest {

    private Connection connection;
    private TaskRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        // インメモリDBは同一Connectionを保持している間だけ内容が維持されるため、
        // Connectionをテスト間で使い回さずBeforeEachで毎回新規作成する。
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        repository = new TaskRepository(connection);
        repository.initSchema();
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void findAllReturnsInsertedTask() {
        repository.insert("牛乳を買う");

        List<Task> tasks = repository.findAll();

        assertEquals(1, tasks.size());
        assertEquals("牛乳を買う", tasks.get(0).title());
        assertFalse(tasks.get(0).done());
    }

    @Test
    void insertThrowsExceptionForBlankTitle() {
        // title TEXT NOT NULLは空文字を許容するため、空タイトルの登録を防げていなかった。
        assertThrows(TaskRepositoryException.class, () -> repository.insert("   "));
    }

    @Test
    void insertReturnsGeneratedId() {
        // AUTOINCREMENTのIDが挿入順に1ずつ増えることを確認する
        // (SQLiteの採番仕様に依存したテストであることに注意)。
        long firstId = repository.insert("牛乳を買う");
        long secondId = repository.insert("卵を買う");

        assertEquals(firstId + 1, secondId);
    }

    @Test
    void findAllReturnsMultipleTasksOrderedById() {
        repository.insert("1件目");
        repository.insert("2件目");
        repository.insert("3件目");

        List<Task> tasks = repository.findAll();

        assertEquals(3, tasks.size());
        assertEquals("1件目", tasks.get(0).title());
        assertEquals("2件目", tasks.get(1).title());
        assertEquals("3件目", tasks.get(2).title());
    }

    @Test
    void updateDoneMarksTaskAsDone() {
        long id = repository.insert("牛乳を買う");

        boolean updated = repository.updateDone(id, true);

        assertTrue(updated);
        assertTrue(repository.findAll().get(0).done());
    }

    @Test
    void updateTitleChangesTaskTitle() {
        // 更新(U)が完了フラグの切り替えだけで、タイトルを変更する手段がなかった問題への対応。
        long id = repository.insert("牛乳を買う");

        boolean updated = repository.updateTitle(id, "牛乳とパンを買う");

        assertTrue(updated);
        assertEquals("牛乳とパンを買う", repository.findAll().get(0).title());
    }

    @Test
    void updateTitleReturnsFalseForNonExistentId() {
        boolean updated = repository.updateTitle(999, "存在しないタスク");

        assertFalse(updated);
    }

    @Test
    void updateTitleThrowsExceptionForBlankTitle() {
        long id = repository.insert("牛乳を買う");

        assertThrows(TaskRepositoryException.class, () -> repository.updateTitle(id, " "));
    }

    @Test
    void updateDoneReturnsFalseForNonExistentId() {
        boolean updated = repository.updateDone(999, true);

        assertFalse(updated);
    }

    @Test
    void insertAllInsertsMultipleTasksInSingleTransaction() {
        // トランザクション(setAutoCommit(false)/commit)・バッチ更新(addBatch/executeBatch)が
        // 未収録だった学習テーマへの対応。複数タイトルを1回のトランザクションでまとめて登録できることを確認する。
        int count = repository.insertAll(List.of("1件目", "2件目", "3件目"));

        assertEquals(3, count);
        assertEquals(3, repository.findAll().size());
    }

    @Test
    void insertAllRollsBackAllInsertsWhenOneTitleIsBlank() {
        // バッチの途中に不正なタイトル(空白)が含まれる場合、他の正常なタイトルも
        // 一切登録されない(トランザクション全体がロールバックされる)ことを確認する。
        assertThrows(TaskRepositoryException.class,
                () -> repository.insertAll(List.of("1件目", "  ", "3件目")));

        assertEquals(0, repository.findAll().size());
    }

    @Test
    void deleteRemovesTask() {
        long id = repository.insert("牛乳を買う");

        boolean deleted = repository.delete(id);

        assertTrue(deleted);
        assertEquals(0, repository.findAll().size());
    }

    @Test
    void deleteReturnsFalseForNonExistentId() {
        boolean deleted = repository.delete(999);

        assertFalse(deleted);
    }
}
