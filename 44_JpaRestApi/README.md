# DB連携REST API(Spring Boot + Spring Data JPA)

## 学習ポイント
Spring Data JPA、レイヤードアーキテクチャ(Controller→Service→Repository)、`@Transactional`によるトランザクション管理

## 概要
商品在庫管理を題材に、Spring Data JPAで実際にDB(H2)へ永続化するCRUD REST API。

- `POST /api/products` — 商品登録(201)
- `GET /api/products` — 商品一覧取得
- `GET /api/products/{id}` — 単一商品取得(存在しなければ404)
- `PUT /api/products/{id}` — 商品の名前・価格・在庫を更新
- `DELETE /api/products/{id}` — 商品削除(204、存在しなければ404)
- `POST /api/orders` — 複数商品の在庫を一括で引き当てる注文API(在庫不足があれば409)

## 実装メモ
- 20_SpringBootApiIntro(REST APIだがインメモリ)と27_JpaHibernateApp(JPA永続化だがコンソールアプリ)は別々に練習していたため、本課題で「DBに永続化するWeb APIサーバー」を一本通しで実践した。
- `Product`(JPAエンティティ)をAPIに直接晒さず、`ProductRequest`/`ProductResponse`(DTO)を介して変換する設計にした。永続化モデルとAPIモデルを分離することで、将来DBスキーマとAPI仕様が独立して変更できるようにしている。
- `ProductService`はクラスレベルで`@Transactional`を付与した。特に`fulfillOrder`(複数商品の在庫を一括引き当てる注文API)は、途中の商品で在庫不足があれば`InsufficientStockException`をスローし、**それより前に引き当てた在庫の減算も含めて全てロールバックされる**ことをテストで実証した。
- ロールバックのテスト(`OrderFulfillmentTest`)には重要な注意点があった。テストクラスに`@Transactional`を付けると、`fulfillOrder`内の例外がテスト全体のトランザクション(参加トランザクション)をrollback-onlyにマークしてしまい、その後の検証用`findById`呼び出しが正常終了しようとした際に`UnexpectedRollbackException`で失敗する。そのため、このテストクラスはあえて`@Transactional`を付けず、各Serviceメソッド呼び出しを独立したトランザクションとして実行させ、実際にDBへコミット/ロールバックされた結果を検証している。
- Spring Boot 3系はデフォルトで`spring.jpa.open-in-view=true`だが、REST APIにはビュー描画がなく遅延ロードの温床になるだけなので、明示的に`false`に設定して起動時の警告を解消した。
- DBは開発時: H2ファイルDB(`./data/`、`.gitignore`対象)、テスト時: H2インメモリDB(`create-drop`)を使い分けている。実DBMS(PostgreSQL)によるテストはTestcontainers採用回(#109)で別途扱う。
- `ProductController`/`OrderController`のテストは`@SpringBootTest(webEnvironment = RANDOM_PORT)` + `TestRestTemplate`で実際に埋め込みTomcat+実H2へアクセスする結合テストとした(`MockMvc`は使わない、既存Issueと同じ「実リソースでのテスト」方針)。

## テスト
```bash
cd 44_JpaRestApi
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
