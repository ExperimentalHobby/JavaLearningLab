package com.javalab.mazesolver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link AStarMazeSolver} の経路探索を検証するテスト。
 * {@link BfsMazeSolverTest}と同一構造の迷路を用いており、単純な迷路ではA*もBFSと
 * 同じ最短経路を返すことを確認できる(ヒューリスティックの効果は分岐が多い迷路で顕著になる)。
 */
class AStarMazeSolverTest {

    private final AStarMazeSolver solver = new AStarMazeSolver();

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
