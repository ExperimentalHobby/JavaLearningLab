package com.javalab.todo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
