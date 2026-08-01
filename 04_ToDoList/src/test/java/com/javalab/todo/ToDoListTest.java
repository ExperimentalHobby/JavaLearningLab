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
        assertThrows(ToDoListException.class, () -> toDoList.complete(0));
    }

    @Test
    void removeDeletesTaskAtIndexAndShrinksSize() {
        toDoList.add("牛乳を買う");
        toDoList.add("掃除する");

        toDoList.remove(0);

        assertEquals(1, toDoList.getTasks().size());
        assertEquals("掃除する", toDoList.getTasks().get(0).getDescription());
    }

    @Test
    void removeThrowsExceptionForInvalidIndex() {
        assertThrows(ToDoListException.class, () -> toDoList.remove(0));
    }

    @Test
    void saveToAndLoadFromRoundTripsTaskState() throws IOException {
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
