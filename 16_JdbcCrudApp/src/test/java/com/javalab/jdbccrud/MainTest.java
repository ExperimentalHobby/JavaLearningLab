package com.javalab.jdbccrud;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    private Connection connection;
    private TaskRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        repository = new TaskRepository(connection);
        repository.initSchema();
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void runHandlesAddListDoneDeleteCommands() {
        Scanner scanner = new Scanner(
                "add 牛乳を買う\n"
                        + "list\n"
                        + "done 1\n"
                        + "list\n"
                        + "delete 1\n"
                        + "list\n"
                        + "exit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, repository);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("追加しました"));
        assertTrue(result.contains("牛乳を買う"));
        assertTrue(result.contains("完了にしました"));
        assertTrue(result.contains("[完了]"));
        assertTrue(result.contains("削除しました"));
        assertTrue(result.contains("タスクはありません"));
    }

    @Test
    void runShowsErrorAndContinuesForUnknownCommand() {
        Scanner scanner = new Scanner("foobar\nlist\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, repository);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("不明なコマンドです"));
        assertTrue(result.contains("タスクはありません"));
    }
}
