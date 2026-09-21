package com.javalab.gameoflife;

import java.util.List;

/**
 * ライフゲームの定番パターン。毎回クリックで配置する手間を省くため、あらかじめ知られたパターンを
 * {@link LifeBoardState#loadPreset(Preset)} でワンクリック配置できるようにする。
 * 各セル座標は盤面左上を原点(0,0)とした相対座標(行, 列)。
 */
public enum Preset {

    /** 斜め方向に無限に移動し続ける最小の「宇宙船」パターン。 */
    GLIDER(List.of(
            new int[]{0, 1},
            new int[]{1, 2},
            new int[]{2, 0}, new int[]{2, 1}, new int[]{2, 2})),

    /** 周期2で水平・垂直を繰り返す最小の振動子パターン。 */
    BLINKER(List.of(
            new int[]{0, 0}, new int[]{0, 1}, new int[]{0, 2})),

    /** 周期2で振動する6セルの振動子パターン。 */
    TOAD(List.of(
            new int[]{0, 1}, new int[]{0, 2}, new int[]{0, 3},
            new int[]{1, 0}, new int[]{1, 1}, new int[]{1, 2}));

    private final List<int[]> liveCells;

    Preset(List<int[]> liveCells) {
        this.liveCells = liveCells;
    }

    List<int[]> liveCells() {
        return liveCells;
    }
}
