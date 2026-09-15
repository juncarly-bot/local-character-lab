# Conversation Analysis

## Goal

Mood、Relationship、Presence、Desireが別々に会話を解釈するのではなく、1つの共有Conversation Analysisを共通センサーとして使う。

同じ会話から同じ基本解釈を作り、その解釈を各キャラクターのProfileやRelationshipと組み合わせて個別の反応へ変換する。

## Inputs

初期候補:

- current transcript / recent conversation window
- current participants
- room conversation start time
- per-character join time
- current local time
- recent topic history

キャラクター固有のInterestsやPersonalityは分析そのものへ直接混ぜすぎず、共通分析結果を各キャラクター側で解釈すると再利用しやすい。

## Initial output concept

```json
{
  "topics": {
    "physics": 0.82,
    "study": 0.55
  },
  "tone": "positive",
  "intimacy": 0.31,
  "energy": 0.62,
  "conflict": 0.02,
  "importance": 0.38
}
```

これは確定schemaではない。次の設計で各フィールドの意味、範囲、更新タイミングを決める。

## Consumers

### Mood

Conversation Analysis + Character Profile + participation duration + time of day から現在Moodを変化させる。

### Relationship

Conversation Analysis + Character Interests + Relationship + participation duration からセッション単位のRelationship deltaを計算する。

### Presence

Conversation Analysis + Character Interests + Relationships with room members + room conversation duration + time of day から入室・退出スコアを計算する。

### Desire

Conversation Analysis + Memory + Profile + Relationship + Mood から「この話題に入りたい」「昨日の続きを聞きたい」などの自発的欲求生成に使う。

## Duration fields

Conversation Analysis周辺では少なくとも次の2つを分離する。

- `room_conversation_duration`: 部屋の現在会話が続いている時間
- `participation_duration[character_id]`: そのキャラクター自身が参加した時間

## Design principles

- 1発言ごとにRelationshipを直接変更しない
- Moodを1発言で極端に飛ばさない
- 同じ話題単語の反復だけでRelationshipを稼げないようにする
- Presenceは固定タイマーではなくスコア補正で扱う
- 共通分析とキャラクター固有解釈を分離する
- Developer Modeから分析結果と最終判断への寄与を確認できる設計を目指す

## Next decisions

1. topic representationを固定カテゴリ中心にするか、動的ラベル中心にするか
2. `tone` をカテゴリにするか連続値にするか
3. intimacy / energy / conflict / importance の定義と範囲
4. Conversation Analysisを毎ターン更新するか、一定間隔またはイベント単位で更新するか
5. セッション境界を何で判定するか
6. 3B本体に分析させる部分と決定論ロジックで処理する部分の分担
