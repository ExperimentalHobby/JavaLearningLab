package com.javalab.mazesolver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link BfsMazeSolver} の経路探索を検証するテスト。
 * 一直線につながった単純な迷路(a-b-c)を使い、経路発見と到達不能時の挙動を確認する。
 * BFS/DFS/A*の3クラスとも同一構造の迷路でテストしており、
 * アルゴリズムが違っても同じ入出力仕様({@link MazeSolver}インターフェース)を満たすことを示している。
 */
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
