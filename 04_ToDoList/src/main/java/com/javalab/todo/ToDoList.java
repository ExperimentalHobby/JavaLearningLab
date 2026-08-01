package com.javalab.todo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ArrayListでタスクを管理し、java.ioでファイルへの保存・読込を行うToDoリスト。
 */
public class ToDoList {

    private final List<Task> tasks = new ArrayList<>();

    /**
     * 新しい未完了タスクを末尾に追加する。
     * @param description タスクの説明
     */
    public void add(String description) {
        tasks.add(new Task(description));
    }

    /**
     * 指定インデックスのタスクを完了状態にする。
     * @param index タスクのインデックス
     * @throws ToDoListException indexが範囲外の場合
     */
    public void complete(int index) {
        validateIndex(index);
        tasks.get(index).markDone();
    }

    /**
     * 指定インデックスのタスクを削除する。
     * @param index タスクのインデックス
     * @throws ToDoListException indexが範囲外の場合
     */
    public void remove(int index) {
        validateIndex(index);
        tasks.remove(index);
    }

    private void validateIndex(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new ToDoListException("不正なタスク番号です: " + index);
        }
    }

    /**
     * 現在のタスク一覧をファイルに書き出す(1行1タスク、{@link Task#toFileLine()}形式)。
     * @param file 書き込み先ファイル
     * @throws IOException 書き込みに失敗した場合
     */
    public void saveTo(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Task task : tasks) {
                writer.write(task.toFileLine());
                writer.newLine();
            }
        }
    }

    /**
     * ファイルからタスク一覧を読み込み、現在のタスクを置き換える。
     * @param file 読み込み元ファイル
     * @throws IOException 読み込みに失敗した場合
     */
    public void loadFrom(File file) throws IOException {
        // 読込前にクリアすることで、load後の状態がファイル内容と完全に一致するようにする。
        tasks.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tasks.add(Task.fromFileLine(line));
            }
        }
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
