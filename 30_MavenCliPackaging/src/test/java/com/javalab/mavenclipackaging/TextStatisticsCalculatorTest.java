package com.javalab.mavenclipackaging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextStatisticsCalculatorTest {

    private final TextStatisticsCalculator calculator = new TextStatisticsCalculator();

    @Test
    void calculateCountsLinesWordsAndCharsForNormalText() {
        TextStatistics stats = calculator.calculate("Hello world\nJava is fun");

        assertEquals(2, stats.lines());
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

        assertEquals(3, stats.lines());
        assertEquals(0, stats.words());
        assertEquals(2, stats.chars());
    }
}
