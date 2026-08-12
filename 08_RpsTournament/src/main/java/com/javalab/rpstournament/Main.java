package com.javalab.rpstournament;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;

/**
 * 対話式CLIじゃんけん大会シミュレーターのエントリーポイント。
 * 標準入力からコマンド({@code register}/{@code start})を1行ずつ読み取り、結果を表示する。
 * {@code exit} で終了する。
 */
public class Main {

    public static void main(String[] args) {
        Random random = new Random();
        run(new Scanner(System.in), System.out, player -> Hand.values()[random.nextInt(Hand.values().length)]);
    }

    /**
     * REPLループ本体。{@link Scanner}/{@link PrintStream}に加え、各プレイヤーの手を決定する処理も
     * {@link Function} として受け取ることで、テストから固定の手を注入できるようにしている。
     * @param scanner 入力読み取り元
     * @param out 出力先
     * @param handSupplier プレイヤーの手を決定する処理(本番では乱数、テストでは固定値)
     */
    static void run(Scanner scanner, PrintStream out, Function<Player, Hand> handSupplier) {
        List<Player> players = new ArrayList<>();

        out.println("じゃんけん大会シミュレーターへようこそ。コマンド: register <名前...> / start / exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                handleCommand(players, line, out, handSupplier);
            } catch (TournamentException | IllegalArgumentException e) {
                // 不正な人数・不明なコマンドはクラッシュさせず、エラー表示して次の入力へ進む。
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    /**
     * 1行分のコマンドを解釈して実行する。
     * @param players 登録済みプレイヤー一覧
     * @param line 入力行
     * @param out 出力先
     * @param handSupplier プレイヤーの手を決定する処理
     * @throws TournamentException 参加人数が不正な場合
     * @throws IllegalArgumentException コマンドが不明な場合
     */
    private static void handleCommand(
            List<Player> players, String line, PrintStream out, Function<Player, Hand> handSupplier) {
        if (line.equals("start")) {
            Tournament tournament = new Tournament(players);
            Player champion = tournament.runTournament(handSupplier);
            out.println("優勝: " + champion.getName());
        } else if (line.startsWith("register ")) {
            String[] names = line.substring(9).trim().split("\\s+");
            for (String name : names) {
                players.add(new Player(name));
            }
        } else {
            throw new IllegalArgumentException("不明なコマンドです: " + line);
        }
    }
}
