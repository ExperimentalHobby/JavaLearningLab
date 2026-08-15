package com.javalab.mazesolver;

import java.util.List;
import java.util.Map;

/**
 * 迷路生成&探索ビジュアライザのアルゴリズム選択・アニメーション状態遷移を担う。
 * {@code javafx.animation.Timeline}・{@code Canvas} 等のJavaFX描画要素には依存しないため、
 * GUIを起動せずに単体テストできる。
 */
public class MazeVisualizerState {

    private final MazeGenerator generator;
    private final Map<String, MazeSolver> solvers;
    private Maze maze;
    private boolean animating;

    public MazeVisualizerState(MazeGenerator generator, Map<String, MazeSolver> solvers) {
        this.generator = generator;
        this.solvers = solvers;
    }

    /**
     * 新しい迷路を生成する。実行中のアニメーションがあってもリセットする。
     * @param width 迷路の幅(マス数)
     * @param height 迷路の高さ(マス数)
     * @param seed 乱数シード
     * @return 生成された迷路
     */
    public Maze generateMaze(int width, int height, long seed) {
        maze = generator.generate(width, height, seed);
        animating = false;
        return maze;
    }

    /**
     * @return アニメーションが実行中の場合はtrue
     */
    public boolean isAnimating() {
        return animating;
    }

    /**
     * 指定されたアルゴリズム名に対応するソルバーを返す。
     * @param algorithmName アルゴリズム名("BFS"/"DFS"/"A*")
     * @return 対応するソルバー
     * @throws IllegalArgumentException 未知のアルゴリズム名が指定された場合
     */
    public MazeSolver solverFor(String algorithmName) {
        MazeSolver solver = solvers.get(algorithmName);
        if (solver == null) {
            throw new IllegalArgumentException("不明なアルゴリズムです: " + algorithmName);
        }
        return solver;
    }

    /**
     * @return 現在保持している迷路。{@link #generateMaze(int, int, long)} 呼び出し前はnull
     */
    public Maze currentMaze() {
        return maze;
    }

    /**
     * 指定アルゴリズムで現在の迷路を探索し、アニメーション実行中状態にする。
     * @param algorithmName アルゴリズム名("BFS"/"DFS"/"A*")
     * @return スタートからゴールまでの経路
     */
    public List<Cell> startAnimation(String algorithmName) {
        List<Cell> path = solverFor(algorithmName).solve(maze);
        animating = true;
        return path;
    }
}
