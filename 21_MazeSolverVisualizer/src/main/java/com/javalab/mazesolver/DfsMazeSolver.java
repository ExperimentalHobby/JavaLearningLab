package com.javalab.mazesolver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 深さ優先探索(DFS)で迷路を解くソルバー。
 */
public class DfsMazeSolver implements MazeSolver {

    @Override
    public List<Cell> solve(Maze maze) {
        Set<Cell> visited = new HashSet<>();
        Map<Cell, Cell> cameFrom = new HashMap<>();
        Deque<Cell> stack = new ArrayDeque<>();
        stack.push(maze.start());
        visited.add(maze.start());

        while (!stack.isEmpty()) {
            Cell current = stack.pop();
            if (current.equals(maze.goal())) {
                return reconstructPath(cameFrom, current);
            }
            for (Cell neighbor : maze.openNeighbors(current)) {
                if (visited.add(neighbor)) {
                    cameFrom.put(neighbor, current);
                    stack.push(neighbor);
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
