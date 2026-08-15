# 簡易ORM/DB操作アプリ(JPA/Hibernate)

## 学習ポイント
アノテーションベースのDB連携

## 概要
JPA(Jakarta Persistence)アノテーションを付与した`Product`エンティティを、Hibernateを介してH2組み込みインメモリDBに永続化する在庫管理CRUDコンソールアプリ。生SQLを書かず、アノテーションとEntityManagerのAPIだけでCRUD操作を組み立てる体験に主眼を置いている。

コマンド一覧:
- `add <商品名> <価格> <在庫数>` — 商品を新規登録する(IDは自動採番)
- `list` — 全商品を一覧表示する
- `find <id>` — IDを指定して1件取得する
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
- 永続化ユニット(`persistence.xml`)はH2インメモリDB(`jdbc:h2:mem:productdb`)を指し、`hibernate.hbm2ddl.auto=create-drop`によりテスト・実行のたびにスキーマが作り直されるようにした。16_JdbcCrudAppの生SQLベースの実装と対比できるよう、あえて別のDB(SQLite→H2)を採用した。
- `ProductRepository`はコンストラクタで受け取った`EntityManager`をそのまま使い回し、`save`/`update`/`deleteById`では明示的にトランザクションを開始・コミットしている(`RESOURCE_LOCAL`のためコンテナ管理トランザクションではなく手動管理が必要)。
- テストは全て実際のH2インメモリDBに対して行い、モックは使用していない(他の学習フォルダと同じ「実リソースで確認する」方針を踏襲)。`@BeforeEach`でテストごとに新しい`EntityManagerFactory`を生成し、`@AfterEach`でクローズすることでテスト間のデータ汚染を防いでいる。

## テスト
```bash
cd 27_JpaHibernateApp
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
