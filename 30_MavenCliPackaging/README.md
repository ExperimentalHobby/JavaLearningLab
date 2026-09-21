# CLIパッケージ化&配布(Mavenプロジェクト)

## 学習ポイント
pom.xml管理、JARビルド、依存関係管理

## 概要
テキストファイルの行数・単語数・文字数を数える`wc`相当のCLIツール。これまでの29個は全て`Scanner`によるREPL形式だったが、本課題は最終回として「OSシェルから直接実行する実行可能fat jar」という配布形態そのものを学習対象にした。引数解析には[picocli](https://picocli.info/)を利用し、`maven-shade-plugin`で依存ライブラリを同梱した単一jarにビルドする。

## 実行方法
```bash
cd 30_MavenCliPackaging
mvn clean package
java -jar target/mavenclipackaging-1.0-SNAPSHOT.jar <ファイルパス>
java -jar target/mavenclipackaging-1.0-SNAPSHOT.jar --format json <ファイルパス>
java -jar target/mavenclipackaging-1.0-SNAPSHOT.jar <ファイルパス1> <ファイルパス2>   # 複数ファイル一括指定(合計も表示)
cat <ファイルパス> | java -jar target/mavenclipackaging-1.0-SNAPSHOT.jar             # 標準入力から読み込み
java -jar target/mavenclipackaging-1.0-SNAPSHOT.jar --version
```

## 実装メモ
- **依存関係管理**: 引数解析に`info.picocli:picocli`を採用した。REPL系の課題では標準入力を`Scanner`で自前パースしていたが、picocliの`@Command`/`@Parameters`/`@Option`アノテーションによる宣言的な定義と比較できるようにした。
- **JARビルド**: `maven-shade-plugin`を`package`フェーズにバインドし、picocli本体を含む実行可能fat jar(`java -jar`単体で動く成果物)を生成する設定にした。`ManifestResourceTransformer`で`Main-Class`をマニフェストに書き込んでいる。
- **pom.xml管理**: `src/main/resources/app.properties`に`version=${project.version}`と記述し、`<resources><resource><filtering>true</filtering>...`でMavenのresource filteringを有効化した。ビルド時にpom.xmlの`<version>`(1.0-SNAPSHOT)へ実際に置換されることを、テスト(`--version`実行結果の検証)とビルド成果物の手動実行の両方で確認した。
- **テストでの出力キャプチャ**: picocliのコマンドは標準出力へ直接書くのではなく、`@Spec CommandSpec spec`経由で`spec.commandLine().getOut()`/`getErr()`に出力する設計にした。これによりテストから`CommandLine#setOut(PrintWriter)`で出力を差し替え、モックなしで実際の出力内容を検証できる。
- 全テストはpicocliの`CommandLine`を実際に実行し、`@TempDir`で作成した実ファイルに対して行っている。モックは使用していない。

### コードレビュー指摘への対応(Issue #164)
- **行数が1多く数えられる問題**: `content.split("\n", -1).length`は末尾が改行で終わる通常のテキストファイルで末尾の空要素を余分に数えてしまっていた(`"a\nb\n".split("\n", -1).length`は3だが本来2)。`wc -l`と同じ「改行文字(`\n`)の出現回数」で行数を数える方式に変更した。この結果、末尾に改行のないテキストの行数の数え方も`wc -l`に合わせて変わる(例: `"Hello world\nJava is fun"`は改行1個のため行数は1)。
- **CRLF改行が考慮されていない問題**: 改行を`\r\n`/`\r`→`\n`に正規化してから行数・文字数を計算するようにした。`\r`が文字数に含まれたり行末に残ったりしないようにしている。
- **`--format`の値が検証されない問題**: `enum OutputFormat { TEXT, JSON }`を導入し、専用の`ITypeConverter`(`OutputFormatConverter`)で検証するようにした。大文字小文字を区別せず`text`/`json`を受け付けつつ、`--format xml`のような未対応の値は`TypeConversionException`(picocliが終了コード2+分かりやすいメッセージに変換)で明示的にエラーにする。
- **UTF-8以外のファイルでスタックトレースが出る問題**: `Files.readString`が投げる`MalformedInputException`を捕捉し、`Files.exists`と同様に意味のあるエラーメッセージ+終了コード1で返すようにした。
- **学習テーマ「標準入力・複数ファイル未対応」への対応**: `@Parameters`を`Path`単数から`List<Path>`(0個以上)に変更した。引数省略時は標準入力から読み込み、複数ファイル指定時はファイルごとの結果に加え合計行を表示する(単一ファイル時の出力形式は既存テストとの互換性のため変更していない)。

## テスト
```bash
cd 30_MavenCliPackaging
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
