package com.javalab.gameoflife;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

/**
 * ライフゲームの盤面編集・世代進行・クリア・ランダム生成という状態遷移を担う。
 * {@link javax.swing.Timer}・{@code JPanel} 等のSwing描画要素には依存しないため、
 * GUIを起動せずに単体テストできる。
 */
public class LifeBoardState {

    // 振動パターンの検出に必要な履歴の長さ。一般的な振動子(周期2〜3)を十分カバーできる値として10を選んだ。
    private static final int HISTORY_LIMIT = 10;

    private final int width;
    private final int height;
    private final GameOfLife gameOfLife;
    private final Random random;
    private final Deque<Grid> history = new ArrayDeque<>();
    private Grid grid;
    private int generation;
    private boolean stable;
    private boolean running;
    private boolean torusMode;

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
     * 指定マスの生死を反転する。実行中({@link #isRunning()}がtrue)は、次の世代で即座に
     * 上書きされてしまう編集を避けるため何もしない。
     * @param row 対象マスの行
     * @param col 対象マスの列
     */
    public void toggleCell(int row, int col) {
        if (running) {
            return;
        }
        grid.setAlive(row, col, !grid.isAlive(row, col));
    }

    /**
     * @return 実行中(アニメーションによる自動世代進行中)であればtrue
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * 実行中状態を設定する。GUI側のタイマー開始/停止と連動させる想定。
     * @param running 実行中であればtrue
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * @return トーラスモード(端が反対側と繋がっているとみなすラップアラウンド)が有効であればtrue
     */
    public boolean isTorusMode() {
        return torusMode;
    }

    /**
     * トーラスモードの有効/無効を切り替える。
     * @param torusMode trueの場合、{@link #step()}での隣接カウントに盤面端のラップアラウンドを適用する
     */
    public void setTorusMode(boolean torusMode) {
        this.torusMode = torusMode;
    }

    /**
     * 盤面を1世代進める。直近{@value #HISTORY_LIMIT}世代の履歴と比較し、
     * 同一盤面が既に現れていれば安定状態(または振動)として{@link #isStable()}がtrueを返すようにする。
     */
    public void step() {
        Grid next = gameOfLife.nextGeneration(grid, torusMode);
        stable = next.equals(grid) || history.contains(next);
        history.addLast(grid);
        if (history.size() > HISTORY_LIMIT) {
            history.removeFirst();
        }
        grid = next;
        generation++;
    }

    /**
     * @return 直近の{@link #step()}の結果、盤面が安定状態(変化なし、または過去の履歴と同一の振動状態)であればtrue
     */
    public boolean isStable() {
        return stable;
    }

    /**
     * 盤面をクリアしたうえで、指定プリセットのパターンを左上を原点として配置する。
     * 毎回クリックで配置する手間を省くための機能。
     * @param preset 配置するプリセットパターン
     */
    public void loadPreset(Preset preset) {
        clear();
        for (int[] cell : preset.liveCells()) {
            grid.setAlive(cell[0], cell[1], true);
        }
    }

    /**
     * 盤面を全マス非生存の状態に戻し、世代数を0にリセットする。
     */
    public void clear() {
        grid = new Grid(width, height);
        generation = 0;
        stable = false;
        history.clear();
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
        stable = false;
        history.clear();
    }
}
