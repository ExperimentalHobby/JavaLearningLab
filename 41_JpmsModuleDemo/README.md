# モジュールシステム入門(JPMS)

## 学習ポイント
Javaモジュールシステム(JPMS)、module-info.javaによる公開範囲制御

## 概要
挨拶メッセージを生成するミニアプリを3つのJavaモジュールに分割した対話式CLIツール。
`greet <name>` / `exit`。

- **`greeting-api`**(module `com.javalab.jpmsmodule.api`): `Greeter`インターフェースを`exports`
- **`greeting-impl`**(module `com.javalab.jpmsmodule.impl`): `Greeter`の日本語版実装`JapaneseGreeter`を
  `provides com.javalab.jpmsmodule.api.Greeter with com.javalab.jpmsmodule.impl.JapaneseGreeter;`で
  `ServiceLoader`向けに提供する。**`exports`は一切行わない**(`impl`パッケージも`internal`パッケージも非公開)
- **`app`**(module `com.javalab.jpmsmodule.app`): `greeting-api`に`requires`し、`ServiceLoader.load(Greeter.class)`で
  実装を取得する(`JapaneseGreeter`を型として直接参照しない)

このフォルダのみ、JPMSを実演するためのローカルなMaven親子構成(アグリゲータpom + 3子モジュール)を取る
(リポジトリ全体で共通親を持たない方針とは矛盾しない、あくまでこのフォルダ内に閉じた構成)。

## 実行方法
```bash
cd 41_JpmsModuleDemo
mvn compile
# --module-pathの区切り文字はOSに依存する(Windows: ; / macOS・Linux: :)
java --module-path "app/target/classes;greeting-api/target/classes;greeting-impl/target/classes" \
  --module com.javalab.jpmsmodule.app/com.javalab.jpmsmodule.app.Main
```

## 実装メモ
- `greeting-impl`の`internal`パッケージを`exports`しないことで、`app`が`greeting-impl`に`requires`していても
  `GreetingFormatter`には一切アクセスできないことを実際に確認した。`app`側で試しに
  `import com.javalab.jpmsmodule.impl.internal.GreetingFormatter;`を追加して`mvn compile`を実行すると、
  以下のコンパイルエラーになることを確認済み(確認後はimportを削除して元に戻した)。
  ```
  パッケージcom.javalab.jpmsmodule.impl.internalは表示不可です
  (パッケージcom.javalab.jpmsmodule.impl.internalはモジュールcom.javalab.jpmsmodule.implで宣言されていますが、
  エクスポートされていません)
  ```
  これがJPMSの核心的な価値で、従来のクラスパス方式では同じJAR内のpublicクラスは(パッケージが分かれていても)
  常にアクセス可能だったのに対し、JPMSでは`exports`していないパッケージは**同じ依存関係を持つモジュールからでも
  物理的にアクセス不能**になる(強いカプセル化)。
- **`greeting-impl`は`impl`パッケージ自体も`exports`していない**(Issue #173での変更)。`JapaneseGreeter`は
  `ServiceLoader`の`provides ... with ...`でのみ提供され、公開されたパッケージが1つも無い状態でも
  サービス実装として機能する。試しに`app`側に`import com.javalab.jpmsmodule.impl.JapaneseGreeter;`を
  追加すると、`internal`パッケージのときと同様に「パッケージは表示不可です」のコンパイルエラーになる
  (`greeting-impl`は`app`から`requires`されている=モジュールとしては読めるが、パッケージが1つも
  `exports`されていないため、型としては一切参照できない)。
- `greeting-api`パッケージ(`com.javalab.jpmsmodule.api`)にクラスが1つも無い状態で`exports`を宣言すると、
  「パッケージは空であるか、または存在しません」というコンパイルエラーになる。`exports`は実際に中身のある
  パッケージにしか使えないことにハマった(`Greeter`インターフェースを追加してから解消)。
- Mavenのリアクタービルド(`mvn test`を`41_JpmsModuleDemo/`直下で実行)により、3モジュール間の依存解決が
  自動で行われる。個別のサブモジュールディレクトリ(例: `app/`)で単独に`mvn test`を実行すると、
  兄弟モジュールがローカルリポジトリに無くビルドエラーになる点に注意(必ず親ディレクトリから実行すること)。
- `greeting-impl`のテスト(`GreetingFormatterTest`)は非公開パッケージ(`internal`)のクラスを直接検証している。
  同一モジュール内のテストコードからは、他モジュールへ`exports`していないパッケージでも通常通りアクセスできる
  (モジュール境界は「モジュール間」の話であり、モジュール内部では従来通り)。

### コードレビュー指摘への対応(Issue #173)
- **JPMSの中核であるServiceLoader(`uses`/`provides`)が使われていなかった問題**: `app`が`greeting-impl`の
  `JapaneseGreeter`を`new`で直接生成しており、「APIモジュールだけに依存し実装は差し替え可能にする」という
  module-infoの主目的が実演できていなかった。`greeting-impl`のmodule-infoを
  `provides com.javalab.jpmsmodule.api.Greeter with com.javalab.jpmsmodule.impl.JapaneseGreeter;`に変更し
  (`exports`は削除)、`app`は`uses com.javalab.jpmsmodule.api.Greeter;`を宣言して
  `ServiceLoader.load(Greeter.class)`で実装を取得するようにした。
  - `app`のmodule-infoは`requires com.javalab.jpmsmodule.impl;`も残している。`uses`だけでも`java --module-path
    ... -m app/...`のように実行すればモジュールシステムが自動でサービス提供モジュールを解決・バインドするが、
    Maven Surefireがテストを実行する際は(後述の理由により)`requires`で明示的にモジュールグラフへ含めておく
    方が確実だったため。
  - **Maven Surefireの制約**: 実際に調査したところ、Maven Surefire(3.5.2)はテストクラスを**クラスパス
    (unnamed module)上で実行**しており、`module-info.java`の`provides`宣言だけでは`ServiceLoader`が
    プロバイダを発見できなかった(`ServiceLoader`はunnamed moduleから呼ばれると、モジュールベースではなく
    従来の`META-INF/services`方式のプロバイダ探索にフォールバックするため)。そのため`greeting-impl`に
    `src/main/resources/META-INF/services/com.javalab.jpmsmodule.api.Greeter`(中身は
    `com.javalab.jpmsmodule.impl.JapaneseGreeter`)も追加した。これはJPMS登場以前からある標準的な
    サービスプロバイダ形式で、多くの実務ライブラリが両方式を併用して`--module-path`実行と
    クラスパス実行の両方に対応している。`java --module-path`で実際に起動して`greet`コマンドが動くことも
    手動で確認済み(module-infoの`provides`が正しく機能している)。
- **学習テーマ「`requires transitive`/`opens`/自動モジュール」への対応**: いずれも意味のある実演には
  新しいモジュール・依存関係の追加が必要(`requires transitive`は4段目のモジュールが要る、`opens`は
  リフレクションで深くアクセスするフレームワーク側の消費者が要る、自動モジュールは非モジュール化jarへの
  依存が要る)。3つまとめて実装するとスコープが大きくなりすぎるため見送った。

## テスト
```bash
cd 41_JpmsModuleDemo
mvn test
```
**注意**: 上記の理由により、`ServiceLoader`が実際にプロバイダを発見できることは`GreeterServiceLoaderTest`
(`app`モジュール)で検証しているが、モジュール境界(`isExported`など)を`mvn test`実行時に検証することは
できない(unnamed module上では常に`true`になってしまうため)。モジュール境界の確認は上記「実装メモ」の
再現手順(実際にimportを追加してコンパイルエラーになることを確認する)で行う。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
