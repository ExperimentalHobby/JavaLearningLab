package com.javalab.genericcollection;

/**
 * 空のスタック/キューに対する{@code pop}/{@code peek}/{@code dequeue}操作を統一して表す非チェック例外。
 */
public class EmptyCollectionException extends RuntimeException {

    public EmptyCollectionException(String message) {
        super(message);
    }
}
