# Interaction Events v1

## Goal

Conversation Analysisから、RelationshipとMemoryに意味のある対人イベントだけを抽出する。

Relationshipは1メッセージごとに直接更新せず、会話イベントまたはセッション単位でInteraction Eventを生成し、そのイベントを各Relationship軸へ反映する。

イベントは原則として方向を持つ。

`A -> B` のイベントと `B -> A` のイベントは別物として扱う。

## Event schema

初期schema:

```json
{
  "type": "support",
  "actor": "sayo",
  "target": "user",
  "strength": 0.72,
  "topic": "study",
  "observed_by": ["sayo", "user", "tt"]
}
```

- `type`: イベント種別
- `actor`: 行動した側
- `target`: 行動を受けた側
- `strength`: `0.0-1.0`。イベントの強さ
- `topic`: 任意。関連話題
- `observed_by`: その出来事を知り得る参加者。Memoryの知識境界に利用できる

相互的な出来事は必要に応じて両方向のイベントを生成する。

## Core event types

v1ではイベント数を増やしすぎず、Relationshipに意味のある以下を中心とする。

| Event | Meaning | Main effect |
|---|---|---|
| `shared_enjoyment` | 一緒に楽しい時間を過ごした | Affection, Comfort, Familiarity |
| `support` | 励ます、支える、寄り添う | Affection, Trust |
| `personal_disclosure` | 悩み、秘密、個人的なことを打ち明ける | Trust, Familiarity |
| `praise` | 相手を褒める、努力を認める | Affection, Comfort |
| `gratitude` | 感謝を明確に伝える | Affection, Trust |
| `help` | 実際に助ける、問題解決を手伝う | Trust, Affection |
| `disagreement` | 意見が食い違う | small Tension; usually little long-term damage |
| `criticism` | 相手への否定的評価や強い指摘 | Comfort down, Tension up |
| `conflict` | 強い口論や対立 | Comfort/Trust down, Tension strongly up |
| `apology` | 自分の非を認めて謝る | Trust recovery, Tension down |
| `reconciliation` | 仲直り、関係修復 | Comfort recovery, Tension down |
| `promise_kept` | 約束を守る | Trust up |
| `promise_broken` | 約束を破る | Trust down, Tension up |

`invitation`, `rejection`, `joke`, `teasing` などはv1のRelationship必須イベントには入れず、必要になったらPresenceまたはConversation Behavior側で追加する。

## Base delta guidance

以下は実装開始時の初期値であり、試遊で調整する。

Relationship軸は `0-100`。通常の1イベントで長期値が大きく動きすぎないようにする。

```text
shared_enjoyment:
  familiarity +0.4
  affection   +0.7
  comfort     +0.8

support:
  affection   +0.5
  trust       +0.8
  comfort     +0.3

personal_disclosure:
  familiarity +0.4
  trust       +0.9

praise:
  affection   +0.4
  comfort     +0.3

gratitude:
  affection   +0.3
  trust       +0.3

help:
  affection   +0.4
  trust       +0.9

disagreement:
  comfort     -0.1
  tension     +2.0

criticism:
  comfort     -0.5
  trust       -0.2
  tension     +6.0

conflict:
  affection   -0.4
  trust       -0.8
  comfort     -1.2
  tension     +15.0

apology:
  trust       +0.3
  comfort     +0.2
  tension     -6.0

reconciliation:
  affection   +0.2
  comfort     +0.7
  tension     -12.0

promise_kept:
  trust       +0.8

promise_broken:
  trust       -1.2
  comfort     -0.3
  tension     +8.0
```

Affectionは常に `0-100` にclampし、負の値にはしない。

## Strength scaling

実際のdeltaはbase deltaをそのまま使わず、少なくとも `strength` を掛ける。

概念:

```text
actual_delta = base_delta * strength * context_modifier
```

`context_modifier` には次を利用できる。

- topicがCharacter Interestsとどの程度一致したか
- Conversation Analysisのvalence / intimacy / conflict / importance
- participation duration
- Valuesとの一致または衝突
- 同種イベントの短時間反復
- 現在Relationship値

## Favorite-topic growth

キャラクターが好きな話題を相手と楽しく共有した場合、`shared_enjoyment` のAffection / Comfortへの寄与を少し増やす。

ただしInterestが高いだけではRelationshipは上げない。

必要条件は「その話題を実際に一緒に楽しんだ」こと。

例:

```text
physics interest = 90
physics conversation
positive valence
participation_duration = 25 min
shared_enjoyment strength = 0.8
```

この場合は通常のshared_enjoymentよりAffection / Comfortがやや上がりやすい。

## Duration and anti-farming

- Relationship計算には主に `participation_duration` を使う
- 単語の反復ではイベントを増やさない
- 同一イベントの短時間反復はdiminishing returnをかける
- 1セッション内の長期Relationship deltaには上限を設ける
- Tensionは短期状態なので長期軸より大きく動いてよい
- 高いRelationship値ほど正方向の成長を緩やかにする

初期案として、通常会話1セッションでFamiliarity / Affection / Trust / Comfortの各軸は絶対値 `2.0` 程度を超えないようにする。重大イベントは別枠として扱える。

## Relationship direction

イベントを受けた側だけでなく、行動した側のRelationshipが変化する場合もある。

例えばpersonal_disclosureでは、打ち明けた側が相手へのTrustを示しているため、actor側の `actor -> target Trust` を主に更新する。

supportやhelpでは、受け手側の `target -> actor Trust/Affection` が主に上がる。

Conversation Analysisはイベントの意味を抽出し、Relationship Engineが「どちら向きの関係に効くか」を決定する。

## Memory integration

重大または高strengthのInteraction EventはMemory候補になる。

例:

- 強いpersonal_disclosure
- conflict
- apology / reconciliation
- promise_kept / promise_broken
- 強いsupport / help

Memoryにはイベントtypeだけでなく、自然言語の短い要約と `observed_by` を保存する。

## Design principle

Interaction Eventは会話をポイントゲームに変えるためのものではない。

目的は「その会話が二人の関係にどんな意味を持ったか」を小さく、継続的に反映すること。
