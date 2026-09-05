# JSON/XMLシリアライズ実践(Jackson)

## 学習ポイント
Jackson(またはGson)によるJSON/XMLシリアライズ・デシリアライズ、カスタムシリアライザ

## 概要
商品カタログを題材に、`ObjectMapper`/`XmlMapper`を明示的に使ってJSON/XML相互変換を行う対話式CLIツール。
`add <ID> <商品名> <価格> <発売日yyyy-MM-dd>` / `list` / `toJson` / `toXml` / `fromJson <JSON>` / `exit`。

## 実装メモ
- `Product`(record)の`price`フィールドに`@JsonSerialize`/`@JsonDeserialize`で`PriceSerializer`/`PriceDeserializer`を直接指定し、`150`⇔`"150円"`のようなカスタム変換を実装した。record component(コンストラクタ引数)に付与したアノテーションは、Jacksonが参照するフィールド・アクセサメソッドへ自動的に伝播する。
- `releaseDate`(`LocalDate`)は`jackson-datatype-jsr310`を登録するだけで標準的にISO-8601形式("2026-04-01")へ変換できる。ただし`SerializationFeature.WRITE_DATES_AS_TIMESTAMPS`が既定で有効なため、これを無効化しないと`[2026,4,1]`という数値配列で出力されてしまう点にハマった(実装メモとして記録)。
- Jackson 2.12以降はrecordをネイティブにサポートしており、`Class#getRecordComponents()`でコンストラクタ引数名を直接取得できるため、`maven-compiler-plugin`に`-parameters`オプションを付けなくても`fromJson`のデシリアライズが正しく動作する。
- XMLはリストをそのままルート要素にできないため、`ProductList`(record)を`@JacksonXmlRootElement`/`@JacksonXmlElementWrapper(useWrapping = false)`でラップし、`<products><product>...</product>...</products>`という構造にした。
- 外部リソース(ネットワーク・ファイル・DB)に依存しない純粋なユニットテストのみで構成した。

## テスト
```bash
cd 36_JacksonJsonMapping
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
