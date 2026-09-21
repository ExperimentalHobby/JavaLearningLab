# JSON/XMLシリアライズ実践(Jackson)

## 学習ポイント
Jackson(またはGson)によるJSON/XMLシリアライズ・デシリアライズ、カスタムシリアライザ

## 概要
商品カタログを題材に、`ObjectMapper`/`XmlMapper`を明示的に使ってJSON/XML相互変換を行う対話式CLIツール。
`add <ID> <商品名> <価格> <発売日yyyy-MM-dd>` / `list` / `toJson` / `toXml` / `fromJson <JSON>` / `fromXml <XML>` / `exit`。

## 実装メモ
- `Product`(record)の`price`フィールドに`@JsonSerialize`/`@JsonDeserialize`で`PriceSerializer`/`PriceDeserializer`を直接指定し、`150`⇔`"150円"`のようなカスタム変換を実装した。record component(コンストラクタ引数)に付与したアノテーションは、Jacksonが参照するフィールド・アクセサメソッドへ自動的に伝播する。
- `releaseDate`(`LocalDate`)は`jackson-datatype-jsr310`を登録するだけで標準的にISO-8601形式("2026-04-01")へ変換できる。ただし`SerializationFeature.WRITE_DATES_AS_TIMESTAMPS`が既定で有効なため、これを無効化しないと`[2026,4,1]`という数値配列で出力されてしまう点にハマった(実装メモとして記録)。
- Jackson 2.12以降はrecordをネイティブにサポートしており、`Class#getRecordComponents()`でコンストラクタ引数名を直接取得できるため、`maven-compiler-plugin`に`-parameters`オプションを付けなくても`fromJson`のデシリアライズが正しく動作する。
- XMLはリストをそのままルート要素にできないため、`ProductList`(record)を`@JacksonXmlRootElement`/`@JacksonXmlElementWrapper(useWrapping = false)`でラップし、`<products><product>...</product>...</products>`という構造にした。
- 外部リソース(ネットワーク・ファイル・DB)に依存しない純粋なユニットテストのみで構成した。

### コードレビュー指摘への対応(Issue #168)
- **壊れたJSONを`fromJson`に渡すとアプリが落ちる問題 + エラー方針が2つのマッパーで食い違っている問題**: 同根の問題としてまとめて対応した。`ProductJsonMapper.fromJson`が`UncheckedIOException`のまま投げていたのを、`ProductXmlMapper.fromXml`と同じ方針(`JsonProcessingException`を`IllegalArgumentException`に変換)に揃えた。これにより`Main.run`の既存の`catch (IllegalArgumentException e)`がそのまま効くようになり、クラッシュも解消した。対象を`MismatchedInputException`(型不一致)から上位の`JsonProcessingException`(構文エラーの`JsonParseException`も含む)に広げ、両マッパーの方針を完全に揃えている。
- **`fromXml`を呼び出すコマンドがない問題**: `fromXml <XML>`コマンドを追加した(`fromJson`と対称)。
- **`PriceDeserializer`が`"abc円"`やJSONの`null`を想定していない問題**: `DeserializationContext.reportInputMismatch(...)`で明示的に`MismatchedInputException`を送出するようにした(上記の統一されたエラー方針により自動的に`IllegalArgumentException`に変換される)。JSONの`null`は`deserialize()`を経由せず`getNullValue()`に来るため、そちらもオーバーライドした。
- **`list`だけrecordの`toString()`をそのまま出力している問題**: `ID: xxx, 商品名: xxx, 価格: xxx円, 発売日: xxx`形式に整形するようにした。

## テスト
```bash
cd 36_JacksonJsonMapping
mvn test       # 15件全てパス
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
