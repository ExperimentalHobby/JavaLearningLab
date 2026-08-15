# docs/design/ 設計書テンプレート

各エクササイズフォルダ(`NN_ExerciseName/`)に対応する設計書を `docs/design/NN_ExerciseName/` に作成する。
`docs/` はgitignore対象のローカル資料であり、PRには含まれない。

`01_Calculator` を最初のテンプレートとして、以降のフォルダも同じ構成・手順を踏襲する。

## ディレクトリ構成

```
docs/design/
├── README.md                  # このファイル
├── fonts/                     # 全設計書で共有する日本語フォント(コミット不要・ローカル資料)
│   ├── VL-Gothic-Regular.ttf
│   └── VL-PGothic-Regular.ttf
├── themes/                    # 全設計書で共有するPDFテーマ
│   ├── ja-theme.yml           # 使用中(VL Gothicベース)
│   └── default-ja-theme.yml   # Noto Sans CJK JP版(未使用・フォント未配置)
└── NN_ExerciseName/
    ├── design.adoc            # 設計書本体(AsciiDoc)
    ├── generate-pdf.ps1       # PDF生成スクリプト(01_Calculatorからコピー)
    └── diagrams/
        ├── class-diagram.puml
        ├── sequence-*.puml    # ユースケースごとに分割
        └── activity-diagram.puml
```

## 新規フォルダ作成手順

1. `docs/design/NN_ExerciseName/diagrams/` を作成し、クラス図・シーケンス図(ユースケースごとに分割)・アクティビティ図の `.puml` を作成する。
   - **各 `.puml` の先頭に必ず `skinparam defaultFontName VL Gothic` を入れる**(PDF化時の文字化け対策。詳細は「PDF出力と文字化け対策」を参照)。
   - シーケンス図はクラス名・メソッド名を使わず、役割ベースの言葉で記述する(設計書としての可読性を優先)。
2. `design.adoc` を作成する。ヘッダーは以下を踏襲する:
   ```
   = NN_ExerciseName 設計書
   :doctype: article
   :toc: left
   :toclevels: 2
   :sectnums:
   :source-highlighter: rouge
   :icons: font
   :imagesdir: diagrams
   :pdf-fontsdir: ../fonts
   :pdf-themesdir: ../themes
   :pdf-theme: ja
   ```
   - 見出しに手動で章番号を書かない(`:sectnums:` が自動採番するため、`== 1. 概要` ではなく `== 概要` とする)。
   - 章立ては 概要 → ユースケース → シーケンス図 → クラス図 → アクティビティ図 → 補足事項 を基本とする。
   - UML図は `[plantuml, 図名, svg]` ブロックで `include::diagrams/xxx.puml[]` して埋め込む。
3. `01_Calculator/generate-pdf.ps1` をコピーして配置する(内容はフォルダ間で共通、変更不要)。
4. `mvn compile`(または `mvnw.cmd compile`)などの実行コマンドは、対象フォルダに実在するビルド手段(`mvnw`の有無)を確認してから記載する。

## PDF出力と文字化け対策

`asciidoctor-pdf` でSVG埋め込みのPlantUML図をPDF化すると、**図の中の日本語テキストだけ文字化けする**既知の問題がある(本文の日本語は無関係)。SVG→PDF変換時にCJKグリフのToUnicodeマッピングが正しく生成されないことが原因。

対策として、PlantUML図側のフォントとPDF側の埋め込みフォントを一致させる:
- `.puml` に `skinparam defaultFontName VL Gothic` を指定し、SVG内のテキストを `font-family="VL Gothic"` として出力させる
- `design.adoc` の `:pdf-fontsdir:` / `:pdf-themesdir:` / `:pdf-theme:` で、同名の `VL Gothic` フォント(`docs/design/fonts/VL-Gothic-Regular.ttf`)を解決できるテーマ(`docs/design/themes/ja-theme.yml`)を指定する

`[plantuml, xxx, png]`(ラスタ画像)への変更でも回避できるが、HTML表示の画質を優先し、上記のフォント統一で対応する方針とした。

## 実行方法

```powershell
cd docs/design/NN_ExerciseName
./generate-pdf.ps1
```

必要な環境: Ruby + `asciidoctor-pdf` + `asciidoctor-diagram`、PlantUML(Java)。

## 参考

- 01_Calculatorでの検証時のやり取り(既存Issue・PR・実装プランの参照方法、AsciiDoc/PlantUMLの検証手順)は本リポジトリの会話ログを参照。
