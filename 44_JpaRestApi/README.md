# DB連携REST API(Spring Boot + Spring Data JPA)

## 学習ポイント
Spring Data JPA、レイヤードアーキテクチャ(Controller→Service→Repository)、`@Transactional`によるトランザクション管理

## 概要
商品在庫管理を題材に、Spring Data JPAで実際にDB(H2)へ永続化するCRUD REST API。

- `POST /api/products` — 商品登録(201)
- `GET /api/products?name=...&page=0&size=10&sort=price,desc` — 商品一覧取得(ページネーション・ソート・名前の部分一致検索対応)
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

## Testcontainers(実PostgreSQLでの結合テスト、#109)
これまでのDB系テストはSQLite/H2インメモリで済ませていたが、`ProductRepositoryPostgresTest`は
Dockerコンテナで実際のPostgreSQLを起動し、`ProductService`が本物のDBMSに対しても正しく
動作することを検証する。`@DynamicPropertySource`でコンテナの接続情報にデータソースを差し替え、
44番のH2版と同じ「在庫不足時のロールバック」検証をPostgreSQLに対しても行っている。

- 既存のH2ベースのテストは維持し、開発時の手軽さ(H2)と本番相当DBでの検証(Testcontainers+PostgreSQL)を使い分ける設計にした
- Testcontainersのバージョンは`spring-boot-starter-parent`の依存関係管理に委ねている(Spring Boot 3.5系がバージョンを管理)
- `@DynamicPropertySource`で`spring.datasource.url`をPostgreSQLコンテナの接続情報に差し替えても、`src/test/resources/application.properties`がH2用の`spring.datasource.driver-class-name=org.h2.Driver`を固定指定しているため、URLだけ差し替えるとドライバがミスマッチし`Driver org.h2.Driver claims to not accept jdbcUrl`で失敗する。`driver-class-name`も`@DynamicPropertySource`側で`org.postgresql.Driver`に明示的に上書きすることで解決した(この不一致はCI上で実際に発生・修正した実例)。

**注記**: 作業環境にDocker Desktopが導入されていないため、ローカルでの`mvn test`実行はできない
(`IllegalStateException: Could not find a valid Docker environment`)。GitHub ActionsのCI環境
(ubuntu-latest)にはDockerが標準搭載されているため、実装後はCI上で実行し、実際のPostgreSQLコンテナに
対してテストが成功することを確認済み。

## ページネーション/検索・ソート(#110)
一覧取得API(`GET /api/products`)にページング・ソート・名前の部分一致検索を追加した。

- `PageResponse<T>`(record)でSpring Data JPAの`Page<T>`を独自DTOに変換してから返す(実装詳細を晒さない)
- `ProductRepository.findByNameContainingIgnoreCase(String, Pageable)`はメソッド名からのクエリ自動生成(Spring Data JPAのクエリメソッド命名規則)
- `ProductController`は`@PageableDefault(size = 10, sort = "id")`でデフォルト値を設定
- **テスト分離の注意点**: `OrderFulfillmentTest`/`ProductControllerTest`/`OrderControllerTest`は`@Transactional`を使わず実コミットするため、同じ共有H2インスタンス上にそれらが作成したデータが残る。`findAll`の総件数を検証するページングのテストは、この残留データの影響を受けて当初「期待5件・実際12件」のように失敗した。そこで各テストで一意なマーカー文字列(例: `PAGINGTEST3`)を商品名に含め、検索条件としてマーカーを指定することで「自分が作成したデータだけ」を対象に集計・検証するよう設計した。
- **URLエンコードの注意点**: 結合テストで日本語(「ノート」等)をクエリパラメータに含める際、`UriComponentsBuilder`で事前にURLエンコードした文字列を`TestRestTemplate.exchange(String url, ...)`にそのまま渡すと、内部でさらに符号化され二重符号化になりサーバー側で意図した文字列に復元できなかった(0件ヒットになる不具合として顕在化)。URIテンプレート(`"...?name={name}"`)+ 生の値を`uriVariables`として渡す方式に変更し、符号化をRestTemplateに一任することで解決した。

## API仕様書自動生成(#111)
`springdoc-openapi-starter-webmvc-ui`を導入し、`/v3/api-docs`(OpenAPI仕様のJSON)と
`/swagger-ui/index.html`(Swagger UI)を自動生成している。

- 依存関係を追加するだけで、既存の`@RestController`のアノテーションからゼロコードで仕様書が生成されることを`OpenApiDocsTest`(導入前は404、導入後は200になることを確認)で実証した
- `OpenApiConfig`(`OpenAPI` Bean)でAPI全体のタイトル・説明・バージョンをカスタマイズ
- `@Operation`(各エンドポイント)・`@Schema`(DTOの各フィールド)を付与し、生成される仕様書の可読性を高めた
- これまでREADMEに手書きしていたエンドポイント仕様(本READMEの「概要」セクションの箇条書き)を、コードから自動生成できることを実践した

## Spring Boot Actuator(#112)
`spring-boot-starter-actuator`を導入し、`/actuator/health`(ヘルスチェック)・`/actuator/metrics`
(メトリクス公開)を有効化した。

- DB接続そのものはSpring Boot標準の`db`ヘルスインジケータで既にカバーされるため、本課題では
  ビジネスロジックに基づくカスタム`HealthIndicator`(`LowStockHealthIndicator`)を実装した。
  在庫が閾値(`app.low-stock-threshold`、デフォルト5)未満の商品数を`lowStockCount`として
  `/actuator/health`のレスポンスに公開する
- 在庫不足はアプリケーションの死活とは別軸の情報のため、あえて常にUPとし、ステータスをDOWNには
  していない(参考情報として公開する設計)
- クラス名`LowStockHealthIndicator`の「HealthIndicator」を除いた「lowStock」が、レスポンスJSON上の
  コンポーネント名になる(Spring Bootの命名規則)
- `management.endpoints.web.exposure.include`はデフォルトで`health`のみが公開対象のため、
  `metrics`・`info`も含めて明示的に設定した
- テストでは他クラスが共有DBに残すデータの影響を避けるため、`lowStockCount`を絶対値ではなく
  「作成前後の差分」で検証している(46番のページングテストと同じ方針)

## VSCode警告の解消(#130)
VSCode(Eclipse JDT)のnull解析で、`ProductService`の`findAll`/`delete`/`findProductOrThrow`に
「unchecked conversion」の誤検知警告が出ていた。

- Spring Data(`org.springframework.data.repository`)はパッケージ単位で`@NonNullApi`が宣言されており、`CrudRepository.findById`/`delete`、`PagingAndSortingRepository.findAll`の引数は暗黙的に`@NonNull`になる
- 一方`ProductService`側の引数(`id`/`pageable`)や`findProductOrThrow`の戻り値にはnull許容性の注釈が無いため、この不一致が誤検知の原因だった
- `id`/戻り値に`@NonNull`を波及させると、その呼び出し元(`findById`/`update`/`delete`/`fulfillOrder`)にも同様の注釈が必要になりクラス全体に変更が広がるため、影響範囲を抑えて該当3メソッドに`@SuppressWarnings("null")`を付与する方針にした

## テスト
```bash
cd 44_JpaRestApi
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
