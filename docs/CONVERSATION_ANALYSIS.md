# Conversation Analysis

## Goal

Mood、Relationship、Presence、Desireが別々に会話を解釈するのではなく、1つの共有Conversation Analysisを共通センサーとして使う。

同じ会話から同じ基本解釈を作り、その解釈を各キャラクターのProfileやRelationshipと組み合わせて個別の反応へ変換する。

## Status

Conversation Analysis v1 の基本構造を採用する。

v1では以下を中核とする。

- 動的なTopic tags
- 部屋全体の会話雰囲気
- 誰から誰へのInteraction Events
- 部屋全体と各キャラクター参加時間の分離

## Inputs

初期入力:

- current transcript / recent conversation window
- current participants
- room conversation start time
- per-character join time
- current local time
- recent topic history

キャラクター固有のInterestsやPersonalityは共通分析そのものへ直接混ぜすぎず、共通分析結果を各キャラクター側で解釈する。

## Output v1

概念schema:

```json
{
  "topics": [
    {"label": "physics", "score": 0.86},
    {"label": "quantum_mechanics", "score": 0.67},
    {"label": "study", "score": 0.42}
  ],
  "room_atmosphere": {
    "valence": 0.72,
    "energy": 0.48,
    "intimacy": 0.35,
    "conflict": 0.03,
    "importance": 0.44,
    "humor": 0.12
  },
  "interaction_events": [
    {
      "from": "user",
      "to": "sayo",
      "type": "personal_disclosure",
      "strength": 0.72
    },
    {
      "from": "sayo",
      "to": "user",
      "type": "support",
      "strength": 0.64
    }
  ],
  "duration": {
    "room_conversation_duration_sec": 1200,
    "participation_duration_sec": {
      "user": 1200,
      "sayo": 1200,
      "tt": 300
    }
  }
}
```

このschemaは実装時にフィールド名を微調整してよいが、概念構造はv1として採用する。

## Topics

Topicは固定カテゴリだけに閉じず、動的タグを基本とする。

例:

- `physics`
- `astrophysics`
- `black_holes`
- `study`
- `games`
- `hot_springs`

各topicには `0.0-1.0` の関連度scoreを持たせる。

Character Interestsもユーザーが自由に追加できるため、Conversation Analysis側も同様に拡張可能であることを優先する。

表記揺れは後段の正規化で吸収する。

例:

- 宇宙物理
- astro_physics
- astrophysics

を内部canonical label `astrophysics` に寄せる。

## Room atmosphere

### valence

範囲: `-1.0` から `+1.0`

会話全体の感情的な方向。

- `-1.0`: 非常にネガティブ
- `0.0`: 中立
- `+1.0`: 非常にポジティブ

カテゴリ文字列だけでなく連続値とすることで、Moodを滑らかに変化させやすくする。

### energy

範囲: `0.0-1.0`

会話の活発さ、勢い、テンポ。

### intimacy

範囲: `0.0-1.0`

個人的・親密な内容をどれだけ含むか。

### conflict

範囲: `0.0-1.0`

対立、言い争い、不一致の強さ。

### importance

範囲: `0.0-1.0`

長期MemoryやRelationshipへ反映する価値がある重要な会話か。

### humor

範囲: `0.0-1.0`

冗談、軽いやり取り、笑いの要素の強さ。

## Interaction Events

部屋全体の雰囲気とは別に、方向を持つ出来事を記録する。

基本構造:

```json
{
  "from": "character_or_user_id",
  "to": "character_or_user_id",
  "type": "event_type",
  "strength": 0.0
}
```

`strength` は原則 `0.0-1.0`。

Interaction EventsはRelationship更新やMemory生成の主要入力になる。

例:

- USERがSAYOへ悩みを打ち明ける
- SAYOがUSERを励ます
- TTがSAYOへ相談する
- AがBを強く否定する

Interaction Event typeの正式一覧は次の設計段階で決定する。

## Duration fields

Conversation Analysisでは少なくとも以下を分離する。

- `room_conversation_duration`: 現在の部屋会話そのものの継続時間
- `participation_duration[character_id]`: 各キャラクターが実際に参加していた時間

用途:

- Presence: room conversation durationも参照
- Mood / fatigue: participation durationを重視
- Relationship: participation durationと会話内容を組み合わせる

## Consumers

### Mood

Conversation Analysis + Character Profile + participation duration + time of day から現在Moodを変化させる。

Moodは一発言ごとに急変させず、会話の流れに応じて徐々に動かす。

### Relationship

Conversation Analysis + Character Interests + Interaction Events + Relationship + participation duration からRelationship deltaを計算する。

原則、1メッセージごとに直接加点せず、会話イベントまたはセッション単位で反映する。

### Presence

Conversation Analysis + Character Interests + Relationships with room members + room conversation duration + time of day から入室・退出スコアを計算する。

### Desire

Conversation Analysis + Memory + Profile + Relationship + Mood から「この話題に入りたい」「昨日の続きを聞きたい」などの自発的欲求生成に使う。

## Design principles

- 1発言ごとにRelationshipを直接変更しない
- Moodを1発言で極端に飛ばさない
- 同じ話題単語の反復だけでRelationshipを稼げないようにする
- Presenceは固定タイマーではなくスコア補正で扱う
- 共通分析とキャラクター固有解釈を分離する
- 部屋全体の雰囲気と、方向を持つ人物間イベントを分離する
- Developer Modeから分析結果と最終判断への寄与を確認できる設計を目指す

## Next decisions

1. Interaction Event typeの正式一覧
2. Conversation Analysisの更新タイミング
3. セッション境界の定義
4. Interaction EventからRelationship deltaへ変換するルール
5. 3B本体に分析させる部分と決定論ロジックで処理する部分の分担
