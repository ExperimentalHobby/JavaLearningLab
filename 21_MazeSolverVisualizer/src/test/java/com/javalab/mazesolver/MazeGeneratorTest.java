package com.javalab.mazesolver;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MazeGeneratorTest {

    private final MazeGenerator generator = new MazeGenerator();

    @Test
    void generateProducesIdenticalMazeForSameSeed() {
        Maze first = generator.generate(5, 5, 42L);
        Maze second = generator.generate(5, 5, 42L);

        assertEquals(first.passages(), second.passages());
    }

    @Test
    void generateProducesMazeWhereGoalIsReachableFromStart() {
        Maze maze = generator.generate(5, 5, 42L);

        // ソルバー実装に依存せず迷路自体の連結性を検証するため、素朴なBFSで直接確認する。
        Set<Cell> visited = new HashSet<>();
        Deque<Cell> queue = new ArrayDeque<>();
        queue.add(maze.start());
        visited.add(maze.start());
        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            for (Cell neighbor : maze.openNeighbors(current)) {
                if (visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }

        assertTrue(visited.contains(maze.goal()));
    }
}
