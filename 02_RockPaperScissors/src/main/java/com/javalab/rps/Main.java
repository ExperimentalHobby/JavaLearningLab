package com.javalab.rps;

import java.io.PrintStream;
import java.util.Locale;
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

        // ラウンドごとの結果だけでなく通算成績も見せるための集計カウンタ。
        int playerWins = 0;
        int computerWins = 0;
        int draws = 0;

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

            switch (result) {
                case PLAYER_WIN -> playerWins++;
                case COMPUTER_WIN -> computerWins++;
                case DRAW -> draws++;
            }
            out.println("現在の成績: " + playerWins + "勝" + computerWins + "敗" + draws + "分け");
        }
    }

    /**
     * 入力文字列を {@link Hand} に変換する。全角カタカナ(グー/チョキ/パー)に加え、
     * ひらがな・英語(大小問わず)・頭文字(r/s/p)も受け付ける。
     * @param input 標準入力から読み取った1行
     * @return 対応するHand、いずれにも一致しない場合はnull
     */
    private static Hand parseHand(String input) {
        // 英語表記は大小文字を区別しないよう小文字化する(カタカナ・ひらがなは影響を受けない)。
        String normalized = input.toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "グー", "ぐー", "rock", "r" -> Hand.ROCK;
            case "チョキ", "ちょき", "scissors", "s" -> Hand.SCISSORS;
            case "パー", "ぱー", "paper", "p" -> Hand.PAPER;
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
