package com.javalab.gameoflife;

import javax.swing.SwingUtilities;

/**
 * ライフゲームのエントリーポイント。
 */
public class Main {

    public static void main(String[] args) {
        // SwingのGUI操作はイベントディスパッチスレッド(EDT)上で行う必要があるため、
        // invokeLaterでウィンドウ生成・表示をEDTのキューに乗せる。
        SwingUtilities.invokeLater(() -> new LifeFrame().setVisible(true));
    }
}
