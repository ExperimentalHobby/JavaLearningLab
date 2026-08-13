package com.javalab.gameoflife;

/**
 * ライフゲームの盤面を2次元配列で表すクラス。
 */
public class Grid {

    private final int width;
    private final int height;
    private final boolean[][] cells;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new boolean[height][width];
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public boolean isAlive(int row, int col) {
        return cells[row][col];
    }

    public void setAlive(int row, int col, boolean alive) {
        cells[row][col] = alive;
    }

    /**
     * 指定マスの周囲8マス(上下左右・斜め)のうち生存しているマスの数を数える。
     * グリッド外は非生存として扱う(トーラス状のラップアラウンドはしない)。
     * @param row 対象マスの行
     * @param col 対象マスの列
     * @return 生存している隣接マスの数(0〜8)
     */
    public int countLiveNeighbors(int row, int col) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }
                int r = row + dr;
                int c = col + dc;
                if (r >= 0 && r < height && c >= 0 && c < width && cells[r][c]) {
                    count++;
                }
            }
        }
        return count;
    }
}
