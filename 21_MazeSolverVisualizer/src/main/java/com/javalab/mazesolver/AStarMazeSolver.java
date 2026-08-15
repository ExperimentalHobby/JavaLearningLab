package com.javalab.mazesolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * A*探索で迷路を解くソルバー。ヒューリスティックにはマンハッタン距離を用いる。
 */
public class AStarMazeSolver implements MazeSolver {

    @Override
    public List<Cell> solve(Maze maze) {
        // A*はBFSの「これまでのコスト」に加え、ゴールまでの推定距離(ヒューリスティック)を
        // 優先度に組み込むことで、ゴール方向を優先的に探索し無駄な探索を減らす。
        // ヒューリスティックが実際の距離を過大評価しない限り、最短経路が保証される。
        Cell goal = maze.goal();
        Map<Cell, Cell> cameFrom = new HashMap<>();
        Map<Cell, Integer> costSoFar = new HashMap<>();
        PriorityQueue<Cell> frontier = new PriorityQueue<>(
                (a, b) -> Integer.compare(priority(a, costSoFar, goal), priority(b, costSoFar, goal)));

        costSoFar.put(maze.start(), 0);
        frontier.add(maze.start());

        while (!frontier.isEmpty()) {
            Cell current = frontier.poll();
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }
            int newCost = costSoFar.get(current) + 1;
            for (Cell neighbor : maze.openNeighbors(current)) {
                if (!costSoFar.containsKey(neighbor) || newCost < costSoFar.get(neighbor)) {
                    costSoFar.put(neighbor, newCost);
                    cameFrom.put(neighbor, current);
                    frontier.add(neighbor);
                }
            }
        }
        return List.of();
    }

    private int priority(Cell cell, Map<Cell, Integer> costSoFar, Cell goal) {
        return costSoFar.get(cell) + manhattanDistance(cell, goal);
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
