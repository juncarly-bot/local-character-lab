# Model Plan

## Base model direction

Character Engineでは1つの共通Main Modelを複数キャラクターで共有し、各キャラクターはProfile、Context、Memory、Relationship、Speech Styleによって分離する。

10キャラクター = 10個のLLM、とはしない。

Main Modelは端末・環境に応じて交換可能とする。

初期方針:

```text
PC / laptop
Main: 8B級

Android 8GB級
Main: 4B級を標準候補

Lightweight Android
Main: 2B級候補
```

8B / 4B / 2BでCharacter Engineの機能そのものは削らない。モデルサイズ差は主に、会話表現力、複雑な文脈理解、複数条件の同時遵守、キャラクター演技の精度として現れるようにする。

詳細なDownload / Delete / role selection仕様は `docs/MODEL_LIBRARY.md` を参照。

## Helper model direction

新躍進政策との統合を踏まえ、0.6B級の小型LLMを補助モデルとして利用する構成を正式候補とする。

主な担当候補:

- Topic分類
- Conversation Analysis補助
- importance判定
- Seed候補分類
- 短い要約
- JSON構造化
- 軽量な意味判定

最終キャラクター発話、複雑な感情理解、長い複数人文脈の最終判断はMain Modelへ残す。

補助モデルへ何を移すかは実測Latencyと品質を見て決める。小型モデルを導入するためにDeterministic codeで十分な処理までLLM化しない。

## Character separation

各キャラクターは少なくとも以下を別々に持つ。

- Character Profile
- Speech Style
- Relationship state
- Memory access scope
- Interest / Expertise / Knowledge state
- Mood / Desire / life state
- 会話Context
- 将来的には必要なキャラクターだけLoRA等のAdapter

Main Modelを変更してもこれらは保持する。

## Generation policy

複数キャラクターを1回の生成でまとめて演じさせるのではなく、基本は1キャラクターずつ順次生成する。

例:

```text
USER
 -> Conversation Director
 -> SAYO生成
 -> transcript更新
 -> Directorが次の発言要否を判断
 -> TTがSAYOの実際の発言を読んで必要なら生成
 -> transcript更新
```

固定のA -> B -> C -> Dローテーションにはしない。

Conversation Directorが「誰が発言候補か」「どのConversation Actか」を決め、発言しない選択も許す。

Main Modelには可能な限り、すでに整理されたRuntime Contextと明確なDirector Instructionを渡す。

## Runtime abstraction

Domain / Application層は特定モデル名へ依存しない。

```text
Character Engine
        ↓
CharacterModelRuntime
        ├ Main Model Runtime
        ├ Helper Model Runtime
        └ optional Remote Runtime
```

これにより、PC 8B、Android 4B、軽量2B、将来の別モデルを同じCharacter Engineで利用できる。

## Model Library

モデルはアプリへ固定せず、Settings内のModel Libraryから手動でDownload / Delete / Useできるようにする。

初期role:

- Main Model
- Helper Model
- Embedding Model

モデルファイルの削除はCharacter Profile、Memory、Relationship等のデータ削除とは完全に分離する。

現在使用中モデルの削除にはreplacement selection等の安全策を入れる。

自動モデル選択は現時点では保留。v1は手動選択を基本とする。

## Training direction

初期段階ではLoRA学習を急がず、まずProfile + Speech Style + Prompt + Few-shot + Memoryでキャラクター性を作る。

実際に使って観察した失敗パターンを集めた後、必要なキャラクターだけSFT / LoRA / QLoRAを検討する。

## Integration

Character Engineを独立API / Runtime interfaceとして扱える形を目標とする。

```text
[UI / 新躍進政策]
       |
       v
[Character Engine]
       |
       +-- Presence / Director
       +-- Relationship
       +-- Memory
       +-- Desire / Mood
       +-- Knowledge / Topic
       +-- Speech Style Resolver
       +-- Model Runtime abstraction
              |
              +-- 8B Main
              +-- 4B Main
              +-- 2B Main
              +-- 0.6B Helper
```

新躍進政策へ統合しても、モデルサイズやRuntimeの変更だけでCharacter Engine全体を再設計しない構造を維持する。
