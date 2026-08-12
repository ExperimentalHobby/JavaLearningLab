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
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void insertReturnsGeneratedId() {
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
    void updateDoneReturnsFalseForNonExistentId() {
        boolean updated = repository.updateDone(999, true);

        assertFalse(updated);
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
