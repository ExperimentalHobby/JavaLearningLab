package com.javalab.todo;

/**
 * ToDoリストの1タスク。完了状態(done)を持つ可変オブジェクト。
 */
public class Task {

    private final String description;
    private boolean done;

    public Task(String description) {
        this.description = description;
        this.done = false;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return done;
    }

    public void markDone() {
        done = true;
    }

    /**
     * ファイル保存用の1行形式に変換する(例: {@code "[x] 説明"} / {@code "[ ] 説明"})。
     * @return ファイル保存形式の文字列
     */
    public String toFileLine() {
        return (done ? "[x] " : "[ ] ") + description;
    }

    /**
     * {@link #toFileLine()} で書き出した形式の1行からTaskを復元する。
     * @param line ファイルから読み込んだ1行
     * @return 復元されたTask
     */
    public static Task fromFileLine(String line) {
        boolean fileDone = line.startsWith("[x] ");
        // "[x] " / "[ ] " はどちらも4文字なので、共通のオフセットで説明部分を取り出せる。
        String fileDescription = line.substring(4);
        Task task = new Task(fileDescription);
        if (fileDone) {
            task.markDone();
        }
        return task;
    }
}
