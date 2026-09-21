package com.javalab.mazesolver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * A*探索で迷路を解くソルバー。ヒューリスティックにはマンハッタン距離を用いる。
 */
public class AStarMazeSolver implements MazeSolver {

    /**
     * 優先度キューの要素。優先度は挿入時点の値をそのまま保持する。
     * 比較のたびに可変のマップを参照すると、挿入後にコストが更新された際にヒープの
     * 不変条件が壊れ、{@code poll()}が最小要素を返さなくなる(最短経路が保証されない)ため、
     * 挿入時に確定した値をイミュータブルなrecordとして持たせている。
     */
    private record Entry(Cell cell, int priority) {
    }

    @Override
    public List<Cell> solve(Maze maze) {
        // A*はBFSの「これまでのコスト」に加え、ゴールまでの推定距離(ヒューリスティック)を
        // 優先度に組み込むことで、ゴール方向を優先的に探索し無駄な探索を減らす。
        // ヒューリスティックが実際の距離を過大評価しない限り、最短経路が保証される。
        Cell goal = maze.goal();
        Map<Cell, Cell> cameFrom = new HashMap<>();
        Map<Cell, Integer> costSoFar = new HashMap<>();
        PriorityQueue<Entry> frontier = new PriorityQueue<>(Comparator.comparingInt(Entry::priority));

        costSoFar.put(maze.start(), 0);
        frontier.add(new Entry(maze.start(), manhattanDistance(maze.start(), goal)));

        while (!frontier.isEmpty()) {
            Cell current = frontier.poll().cell();
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }
            int newCost = costSoFar.get(current) + 1;
            for (Cell neighbor : maze.openNeighbors(current)) {
                if (!costSoFar.containsKey(neighbor) || newCost < costSoFar.get(neighbor)) {
                    costSoFar.put(neighbor, newCost);
                    cameFrom.put(neighbor, current);
                    frontier.add(new Entry(neighbor, newCost + manhattanDistance(neighbor, goal)));
                }
            }
        }
        return List.of();
    }

    private int manhattanDistance(Cell a, Cell b) {
        return Math.abs(a.row() - b.row()) + Math.abs(a.col() - b.col());
    }

    private List<Cell> reconstructPath(Map<Cell, Cell> cameFrom, Cell goal) {
        List<Cell> path = new ArrayList<>();
        for (Cell cell = goal; cell != null; cell = cameFrom.get(cell)) {
            path.add(cell);
        }
        return path.reversed();
    }
}
