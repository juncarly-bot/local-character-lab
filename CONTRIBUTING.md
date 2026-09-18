# Contributing

Local Character Labへの参加ありがとうございます。

このプロジェクトは現在、MVP実装へ入る直前の設計・基盤構築フェーズです。大きなコードベースが完成していることを前提にせず、設計・UI・モデル実験・QA・ドキュメントのどこからでも参加できるようにする。

## 最初に読むもの

1. `README.md`
2. `docs/PROJECT_OVERVIEW.md`
3. `docs/ARCHITECTURE_OVERVIEW.md`
4. 自分が触る領域の `docs/*.md`

## 参加しやすい領域

### Android

- Kotlin
- Jetpack Compose
- Navigation
- Character Profile UI
- Chat UI
- Room / SQLite
- DataStore

### Character Engine

- Conversation Director
- Presence / Guest Slot
- Topic Lifecycle
- Conversation Acts
- Relationship
- Mood / Desire / Seed
- Memory retrieval
- Context Builder

### Local LLM

- Main LLM runtime
- 0.6B-class Helper experiments
- quantization / context / performance measurement
- structured output validation
- streaming generation

### UX / QA

- キャラクターらしさの比較
- 4B / 8B A-B test
- AI-to-AI会話の自然さ
- latency profiling
- Character consistency test

### Documentation

- Mermaid図
- 仕様整理
- テストシナリオ
- Character Profile sample

## 設計資料の扱い

Repository内では役割を分ける。

```text
docs/
正式採用している現在仕様

decisions/
大きな設計判断と理由

notes/
設計会議・未確定案・検討履歴
```

`notes/` にある案を、合意なしに正式仕様として実装しない。現在の採用仕様は原則 `docs/` を優先する。

## 推奨開発フロー

小さな単位で変更する。

```text
main
  ↓
feature branch
  ↓
implementation / tests
  ↓
Pull Request
  ↓
review
  ↓
merge
```

Branch名の例:

```text
feature/character-profile-ui
feature/conversation-director
feature/model-runtime
fix/relationship-decay
docs/architecture-update
```

Commitは「何をしたか」が分かる粒度を優先する。

例:

```text
Add Character Profile speech editor
Implement response streaming skeleton
Add relationship axis tests
Document runtime latency target
```

## Pull Requestで書いてほしいこと

- 何を変えたか
- なぜ必要か
- どの仕様書に対応するか
- 動作確認方法
- 未解決点 / trade-off
- UI変更ならスクリーンショット
- latency変更なら計測値

## 守りたい設計原則

- Character Profile / Memory / RelationshipをMain LLMの内部状態だけに依存させない
- UIとDomainを分離する
- 同じ基盤LLMだからといって全Characterへ同じKnowledgeを与えない
- 発言しないこと、沈黙することを正常状態として扱う
- Relationshipを単一好感度へ潰さない
- foregroundでLLM callを増やしすぎない
- 通常turnは10秒以内を目安にする
- Character Engineを将来新躍進政策へ再利用できる境界を守る

## 仕様変更を提案したいとき

大きな仕様変更は、いきなり実装へ入るより先にIssueまたは設計メモとして目的とtrade-offを共有する。

特に以下は先に相談する。

- Relationship軸の追加・削除
- Memory schema変更
- Character Profile互換性を壊す変更
- Main / Helper modelの役割変更
- Room / Presenceの基本ルール変更
- データ破壊を伴うmigration

## セキュリティ

- API keyやtokenをcommitしない
- model download URLやchecksumは検証可能な形で管理する
- private dataをテストログへ無制限に残さない
- destructive migrationは明示的な承認なしに行わない

## 目標

最初のMVPでは機能数よりも次を優先する。

> 複数Characterが同じRoomに存在し、それぞれ違う人格・知識・関係を保ちながら、USERを含めて自然に会話できること。

小さく動かし、Developer Modeと実測データを使って調整していく。
