package com.javalab.mazesolver;

import javafx.application.Application;

/**
 * 迷路生成&探索ビジュアライザのエントリーポイント。
 * {@link MazeVisualizerApp}(JavaFXの{@code Application}サブクラス)を{@code Application.launch}で起動する。
 */
public class Main {

    public static void main(String[] args) {
        Application.launch(MazeVisualizerApp.class, args);
    }
}
