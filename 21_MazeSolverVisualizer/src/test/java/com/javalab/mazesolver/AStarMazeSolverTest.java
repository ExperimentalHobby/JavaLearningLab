package com.javalab.mazesolver;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

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

    @Test
    void solveReturnsShortestPathEvenWhenCellCostIsRevisedAfterQueueing() {
        // 優先度キューの比較器が可変のcostSoFarを参照していたバグの再現テスト。
        // 完全迷路(閉路なし)では経路が一意なため再現できないが、閉路のあるグラフでは
        // 「セルAがキューに入った後、より短い経路でセルAのコストが更新される」ケースが発生し得る。
        // このコード(size=18, seed=91)は、修正前の実装ではBFSの最短経路長(35)より長い経路(37)を
        // 返すことを乱数探索で確認済みの再現ケース。
        int size = 18;
        Random random = new Random(91L * 1000 + size);
        Cell start = new Cell(0, 0);
        Cell goal = new Cell(size - 1, size - 1);
        Maze maze = new Maze(size, size, start, goal);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Cell cell = new Cell(row, col);
                if (col + 1 < size && random.nextDouble() < 0.6) {
                    maze.connect(cell, new Cell(row, col + 1));
                }
                if (row + 1 < size && random.nextDouble() < 0.6) {
                    maze.connect(cell, new Cell(row + 1, col));
                }
            }
        }
        int shortestLength = new BfsMazeSolver().solve(maze).size();

        List<Cell> path = solver.solve(maze);

        assertEquals(shortestLength, path.size());
    }
}
