package com.javalab.mazesolver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link DfsMazeSolver} の経路探索を検証するテスト。
 * {@link BfsMazeSolverTest}と同一構造の迷路(一直線のa-b-c)を用いており、
 * このように単純な経路ではDFSもBFSと同じ最短経路を返すことを確認できる
 * (経路が複数存在する複雑な迷路ではDFSは最短性を保証しない点に注意)。
 */
class DfsMazeSolverTest {

    private final DfsMazeSolver solver = new DfsMazeSolver();

    @Test
    void solveReturnsPathFromStartToGoal() {
        Cell a = new Cell(0, 0);
        Cell b = new Cell(0, 1);
        Cell c = new Cell(0, 2);
        Maze maze = new Maze(3, 1, a, c);
        maze.connect(a, b);
        maze.connect(b, c);

        List<Cell> path = solver.solve(maze);

        assertEquals(List.of(a, b, c), path);
    }

    @Test
    void solveReturnsEmptyListWhenGoalIsUnreachable() {
        Cell start = new Cell(0, 0);
        Cell goal = new Cell(0, 1);
        Maze maze = new Maze(2, 1, start, goal);

        List<Cell> path = solver.solve(maze);

        assertEquals(List.of(), path);
    }
}
