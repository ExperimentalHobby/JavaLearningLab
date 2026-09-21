package com.javalab.gameoflife;

import java.util.Arrays;

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
        validateInBounds(row, col);
        return cells[row][col];
    }

    public void setAlive(int row, int col, boolean alive) {
        validateInBounds(row, col);
        cells[row][col] = alive;
    }

    private void validateInBounds(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            throw new IllegalArgumentException(
                    "座標が盤面の範囲外です: row=" + row + ", col=" + col + ", width=" + width + ", height=" + height);
        }
    }

    /**
     * 指定マスの周囲8マス(上下左右・斜め)のうち生存しているマスの数を数える。
     * グリッド外は非生存として扱う(トーラス状のラップアラウンドはしない)。
     * @param row 対象マスの行
     * @param col 対象マスの列
     * @return 生存している隣接マスの数(0〜8)
     */
    public int countLiveNeighbors(int row, int col) {
        return countLiveNeighbors(row, col, false);
    }

    /**
     * 指定マスの周囲8マス(上下左右・斜め)のうち生存しているマスの数を数える。
     * @param row 対象マスの行
     * @param col 対象マスの列
     * @param wrapAround trueの場合、盤面の端を反対側の端と繋がっているものとして扱う(トーラスモード)
     * @return 生存している隣接マスの数(0〜8)
     */
    public int countLiveNeighbors(int row, int col, boolean wrapAround) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }
                int r = row + dr;
                int c = col + dc;
                if (wrapAround) {
                    r = Math.floorMod(r, height);
                    c = Math.floorMod(c, width);
                }
                if (r >= 0 && r < height && c >= 0 && c < width && cells[r][c]) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Grid other)) {
            return false;
        }
        return width == other.width && height == other.height && Arrays.deepEquals(cells, other.cells);
    }

    @Override
    public int hashCode() {
        return 31 * (31 * width + height) + Arrays.deepHashCode(cells);
    }
}
