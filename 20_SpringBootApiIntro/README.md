# 簡易Webサーバー/API(Spring Boot入門)

## 学習ポイント
REST API構築、DI(依存性注入)の基礎

## 概要
書籍管理を題材にしたインメモリのREST API。`BookController`(REST層)が`BookService`(ロジック層)を
コンストラクタインジェクションで受け取る構成にし、DIの基礎を実践している。

- `GET /api/books` — 書籍一覧取得
- `GET /api/books/{id}` — 単一書籍取得(存在しなければ404)
- `POST /api/books` — 書籍登録(201、リクエストボディ`{"title": "...", "author": "..."}`)
- `DELETE /api/books/{id}` — 書籍削除(204、存在しなければ404)

## 実装メモ
- このリポジトリで初めてSpring Bootを使う課題。`spring-boot-starter-parent:3.5.3`をこのフォルダ単体の親POMとして採用した(リポジトリ全体で共通親を持たない方針とは矛盾しない、あくまでこのフォルダのみの親)。
- Java 25 + Spring Boot 3.5.3の組み合わせでビルド・テストとも問題なく動作することを確認済み(リリース時点でJava 25は正式サポート対象外の可能性を懸念していたが、実際には`<java.version>25</java.version>`の指定で警告なくコンパイル・実行できた)。
- `BookService`はSpringのDIコンテナ(`@Service`)に登録しつつも、テストでは`new BookService()`でSpringコンテナなしにプレーンなJUnitテストとして直接検証できる設計にした(01〜19で培った「ロジックをフレームワークから独立させる」パターンをSpring Boot上でも踏襲)。
- `BookController`は`@SpringBootTest(webEnvironment = RANDOM_PORT)` + `TestRestTemplate`で実際に埋め込みTomcatへHTTPリクエストを送る結合テストとした。`MockMvc`のような擬似的な検証ではなく、既存Issueと同じ「実リソースでのテスト」方針をHTTPレベルでも徹底している。
- 存在しないIDへのアクセスによる404は、コントローラー側で個別に例外処理をせず、`BookNotFoundException`に`@ResponseStatus(HttpStatus.NOT_FOUND)`を付与することでSpring MVCに自動マッピングさせている。
- `GET /api/books/{id}`の404テストを実装する際、最初に書いたテスト(存在しないIDへのアクセスが404であること)は、そもそも`/{id}`のマッピング自体が存在しない状態でもSpring Bootのデフォルト404応答により偽陽性でGreenになってしまう問題に気づいた。存在するIDで200が返ることを検証するテストを追加してから初めてRedを確認でき、エンドポイント実装後にGreenへ移行させた(TDDでテストの実効性を見誤らないための教訓として記録)。

## Docker
マルチステージビルドのDockerfileを追加した(ビルドステージ`eclipse-temurin:25-jdk` → 実行ステージ
`eclipse-temurin:25-jre`、非rootユーザーで実行)。

```bash
cd 20_SpringBootApiIntro
docker build -t springbootapiintro .
docker run --rm -p 8080:8080 springbootapiintro
curl http://localhost:8080/api/books
```

- `mvnw`(Maven Wrapper)を使ってビルドステージ内で依存解決・パッケージングを行うため、
  ホスト側にMavenのインストールは不要
- `pom.xml`だけを先にコピーして`dependency:go-offline`することで依存解決レイヤーをキャッシュし、
  `src`だけを変更した再ビルドを高速化している
- イメージビルドではテストを実行しない(`-DskipTests`)。テストの担保は`mvn test`/CIの役割とし、
  Dockerビルドはパッケージングに専念させる設計にした

**注記**: 作業環境にDocker Desktopが導入されていないため、`docker build`/`docker run`による実機検証は
未実施(構文レビューのみ)。実行確認は各自の環境で行うこと。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
