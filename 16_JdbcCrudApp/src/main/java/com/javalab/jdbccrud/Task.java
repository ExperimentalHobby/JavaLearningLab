package com.javalab.jdbccrud;

/**
 * タスク1件を表す不変な値オブジェクト。
 * @param id タスクID(DBで採番)
 * @param title タスク名
 * @param done 完了状態
 */
public record Task(long id, String title, boolean done) {
}
