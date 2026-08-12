package com.javalab.quiz;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuizLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void loadsQuestionsFromPropertiesFile() throws IOException {
        File file = tempDir.resolve("questions.properties").toFile();
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            writer.println("question.count=2");
            writer.println("q1.question=Javaの生みの親は誰?");
            writer.println("q1.answer=James Gosling");
            writer.println("q2.question=1+1は?");
            writer.println("q2.answer=2");
        }

        List<Question> questions = QuizLoader.loadFromProperties(file);

        assertEquals(2, questions.size());
        assertEquals("Javaの生みの親は誰?", questions.get(0).getText());
        assertEquals("1+1は?", questions.get(1).getText());
    }

    @Test
    void throwsExceptionWhenQuestionCountIsMissing() throws IOException {
        File file = tempDir.resolve("no-count.properties").toFile();
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            writer.println("q1.question=Javaの生みの親は誰?");
            writer.println("q1.answer=James Gosling");
        }

        assertThrows(QuizLoaderException.class, () -> QuizLoader.loadFromProperties(file));
    }

    @Test
    void throwsExceptionWhenAnswerKeyIsMissing() throws IOException {
        File file = tempDir.resolve("no-answer.properties").toFile();
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            writer.println("question.count=1");
            writer.println("q1.question=Javaの生みの親は誰?");
        }

        assertThrows(QuizLoaderException.class, () -> QuizLoader.loadFromProperties(file));
    }
}
