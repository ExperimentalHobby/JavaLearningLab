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
java -jar target/mavenclipackaging-1.0-SNAPSHOT.jar --version
```

## 実装メモ
- **依存関係管理**: 引数解析に`info.picocli:picocli`を採用した。REPL系の課題では標準入力を`Scanner`で自前パースしていたが、picocliの`@Command`/`@Parameters`/`@Option`アノテーションによる宣言的な定義と比較できるようにした。
- **JARビルド**: `maven-shade-plugin`を`package`フェーズにバインドし、picocli本体を含む実行可能fat jar(`java -jar`単体で動く成果物)を生成する設定にした。`ManifestResourceTransformer`で`Main-Class`をマニフェストに書き込んでいる。
- **pom.xml管理**: `src/main/resources/app.properties`に`version=${project.version}`と記述し、`<resources><resource><filtering>true</filtering>...`でMavenのresource filteringを有効化した。ビルド時にpom.xmlの`<version>`(1.0-SNAPSHOT)へ実際に置換されることを、テスト(`--version`実行結果の検証)とビルド成果物の手動実行の両方で確認した。
- **テストでの出力キャプチャ**: picocliのコマンドは標準出力へ直接書くのではなく、`@Spec CommandSpec spec`経由で`spec.commandLine().getOut()`/`getErr()`に出力する設計にした。これによりテストから`CommandLine#setOut(PrintWriter)`で出力を差し替え、モックなしで実際の出力内容を検証できる。
- 全テストはpicocliの`CommandLine`を実際に実行し、`@TempDir`で作成した実ファイルに対して行っている。モックは使用していない。

## テスト
```bash
cd 30_MavenCliPackaging
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
