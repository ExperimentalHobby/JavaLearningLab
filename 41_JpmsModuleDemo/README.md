# モジュールシステム入門(JPMS)

## 学習ポイント
Javaモジュールシステム(JPMS)、module-info.javaによる公開範囲制御

## 概要
挨拶メッセージを生成するミニアプリを3つのJavaモジュールに分割した対話式CLIツール。
`greet <name>` / `exit`。

- **`greeting-api`**(module `com.javalab.jpmsmodule.api`): `Greeter`インターフェースを`exports`
- **`greeting-impl`**(module `com.javalab.jpmsmodule.impl`): `Greeter`の日本語版実装`JapaneseGreeter`を`exports`。
  内部ヘルパー`GreetingFormatter`は`com.javalab.jpmsmodule.impl.internal`パッケージに置き、**意図的に`exports`しない**
- **`app`**(module `com.javalab.jpmsmodule.app`): 両モジュールに`requires`し、`Main`(REPL)が`JapaneseGreeter`を使う

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
- `greeting-api`パッケージ(`com.javalab.jpmsmodule.api`)にクラスが1つも無い状態で`exports`を宣言すると、
  「パッケージは空であるか、または存在しません」というコンパイルエラーになる。`exports`は実際に中身のある
  パッケージにしか使えないことにハマった(`Greeter`インターフェースを追加してから解消)。
- Mavenのリアクタービルド(`mvn test`を`41_JpmsModuleDemo/`直下で実行)により、3モジュール間の依存解決が
  自動で行われる。個別のサブモジュールディレクトリ(例: `app/`)で単独に`mvn test`を実行すると、
  兄弟モジュールがローカルリポジトリに無くビルドエラーになる点に注意(必ず親ディレクトリから実行すること)。
- `greeting-impl`のテスト(`GreetingFormatterTest`)は非公開パッケージ(`internal`)のクラスを直接検証している。
  同一モジュール内のテストコードからは、他モジュールへ`exports`していないパッケージでも通常通りアクセスできる
  (モジュール境界は「モジュール間」の話であり、モジュール内部では従来通り)。

## テスト
```bash
cd 41_JpmsModuleDemo
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
