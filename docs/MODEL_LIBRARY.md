# Model Library v1

## Status

本仕様を正式採用する。

Character Engine / 新躍進政策では、Main LLMをアプリに固定せず、複数モデルをダウンロード・削除・切り替えできる `Model Library` を持つ。

自動モデル選択は現時点では採用せず、保留とする。v1ではユーザーが明示的にモデルを選択する。

---

## 1. Core principle

Character Profile、Memory、Relationship、Mood、Speech Style、Conversation Directorなどのキャラクター状態はモデルファイルから独立して保存する。

```text
Character Profile
Memory
Relationship
Speech Style
Conversation State
        ↓
CharacterModelRuntime
        ↓
8B / 4B / 2B / other compatible model
```

モデルを削除・交換しても、キャラクター人格・記憶・関係性は消えない。

モデルはキャラクターそのものではなく、Character Engineの指示を実際の文章へ変換する生成エンジンとして扱う。

---

## 2. Initial model roles

### Main Model

キャラクターの最終発話を生成する主モデル。

初期方針:

- PC / 高性能環境: 8B級を高品質Main Model候補
- Android 8GB級: 4B級を標準Main Model候補
- 低負荷・省容量: 2B級を軽量Main Model候補

Character Engineの機能自体はモデルサイズで削らない。

```text
Memory
Mood
Relationship
Guest Slot
Conversation Seeds
Speech Style
Conversation Director
```

などは8B / 4B / 2Bで共通とし、モデルサイズ差は主に会話表現力・複雑な文脈理解・演技精度として現れる設計にする。

### Helper Model

補助LLMとして0.6B級を候補とする。

主な役割候補:

- Topic分類
- Conversation Analysis補助
- importance判定
- Seed候補分類
- 簡易要約
- JSON構造化
- 軽量な意味判定

最終キャラクター発話、複雑な感情理解、長い複数人文脈の最終判断などはMain Model側へ残す。

### Embedding Model

Topic similarity、Memory retrieval、Interest / Expertise fallbackなどに利用するEmbeddingモデルはMain / Helperと別役割として扱う。

---

## 3. Model Library UI

Settings内に `Model Library` を置く。

例:

```text
Settings
└ Model Library

Main Model
────────────────────
Qwen 8B
高品質
約 5 GB
[ Download ]

Qwen 4B
標準
Installed
[ Use ] [ Delete ]

Qwen 2B
軽量
Installed
[ Use ] [ Delete ]

Helper Model
────────────────────
Qwen 0.6B
分析・分類用
Installed
[ Use ] [ Delete ]
```

モデルごとに少なくとも以下を表示する。

- display name
- role compatibility: Main / Helper / Embedding
- model size
- quantization
- installed / not installed
- currently selectedか
- optional short description

---

## 4. Role selection

v1では明示的な手動選択を基本とする。

```text
Main Model
[ Qwen 4B ▼ ]

Helper Model
[ Qwen 0.6B ▼ ]

Embedding Model
[ Default ▼ ]
```

同じモデルファイルが複数roleへ対応できる場合でも、role selectionはRuntime側で明示的に管理する。

### Auto selection

以下のような自動選択案は将来候補として残すが、v1では保留する。

```text
RAMに余裕あり -> larger model
memory pressure -> smaller model
Laptop接続中 -> remote 8B
```

ユーザーが自分で選択できる仕組みを先に完成させる。

---

## 5. Download behavior

未導入モデルには `Download` を表示する。

詳細画面例:

```text
Qwen 4B

Size: 2.5 GB
Quantization: Q4_K_M
Role: Main Model
空き容量: 18.4 GB

[ Download ]
```

Download中:

```text
Downloading...
1.37 / 2.50 GB
54%

[ Pause ] [ Cancel ]
```

要件:

- progress表示
- pause / resume
- interrupted downloadの再開
- cancel
- download完了後のchecksum検証
- 検証成功後のみInstalled扱い
- 必要容量不足時は開始前に警告

モデル配布元・ライセンス・URL管理方式は実装時に個別決定する。

---

## 6. Delete behavior

Installedモデルには `Delete` を表示する。

削除はモデルファイルのみを対象とし、Character Profile、Memory、Relationshipなどのユーザーデータは削除しない。

### Current model guard

現在使用中のMain / Helper / Embedding Modelを削除しようとした場合、無警告で削除しない。

例:

```text
Qwen 4B is currently used as Main Model.

Deleting it requires selecting another Main Model.

[ Cancel ]
[ Select replacement ]
```

代替モデルが存在しない場合は、削除後にCharacter Generationが利用不能になることを明示し、ユーザーが理解して選択した場合のみ許可するか、v1では削除をブロックする。

初期実装では安全側に寄せ、現在選択中モデルの削除にはreplacement selectionを要求する方針とする。

---

## 7. Runtime independence

Application / Domain層は特定モデル名を直接参照しない。

```text
Character Engine
        ↓
CharacterModelRuntime
        ├ LocalMainRuntime
        ├ LocalHelperRuntime
        └ optional RemoteRuntime
```

Main Modelを8Bから4Bへ変更しても、Conversation DirectorやProfile UI、Memory schemaを変更しない。

この抽象化により、将来Qwen以外の互換モデルを追加可能にする。

---

## 8. Initial deployment direction

現在の基本構成:

```text
PC / laptop
Main: 8B級
Helper: 0.6B級候補

Android 8GB級
Main: 4B級を標準候補
Helper: 0.6B級候補

Lightweight Android
Main: 2B級候補
Helper: 0.6B級または必要に応じて無効化
```

Androidで8Bを利用できる余地は残すが、8GB端末で8Bを標準構成にはしない。

ユーザーが端末状況を理解した上でモデルを個別にDownload / Useできる設計を優先する。

---

## 9. Future considerations

v1後に検討する。

- Auto model selection
- RAM / thermal aware switching
- runtime benchmarkによるおすすめ表示
- per-device recommendation
- model presets: High Quality / Balanced / Lightweight
- background download constraints
- Wi-Fi only download option
- remote / laptop runtime discovery
- model compatibility manifest

これらは現時点では本採用範囲外。まずManual Model Libraryを完成させる。
