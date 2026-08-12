package com.javalab.rpn;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Stack;

/**
 * 逆ポーランド記法(RPN)の数式を{@link Stack}で評価する電卓ロジック。
 * 数値は{@link BigDecimal}で扱い、除算による丸め誤差を避ける。
 */
public class RpnCalculator {

    // 割り切れない除算でも例外にならないよう、スケール10で丸める。
    private static final int DIVISION_SCALE = 10;

    /**
     * RPN形式の数式を評価する。
     * トークンを先頭から順にStackへpushし、演算子が来るたびに直近2つをpopして計算・pushし直す
     * (例: {@code "3 4 +"} → 3をpush→4をpush→"+"で2つpopして7をpush)。
     * @param expression 空白区切りのRPN形式の数式(例: {@code "3 4 +"})
     * @return 評価結果
     * @throws RpnCalculatorException 不正なトークン・オペランド不足・不正な式の場合
     * @throws ArithmeticException ゼロ除算の場合
     */
    public static BigDecimal evaluate(String expression) {
        Stack<BigDecimal> stack = new Stack<>();
        String[] tokens = expression.split("\\s+");
        for (String token : tokens) {
            if (isOperator(token)) {
                BigDecimal b = popOperand(stack);
                BigDecimal a = popOperand(stack);
                stack.push(applyOperator(token, a, b));
            } else {
                stack.push(parseOperand(token));
            }
        }
        // 正しいRPN式は評価後にスタックへ結果が1つだけ残る。
        // 2つ以上残る場合は演算子が不足しており、0の場合は空の式である。
        if (stack.size() != 1) {
            throw new RpnCalculatorException("不正な式です: " + expression);
        }
        return stack.peek();
    }

    private static BigDecimal parseOperand(String token) {
        try {
            return new BigDecimal(token);
        } catch (NumberFormatException e) {
            throw new RpnCalculatorException("不正なトークンです: " + token);
        }
    }

    private static BigDecimal popOperand(Stack<BigDecimal> stack) {
        if (stack.isEmpty()) {
            throw new RpnCalculatorException("オペランドが不足しています");
        }
        return stack.pop();
    }

    private static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    private static BigDecimal applyOperator(String operator, BigDecimal a, BigDecimal b) {
        return switch (operator) {
            case "+" -> a.add(b);
            case "-" -> a.subtract(b);
            case "*" -> a.multiply(b);
            case "/" -> a.divide(b, DIVISION_SCALE, RoundingMode.HALF_UP);
            default -> throw new IllegalStateException("未対応の演算子です: " + operator);
        };
    }
}
