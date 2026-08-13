package com.javalab.mazesolver;

/**
 * 迷路上の1マスを表す不変な値オブジェクト。
 * @param row 行番号(0始まり)
 * @param col 列番号(0始まり)
 */
public record Cell(int row, int col) {
}
