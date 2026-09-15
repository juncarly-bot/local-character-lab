# Memory System

## Current direction

記憶はキャラクターごとに持たせる方向を第一候補とする。

ただし、実装方式はまだ完全確定ではない。共通ストアを使う場合でも、各記憶に「誰が知っているか」を必ず持たせ、キャラクター間の情報差を失わない設計にする。

## Why personal memory matters

例として、SAYOとTTだけが喧嘩した場合:

```text
SAYO: 喧嘩を覚えている
TT:   喧嘩を覚えている
C:    その場にいなかったので知らない
```

Cが後から部屋へ入り、「二人ともなんか静かじゃない？」と反応できる状態を作りたい。

全員が見ていない出来事まで自動的に知ってしまうことは避ける。

## Candidate memory fields

```yaml
id: memory-id
created_at: timestamp
summary: "TTと物理の話をした"
participants: [sayo, tt]
known_by: [sayo, tt]
importance: 0-100
emotion: calm
related_topics: [physics]
relationship_effects: []
source_event_id: event-id
```

## Memory roles

記憶は少なくとも以下に利用する。

- 昨日の話題を自発的に再開する
- Character Desireの生成
- Relationship変化の根拠
- 同じ失敗や質問の不自然な繰り返しを減らす
- キャラクター間で「知っている / 知らない」を作る

## Open decision

保存構造は以下のどちらかを比較する。

1. キャラクターごとに完全に別のMemory Storeを持つ
2. 1つの共通Memory Storeに `known_by` / `participants` を持たせ、検索時にキャラクター単位でフィルタする

体験としては個別記憶を維持することを必須とし、物理保存方式は実装時に決定する。
