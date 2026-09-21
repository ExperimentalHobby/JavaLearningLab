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
 * {@link MazeGenerator}が生成する完全迷路(任意の2マス間の経路が一意に定まる、閉路のない迷路)を
 * 前提としている。閉路のある一般のグラフに対しては、{@code cameFrom}をpush時点で確定させる実装のため、
 * 実際に辿った経路(スタックの積み下ろし順)と一致しない経路を返すことがある。
 */
public class DfsMazeSolver implements MazeSolver {

    @Override
    public List<Cell> solve(Maze maze) {
        // DFSはLIFOスタックを使い、行けるところまで一方向に深く進んでから引き返す。
        // BFSと異なり見つかる経路は最短とは限らないが、メモリ効率がよく実装もシンプルになる。
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
