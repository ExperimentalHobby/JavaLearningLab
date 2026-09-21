package com.javalab.mavenclipackaging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link TextStatisticsCalculator#calculate(String)} の行数・単語数・文字数計算を検証するテスト。
 * 行数は{@code wc -l}と同じ「改行文字(\n)の出現回数」で数える仕様とし、
 * 末尾が改行で終わるかどうかに関わらず一貫した値になることを確認する。
 */
class TextStatisticsCalculatorTest {

    private final TextStatisticsCalculator calculator = new TextStatisticsCalculator();

    @Test
    void calculateCountsLinesWordsAndCharsForNormalText() {
        // "Hello world\nJava is fun"は改行が1個(末尾に改行なし)。
        // wc -lは改行文字の出現回数を数えるため、行数は1になる。
        TextStatistics stats = calculator.calculate("Hello world\nJava is fun");

        assertEquals(1, stats.lines());
        assertEquals(5, stats.words());
        assertEquals(23, stats.chars());
    }

    @Test
    void calculateReturnsAllZerosForEmptyString() {
        TextStatistics stats = calculator.calculate("");

        assertEquals(0, stats.lines());
        assertEquals(0, stats.words());
        assertEquals(0, stats.chars());
    }

    @Test
    void calculateCountsLinesCorrectlyForNewlineOnlyText() {
        TextStatistics stats = calculator.calculate("\n\n");

        assertEquals(2, stats.lines());
        assertEquals(0, stats.words());
        assertEquals(2, stats.chars());
    }

    @Test
    void calculateDoesNotCountTrailingEmptyElementAsExtraLine() {
        // 修正前は"a\nb\n".split("\n", -1).lengthが3(本来2であるべき)になっていた問題への対応。
        // wc -lで"a\nb\n"を数えると2になる(改行文字が2個)。
        TextStatistics stats = calculator.calculate("a\nb\n");

        assertEquals(2, stats.lines());
    }

    @Test
    void calculateNormalizesCrlfLineEndingsWhenCountingLines() {
        // Windowsで作成したファイル(CRLF改行)でも、LF改行と同じ行数になることを確認する。
        TextStatistics stats = calculator.calculate("a\r\nb\r\n");

        assertEquals(2, stats.lines());
    }

    @Test
    void calculateExcludesCarriageReturnFromCharCount() {
        // \rが文字数に含まれてしまっていた問題への対応。
        // "a\r\nb"は正規化後"a\nb"(3文字)になるはずで、\r込みの4文字にはならない。
        TextStatistics stats = calculator.calculate("a\r\nb");

        assertEquals(3, stats.chars());
    }
}
