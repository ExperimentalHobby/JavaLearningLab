package com.javalab.mavenclipackaging;

/**
 * テキストの統計情報。
 * @param lines 行数
 * @param words 単語数(空白区切り)
 * @param chars 文字数
 */
public record TextStatistics(int lines, int words, int chars) {
}
