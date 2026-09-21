package com.javalab.bank;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * 対話式CLI銀行口座システムのエントリーポイント。
 * 起動時に口座名義人名を入力し、標準入力からコマンド({@code deposit}/{@code withdraw}/
 * {@code balance}/{@code history})を1行ずつ読み取って結果を表示する。{@code exit} で終了する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    /**
     * REPLループ本体。{@link Scanner}/{@link PrintStream} を引数として受け取ることで、
     * テストから {@code StringReader}/{@code ByteArrayOutputStream} を注入できるようにしている。
     * @param scanner 入力読み取り元
     * @param out 出力先
     */
    static void run(Scanner scanner, PrintStream out) {
        out.println("口座名義人名を入力してください:");
        String ownerName = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        Account account = new Account(ownerName);

        out.println("コマンド: deposit <金額> / withdraw <金額> / balance / history / exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                handleCommand(account, line, out);
            } catch (NumberFormatException e) {
                out.println("エラー: 数値の形式が不正です");
            } catch (BankAccountException | IllegalArgumentException e) {
                // 不正な金額・残高不足・不明なコマンドはクラッシュさせず、エラー表示して次の入力へ進む。
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    /**
     * 1行分のコマンドを解釈して実行する。
     * @param account 操作対象の口座
     * @param line 入力行
     * @param out 出力先
     * @throws BankAccountException 入出金額が不正、または残高不足の場合
     * @throws IllegalArgumentException コマンドが不明な場合
     */
    private static void handleCommand(Account account, String line, PrintStream out) {
        if (line.equals("balance")) {
            out.println("残高: " + format(account.getBalance()));
        } else if (line.equals("history")) {
            printHistory(account, out);
        } else if (line.startsWith("deposit ")) {
            account.deposit(new BigDecimal(line.substring(8).trim()));
        } else if (line.startsWith("withdraw ")) {
            account.withdraw(new BigDecimal(line.substring(9).trim()));
        } else {
            throw new IllegalArgumentException("不明なコマンドです: " + line);
        }
    }

    private static void printHistory(Account account, PrintStream out) {
        List<Transaction> history = account.getHistory();
        if (history.isEmpty()) {
            out.println("取引履歴はありません");
            return;
        }
        for (Transaction transaction : history) {
            out.println(transaction.timestamp() + " " + transaction.type() + " " + format(transaction.amount())
                    + " (残高: " + format(transaction.balanceAfter()) + ")");
        }
    }

    /**
     * 表示用に整形する。スケール2に正規化された金額をそのまま表示すると
     * {@code 1000.00}のように冗長になるため、末尾の余分なゼロを取り除く。
     * @param value 整形対象の値
     * @return 整形後の文字列
     */
    private static String format(BigDecimal value) {
        // BigDecimal.ZERO.stripTrailingZeros() は "0E-10" のような表記になり得るため、0は個別に扱う。
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
    }
}
