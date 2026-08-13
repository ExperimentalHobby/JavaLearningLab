package com.javalab.mazesolver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BfsMazeSolverTest {

    private final BfsMazeSolver solver = new BfsMazeSolver();

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
        // start・goalを接続しない(通路なし)ことで到達不可能な状態を作る。

        List<Cell> path = solver.solve(maze);

        assertEquals(List.of(), path);
    }
}
