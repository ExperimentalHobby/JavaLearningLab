package com.javalab.jdbccrud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code tasks}テーブルに対するCRUD操作を提供するリポジトリ。
 * SQL関連の失敗は{@link TaskRepositoryException}に統一して呼び出し元へ伝える。
 */
public class TaskRepository {

    private final Connection connection;

    /**
     * @param connection 使用するJDBC接続(テストは{@code jdbc:sqlite::memory:}、本番はファイルDBを想定)
     */
    public TaskRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@code tasks}テーブルが存在しなければ作成する。
     * @throws TaskRepositoryException スキーマ作成に失敗した場合
     */
    public void initSchema() {
        String sql = "CREATE TABLE IF NOT EXISTS tasks ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "done INTEGER NOT NULL DEFAULT 0)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new TaskRepositoryException("スキーマの初期化に失敗しました", e);
        }
    }

    /**
     * 新規タスクを登録する(完了状態は常に未完了で登録される)。
     * @param title タスク名
     * @return 採番されたタスクID
     * @throws TaskRepositoryException 登録に失敗した場合
     */
    public long insert(String title) {
        String sql = "INSERT INTO tasks (title, done) VALUES (?, 0)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, title);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        } catch (SQLException e) {
            throw new TaskRepositoryException("タスクの登録に失敗しました", e);
        }
    }

    /**
     * 全タスクをID昇順で取得する。
     * @return タスク一覧
     * @throws TaskRepositoryException 取得に失敗した場合
     */
    public List<Task> findAll() {
        String sql = "SELECT id, title, done FROM tasks ORDER BY id";
        List<Task> tasks = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                tasks.add(new Task(rs.getLong("id"), rs.getString("title"), rs.getBoolean("done")));
            }
            return tasks;
        } catch (SQLException e) {
            throw new TaskRepositoryException("タスク一覧の取得に失敗しました", e);
        }
    }

    /**
     * 指定IDのタスクの完了状態を更新する。
     * @param id 対象タスクID
     * @param done 更新後の完了状態
     * @return 対象行が存在し更新できた場合はtrue、該当行がなければfalse
     * @throws TaskRepositoryException 更新に失敗した場合
     */
    public boolean updateDone(long id, boolean done) {
        String sql = "UPDATE tasks SET done = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, done);
            statement.setLong(2, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new TaskRepositoryException("タスクの完了状態更新に失敗しました", e);
        }
    }

    /**
     * 指定IDのタスクを削除する。
     * @param id 対象タスクID
     * @return 対象行が存在し削除できた場合はtrue、該当行がなければfalse
     * @throws TaskRepositoryException 削除に失敗した場合
     */
    public boolean delete(long id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new TaskRepositoryException("タスクの削除に失敗しました", e);
        }
    }
}
