package com.javalab.junitpractice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link OrderNotificationService} を{@link EmailSender}/{@link EmailValidator}の
 * Mockitoモックを使って検証するテスト。
 * このプロジェクトの他の学習フォルダは基本的に実オブジェクト(実DB・実サーバー等)でテストしているが、
 * メール送信は実行すると外部への副作用が大きく実リソースでのテストに向かないため、
 * あえてMockitoを導入して「モックでしか検証できないもの」を学ぶ題材にしている。
 * 実オブジェクトによる統合テストは{@link MainTest}が担当する。
 *
 * <p>JUnit 5 / Mockitoの機能デモを兼ねており、{@code @Nested}でシナリオごとに構造化し、
 * {@code @ParameterizedTest}(CsvSource/ValueSource)・{@code assertAll}・
 * {@code assertThrows}の戻り値検証・{@code @Tag}/{@code @Disabled}/{@code assumeTrue}、
 * Mockitoの{@code ArgumentCaptor}/{@code when().thenReturn()}/{@code @InjectMocks}/{@code InOrder}を
 * それぞれ最低1箇所は使うようにしている。</p>
 */
@Tag("mockito")
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderNotificationService")
class OrderNotificationServiceTest {

    @Mock
    private EmailSender emailSender;

    @Mock
    private EmailValidator emailValidator;

    // @InjectMocksはコンストラクタ引数と同じ型の@Mockフィールド(emailSender, emailValidator)を
    // 自動的に注入してくれる。手動でnewするより配線ミスが起きにくい。
    @InjectMocks
    private OrderNotificationService service;

    @BeforeAll
    static void beforeAll() {
        // クラス全体で1度だけ実行されるセットアップ(ライフサイクルのデモ)。
        // このテストクラスでは共有すべき重い初期化がないため、ログ出力のみ行う。
        System.out.println("[OrderNotificationServiceTest] テストクラスを開始します");
    }

    @BeforeEach
    void setUp() {
        // 各テストの直前に、デフォルトでは「メールアドレスは常に正しい形式」として扱う
        // (形式検証そのものを検証するテストでは個別にwhen()で上書きする)。
        // Mockitoのwhen().thenReturn()による戻り値のスタブ設定。
        // 合計金額0円以下のテストではemailValidator.isValid()自体が呼ばれずこのスタブが
        // 使われないため、Mockitoの厳格スタブ検証(未使用スタブをエラーにする)に
        // 引っかからないようlenient()を付けている。
        lenient().when(emailValidator.isValid(anyString())).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        // 各テストの直後に実行される後始末(ライフサイクルのデモ)。
        // モックはMockitoExtensionが自動リセットするため、ここでは特に何もしない。
    }

    @Nested
    @DisplayName("正常に送信できるケース")
    class SendSuccessfully {

        @Test
        @DisplayName("宛先・件名・本文が期待通りに組み立てられる")
        void sendsEmailWithCorrectArguments() {
            Order order = new Order(1L, "alice@example.com", new BigDecimal("1000"));

            NotificationOutcome outcome = service.notifyOrderConfirmed(order);

            // ArgumentCaptorで実際に渡された引数を捕捉し、まとめて検証する
            // (eq()による個別一致検証とは異なり、捕捉した値を後から自由に加工・検査できる)。
            ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
            verify(emailSender).send(eq("alice@example.com"), subjectCaptor.capture(), bodyCaptor.capture());
            assertAll("送信結果と本文の内容",
                    () -> assertEquals(NotificationOutcome.SENT, outcome),
                    () -> assertTrue(subjectCaptor.getValue().contains("注文番号: 1")),
                    () -> assertTrue(bodyCaptor.getValue().contains("1000円")));
        }

        @ParameterizedTest(name = "[{index}] 合計{0}円 -> {1}")
        @DisplayName("合計金額に応じて送信/スキップが切り替わる")
        @CsvSource({
                "1000, SENT",
                "1, SENT",
                "0, SKIPPED_NON_POSITIVE_TOTAL",
                "-500, SKIPPED_NON_POSITIVE_TOTAL"
        })
        void outcomeDependsOnTotalAmount(String total, NotificationOutcome expected) {
            Order order = new Order(1L, "alice@example.com", new BigDecimal(total));

            NotificationOutcome outcome = service.notifyOrderConfirmed(order);

            assertEquals(expected, outcome);
        }
    }

    @Nested
    @DisplayName("送信をスキップするケース")
    class SkipSending {

        @Test
        @DisplayName("合計金額0円以下の注文はメール送信自体が行われない")
        void skipsSendingForOrderWithNonPositiveTotal() {
            Order order = new Order(3L, "carol@example.com", BigDecimal.ZERO);

            NotificationOutcome outcome = service.notifyOrderConfirmed(order);

            assertAll(
                    () -> assertEquals(NotificationOutcome.SKIPPED_NON_POSITIVE_TOTAL, outcome),
                    () -> verify(emailSender, never()).send(anyString(), anyString(), anyString()));
        }

        @Test
        @DisplayName("メールアドレスの形式が不正な注文はメール送信自体が行われない")
        void skipsSendingForOrderWithInvalidEmail() {
            // このテストではメールアドレスが不正なケースを再現するため、
            // @BeforeEachで設定したデフォルトのスタブ(常にtrue)をfalseで上書きする。
            when(emailValidator.isValid("invalid-email")).thenReturn(false);
            Order order = new Order(4L, "invalid-email", new BigDecimal("1000"));

            NotificationOutcome outcome = service.notifyOrderConfirmed(order);

            assertAll(
                    () -> assertEquals(NotificationOutcome.SKIPPED_INVALID_EMAIL, outcome),
                    () -> verify(emailSender, never()).send(anyString(), anyString(), anyString()));
        }

