package com.javalab.mazesolver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 幅優先探索(BFS)で迷路を解くソルバー。
 */
public class BfsMazeSolver implements MazeSolver {

    @Override
    public List<Cell> solve(Maze maze) {
        Map<Cell, Cell> cameFrom = new HashMap<>();
        Deque<Cell> queue = new ArrayDeque<>();
        queue.add(maze.start());
        cameFrom.put(maze.start(), null);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            if (current.equals(maze.goal())) {
                return reconstructPath(cameFrom, current);
            }
            for (Cell neighbor : maze.openNeighbors(current)) {
                if (!cameFrom.containsKey(neighbor)) {
                    cameFrom.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return List.of();
    }

    private List<Cell> reconstructPath(Map<Cell, Cell> cameFrom, Cell goal) {
        List<Cell> path = new ArrayList<>();
        for (Cell cell = goal; cell != null; cell = cameFrom.get(cell)) {
            path.add(cell);
        }
        return path.reversed();
    }
}
