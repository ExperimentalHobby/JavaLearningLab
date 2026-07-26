package com.javalab.rps;

import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * 対話式CLIじゃんけんゲームのエントリーポイント。
 * 標準入力から「グー」「チョキ」「パー」を1行ずつ読み取り、コンピュータと対戦した結果を表示する。
 * {@code exit} で終了する。
 */
public class Main {

    public static void main(String[] args) {
        Random random = new Random();
        run(new Scanner(System.in), System.out, () -> RockPaperScissorsGame.randomHand(random));
    }

    /**
     * REPLループ本体。{@link Scanner}/{@link PrintStream} に加え、コンピュータの手を
     * {@link Supplier} で受け取ることで、テストから固定の手を注入できるようにしている。
     * @param scanner 入力読み取り元
     * @param out 出力先
     * @param computerHandSupplier コンピュータの手を決定する処理(本番では乱数、テストでは固定値)
     */
    static void run(Scanner scanner, PrintStream out, Supplier<Hand> computerHandSupplier) {
        out.println("じゃんけんゲームへようこそ。「グー」「チョキ」「パー」のいずれかを入力してください。");
        out.println("終了: exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            Hand playerHand = parseHand(line);
            if (playerHand == null) {
                // 不正な入力はクラッシュさせず、エラー表示して次の入力へ進む。
                out.println("エラー: 「グー」「チョキ」「パー」のいずれかを入力してください");
                continue;
            }

            Hand computerHand = computerHandSupplier.get();
            Result result = RockPaperScissorsGame.judge(playerHand, computerHand);
            out.println("あなた: " + toDisplayName(playerHand) + " / コンピュータ: " + toDisplayName(computerHand));
            out.println(toMessage(result));
        }
    }

    /**
     * 入力文字列を {@link Hand} に変換する。
     * @param input 標準入力から読み取った1行
     * @return 対応するHand、いずれにも一致しない場合はnull
     */
    private static Hand parseHand(String input) {
        return switch (input) {
            case "グー" -> Hand.ROCK;
            case "チョキ" -> Hand.SCISSORS;
            case "パー" -> Hand.PAPER;
            default -> null;
        };
    }

    private static String toDisplayName(Hand hand) {
        return switch (hand) {
            case ROCK -> "グー";
            case SCISSORS -> "チョキ";
            case PAPER -> "パー";
        };
    }

    private static String toMessage(Result result) {
        return switch (result) {
            case PLAYER_WIN -> "あなたの勝ちです";
            case COMPUTER_WIN -> "あなたの負けです";
            case DRAW -> "引き分けです";
        };
    }
}
