package com.javalab.todo;

/**
 * 不正なタスク番号・不明なコマンドなど、ToDoリスト固有のエラーを表す例外。
 */
public class ToDoListException extends RuntimeException {

    /**
     * @param message エラー内容を説明するメッセージ
     */
    public ToDoListException(String message) {
        super(message);
    }
}
