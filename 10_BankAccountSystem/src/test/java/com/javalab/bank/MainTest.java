package com.javalab.bank;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream)} のREPLループを結合テストするクラス。
 * 入力の1行目は口座名義人名として扱われる仕様のため、各シナリオの先頭に"Alice"を含めている。
 */
class MainTest {

    @Test
    void invalidAmountShowsErrorAndContinuesWithoutCrashing() {
        // "-100"は0以下のためInvalidAmountExceptionになる不正な入金額。
        // エラー表示のみで継続し、直後の正常な "deposit 1000" が処理されることを確認する。
        Scanner scanner = new Scanner(new StringReader("Alice\ndeposit -100\ndeposit 1000\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
    }

    @Test
    void depositWithdrawBalanceSequenceShowsCorrectBalance() {
        // 1000入金→300出金という一連の操作後、balanceコマンドで残高700が表示されることを確認する。
        Scanner scanner = new Scanner(new StringReader("Alice\ndeposit 1000\nwithdraw 300\nbalance\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("残高: 700"));
    }

    @Test
    void historyCommandShowsDepositAndWithdrawalWithBalanceSnapshot() {
        // 1000入金→300出金の後、historyコマンドで各取引の種別・金額・取引後残高が
        // 「種別 金額 (残高: 残高)」の形式で表示されることを確認する。
        Scanner scanner = new Scanner(new StringReader("Alice\ndeposit 1000\nwithdraw 300\nhistory\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("DEPOSIT 1000 (残高: 1000)"));
        assertTrue(output.contains("WITHDRAWAL 300 (残高: 700)"));
    }
}
