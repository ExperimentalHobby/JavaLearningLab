package com.javalab.gameoflife;

import java.util.Random;

/**
 * ライフゲームの盤面編集・世代進行・クリア・ランダム生成という状態遷移を担う。
 * {@link javax.swing.Timer}・{@code JPanel} 等のSwing描画要素には依存しないため、
 * GUIを起動せずに単体テストできる。
 */
public class LifeBoardState {

    private final int width;
    private final int height;
    private final GameOfLife gameOfLife;
    private final Random random;
    private Grid grid;
    private int generation;

    public LifeBoardState(int width, int height, GameOfLife gameOfLife, Random random) {
        this.width = width;
        this.height = height;
        this.gameOfLife = gameOfLife;
        this.random = random;
        this.grid = new Grid(width, height);
    }

    /**
     * @return 現在の盤面
     */
    public Grid grid() {
        return grid;
    }

    /**
     * @return 現在の世代数
     */
    public int generation() {
        return generation;
    }

    /**
     * 指定マスの生死を反転する。
     * @param row 対象マスの行
     * @param col 対象マスの列
     */
    public void toggleCell(int row, int col) {
        grid.setAlive(row, col, !grid.isAlive(row, col));
    }

    /**
     * 盤面を1世代進める。
     */
    public void step() {
        grid = gameOfLife.nextGeneration(grid);
        generation++;
    }

    /**
     * 盤面を全マス非生存の状態に戻し、世代数を0にリセットする。
     */
    public void clear() {
        grid = new Grid(width, height);
        generation = 0;
    }

    /**
     * ランダムな初期パターンを生成し、世代数を0にリセットする。
     */
    public void randomize() {
        Grid newGrid = new Grid(width, height);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // 2回のnextBooleanのANDで生存率を約25%に抑え、初期盤面が密集しすぎないようにする。
                newGrid.setAlive(row, col, random.nextBoolean() && random.nextBoolean());
            }
        }
        grid = newGrid;
        generation = 0;
    }
}
