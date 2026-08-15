# ログ収集&解析ツール(SLF4J+Logback)

## 学習ポイント
ロギング設計、ログレベル管理

## 概要
SLF4Jの薄いAPIとLogback実装固有のログレベル管理機能を対比しながら体験するコンソールアプリ。19_LogAnalyzerRegex(既存ログファイルのテキスト解析)とは逆に、アプリ自身が設計に沿ってログを出力し、そのレベルを実行時に制御する側に焦点を当てている。

コマンド一覧:
- `run <job1,job2,...>` — カンマ区切りのジョブ名をバッチ処理し、成功/失敗/スキップの件数サマリーを表示する
- `level <ロガー名> <LEVEL>` — 指定ロガーのログレベルを実行時に変更する(`TRACE`/`DEBUG`/`INFO`/`WARN`/`ERROR`など)
- `exit` — 終了する

## 実行方法
```bash
cd 28_LoggingTool
mvn compile
mvn exec:java -Dexec.mainClass="com.javalab.loggingtool.Main"
```

## 実装メモ
- `BatchJobRunner`はSLF4Jの`Logger`のみに依存し、ログレベルの使い分けを実践した: 開始/完了は`INFO`、各ジョブの処理経過は`DEBUG`、空文字ジョブのスキップは`WARN`、`FAIL_`始まりのジョブ失敗は例外情報付きで`ERROR`。
- `LogLevelController`はSLF4Jではなく`ch.qos.logback.classic.Logger`/`Level`を直接扱う。SLF4JのAPIだけでは実行時のログレベル変更ができない(ファサードとしてバインディングの詳細を隠しているため)ことを踏まえ、Logback固有の機能をあえて切り出すことで両者の役割の違いを明示した。
- `Logger.getLevel()`は「そのロガー自身に明示設定された」レベルのみを返し、親から継承した実効レベルは含まない(継承分は`getEffectiveLevel()`が担う)。この違いを`LogLevelControllerTest`で明示的に検証した。
- テストは全てLogback標準の`ListAppender`を実際のLoggerに取り付けて実ログイベントを検証しており、モックは使用していない(他の学習フォルダと同じ「実リソースで確認する」方針を踏襲)。
- `logback.xml`はコンソールAppenderと日次ローリングのファイルAppender(`logs/app.log`)を併用する構成にした。`logs/`はビルド成果物と同様に`.gitignore`対象に追加した。

## テスト
```bash
cd 28_LoggingTool
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