        @Disabled("RegexEmailValidatorは長さ上限を検証しない簡易実装のため、254文字超のメールアドレスを"
                + "拒否する仕様が未実装(#159で導入した簡易バリデーションの既知の制約。将来課題)")
        @Test
        @DisplayName("254文字を超える長大なメールアドレスは拒否される(未実装)")
        void rejectsExcessivelyLongEmail() {
            String tooLong = "a".repeat(250) + "@example.com";
            Order order = new Order(5L, tooLong, new BigDecimal("1000"));

            NotificationOutcome outcome = service.notifyOrderConfirmed(order);

            assertEquals(NotificationOutcome.SKIPPED_INVALID_EMAIL, outcome);
        }
    }

    @Nested
    @DisplayName("送信に失敗するケース")
    class SendFailure {

        @Test
        @DisplayName("EmailSenderの失敗をOrderNotificationExceptionへラップして再スローする")
        void wrapsEmailSenderFailureIntoOrderNotificationException() {
            // doThrow().when(...)で、emailSender.send()が呼ばれたら例外を投げるよう設定する
            // (Mockitoでモックに「例外を投げさせる」ためのスタブ設定)。
            Order order = new Order(2L, "bob@example.com", new BigDecimal("500"));
            doThrow(new RuntimeException("SMTP接続に失敗しました"))
                    .when(emailSender).send(anyString(), anyString(), anyString());

            // assertThrowsの戻り値(実際に投げられた例外インスタンス)を使い、
            // メッセージと原因(cause)の両方まで検証する。
            OrderNotificationException exception =
                    assertThrows(OrderNotificationException.class, () -> service.notifyOrderConfirmed(order));

            assertAll(
                    () -> assertTrue(exception.getMessage().contains("注文番号: 2")),
                    () -> assertEquals("SMTP接続に失敗しました", exception.getCause().getMessage()));
        }
    }

    @Nested
    @DisplayName("複数注文の一括送信")
    class BatchNotification {

        @Test
        @DisplayName("スキップされる注文があっても残りは送信される")
        void sendsOnlyForOrdersWithPositiveTotal() {
            List<Order> orders = List.of(
                    new Order(1L, "alice@example.com", new BigDecimal("1000")),
                    new Order(2L, "bob@example.com", BigDecimal.ZERO),
                    new Order(3L, "carol@example.com", new BigDecimal("500")));

            BatchNotificationResult result = service.notifyOrders(orders);

            assertAll("バッチ送信結果",
                    () -> assertEquals(2, result.sentCount()),
                    () -> assertEquals(1, result.skippedCount()),
                    () -> assertTrue(result.failures().isEmpty()));
        }

        @Test
        @DisplayName("1件が送信失敗しても残りの注文は送信される(部分失敗の集約)")
        void continuesSendingAfterOneFailure() {
            // 修正前はnotifyOrdersが1件目の失敗で例外を伝播し、2件目以降が送信されなかった。
            Order failing = new Order(1L, "alice@example.com", new BigDecimal("1000"));
            Order succeeding = new Order(2L, "bob@example.com", new BigDecimal("500"));
            doThrow(new RuntimeException("SMTP接続に失敗しました"))
                    .when(emailSender).send(eq("alice@example.com"), anyString(), anyString());

            BatchNotificationResult result = service.notifyOrders(List.of(failing, succeeding));

            assertAll("部分失敗を集約した結果",
                    () -> assertEquals(1, result.sentCount()),
                    () -> assertEquals(0, result.skippedCount()),
                    () -> assertEquals(1, result.failures().size()),
                    () -> verify(emailSender).send(eq("bob@example.com"), anyString(), anyString()));
        }

        @Test
        @DisplayName("登録順(リストの順序)通りに送信される")
        void sendsInListOrder() {
            List<Order> orders = List.of(
                    new Order(1L, "alice@example.com", new BigDecimal("1000")),
                    new Order(2L, "bob@example.com", new BigDecimal("500")));

            service.notifyOrders(orders);

            // InOrderで呼び出し順序そのものを検証する(通常のverify()は回数・引数のみで順序は見ない)。
            InOrder inOrder = inOrder(emailSender);
            inOrder.verify(emailSender).send(eq("alice@example.com"), anyString(), anyString());
            inOrder.verify(emailSender).send(eq("bob@example.com"), anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("環境依存で条件付き実行するテスト(assumeTrueのデモ)")
    class ConditionalExecution {

        @Test
        @DisplayName("システムプロパティで有効化されている場合のみ実行する")
        void runsOnlyWhenExplicitlyEnabled() {
            // assumeTrue()は条件を満たさない場合、失敗ではなく「スキップ」としてテストを打ち切る。
            // CIで時間のかかる/環境依存のテストだけ選択的に実行する際によく使われるパターンを示す
            // (このプロジェクトではデフォルトで無効なため、通常の実行では毎回スキップされる)。
            Assumptions.assumeTrue(Boolean.getBoolean("junitpractice.runExtraChecks"),
                    "システムプロパティ junitpractice.runExtraChecks=true の場合のみ実行");

            Order order = new Order(1L, "alice@example.com", new BigDecimal("1000"));
            NotificationOutcome outcome = service.notifyOrderConfirmed(order);

            assertEquals(NotificationOutcome.SENT, outcome);
        }
    }
}
