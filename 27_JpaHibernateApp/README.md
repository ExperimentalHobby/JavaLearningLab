# 簡易ORM/DB操作アプリ(JPA/Hibernate)

## 学習ポイント
アノテーションベースのDB連携

## 概要
JPA(Jakarta Persistence)アノテーションを付与した`Product`エンティティを、Hibernateを介してH2組み込みインメモリDBに永続化する在庫管理CRUDコンソールアプリ。生SQLを書かず、アノテーションとEntityManagerのAPIだけでCRUD操作を組み立てる体験に主眼を置いている。

コマンド一覧:
- `add <商品名> <価格> <在庫数>` — 商品を新規登録する(IDは自動採番)
- `list` — 全商品を一覧表示する
- `find <id>` — IDを指定して1件取得する
- `search <商品名>` — 商品名が完全一致する商品を検索する(`@NamedQuery`によるJPQLパラメータバインド)
- `update <id> <価格> <在庫数>` — 既存商品の価格・在庫数を更新する
- `delete <id>` — 指定IDの商品を削除する
- `exit` — 終了する

## 実行方法
```bash
cd 27_JpaHibernateApp
mvn compile
mvn exec:java -Dexec.mainClass="com.javalab.jpahibernate.Main"
```

## 実装メモ
- `Product`は`@Entity`/`@Table`/`@Id`/`@GeneratedValue`/`@Column`をフィールドに直接付与するアノテーションベースのマッピングとした。JPA仕様上、エンティティにはpublic/protectedの引数無しコンストラクタが必須なため、`protected Product()`を用意しつつ、通常利用は3引数コンストラクタに限定した。
- テストは全て実際のH2インメモリDBに対して行い、モックは使用していない(他の学習フォルダと同じ「実リソースで確認する」方針を踏襲)。`@BeforeEach`でテストごとに新しい`EntityManagerFactory`を生成し、`@AfterEach`でクローズすることでテスト間のデータ汚染を防いでいる。

### コードレビュー指摘への対応(Issue #161)
- **トランザクションのロールバック処理がない問題 + `EntityManager`を使い回している問題**: 関連が深いため一緒に解決した。`ProductRepository`は`EntityManagerFactory`を受け取る設計に変更し、操作ごとに`createEntityManager()`→処理→`close()`する共通ヘルパー(`inTransaction`/`withEntityManager`)を導入した。`inTransaction`は例外時に`rollback()`を呼んでから再スローするようにした(修正前は`commit()`失敗時に`EntityTransaction`がactiveのまま残り、以降の全操作が「Transaction already active」で失敗していた)。テストでは、`name`が`NOT NULL`制約違反になる商品を保存して意図的に失敗させた後、後続の操作が正常に継続できることを確認している。
- **`persistence.xml`がインメモリDB固定でデータが消える問題**: メイン用(`src/main/resources`)はファイルDB(`jdbc:h2:file:./data/productdb`、`hbm2ddl.auto=update`)に変更した。テスト用は`src/test/resources`に別の永続化ユニット名(`productPU-test`)で配置し、高速・分離されたインメモリDB(`create-drop`)を使う(`44_JpaRestApi`と同じパターン)。JPAは`ClassLoader.getResources()`でクラスパス上の全`persistence.xml`を走査するため、Spring Bootの`application.properties`のような単純な上書きにはならず、同名の永続化ユニットが複数あると警告になる点に注意した(そのため名前を分けている)。
- **`Product`に`equals`/`hashCode`がない問題**: JPAエンティティのベストプラクティスに沿い、`equals`はid基準(idがnullなら同一インスタンスでない限り等しいとみなさない)、`hashCode`は`getClass().hashCode()`でid採番前後を通じて不変にした。
- **`handleUpdate`がトランザクション外でmanagedエンティティを書き換える問題**: 上記のEntityManager分離の副次効果として解消した。`findById`が返す`Product`は取得元の`EntityManager`が既にcloseされているため自動的にdetachedとなり、`repository.update()`(内部で`merge()`)が必要な理由が自明になった。
- **引数不足で英語の内部例外メッセージが表示される問題**: `add`コマンドの引数個数を事前検証し、「使用方法: add <商品名> <価格> <在庫数>」と表示するようにした。
- **学習テーマ「名前検索・ページング・`@NamedQuery`が未実装」への対応**: `Product`に`@NamedQuery(name = "Product.findByName", ...)`を定義し、`ProductRepository.findByName()`でJPQLのパラメータバインド(`:name`)を実践した。`findAll(offset, limit)`で`setFirstResult`/`setMaxResults`によるページングも追加した。CLIには`search <商品名>`コマンドを追加した。

## テスト
```bash
cd 27_JpaHibernateApp
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
