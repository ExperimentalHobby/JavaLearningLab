package com.javalab.mazesolver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * 再帰的バックトラック法で「完全迷路」(任意の2マス間の経路が一意に定まる迷路)を生成するクラス。
 */
public class MazeGenerator {

    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    /**
     * 指定サイズの迷路を生成する。スタートは左上、ゴールは右下に固定する。
     * @param width 迷路の幅(マス数)
     * @param height 迷路の高さ(マス数)
     * @param seed 乱数シード。同じ値を指定すると常に同じ迷路が生成される
     * @return 生成された迷路
     */
    public Maze generate(int width, int height, long seed) {
        Cell start = new Cell(0, 0);
        Cell goal = new Cell(height - 1, width - 1);
        Maze maze = new Maze(width, height, start, goal);
        Random random = new Random(seed);

        boolean[][] visited = new boolean[height][width];
        Deque<Cell> stack = new ArrayDeque<>();
        stack.push(start);
        visited[start.row()][start.col()] = true;

        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            List<Cell> unvisitedNeighbors = unvisitedNeighborsOf(current, width, height, visited);
            if (unvisitedNeighbors.isEmpty()) {
                stack.pop();
                continue;
            }
            Cell next = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
            maze.connect(current, next);
            visited[next.row()][next.col()] = true;
            stack.push(next);
        }
        return maze;
    }

    private List<Cell> unvisitedNeighborsOf(Cell cell, int width, int height, boolean[][] visited) {
        List<Cell> neighbors = new ArrayList<>();
        for (int[] direction : DIRECTIONS) {
            int row = cell.row() + direction[0];
            int col = cell.col() + direction[1];
            if (row >= 0 && row < height && col >= 0 && col < width && !visited[row][col]) {
                neighbors.add(new Cell(row, col));
            }
        }
        return neighbors;
    }
}
