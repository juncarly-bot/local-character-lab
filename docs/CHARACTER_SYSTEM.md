# Character System

## Character Profile v1 baseline

各キャラクターは共通ベースモデルとは独立したProfileを持つ。Profileはキャラクターの恒常的な性質を表し、現在状態や一時感情とは分離する。

v1では以下の構造を正式な基本方針として採用する。

- Identity / speech style
- Personality
- Conversation behavior
- Interests
- Values
- Life-cycle settings
- Initial directional Relationships

正確な保存形式や数値アンカーは後で調整してよいが、概念構造はこの形で進める。

### Personality

各値は原則 `0-100`。

- `sociability`: 社交性。人のいる場を好む度合い
- `initiative`: 自発性。自分から行動・発言する度合い
- `curiosity`: 好奇心。知らない話題へ食いつく度合い
- `emotionality`: 感情が表面へ出やすい度合い
- `humor`: 冗談や軽い遊びを好む度合い
- `assertiveness`: 自分の意見を強く出す度合い

### Conversation behavior

- `talkativeness`: 発言量
- `interruption`: 割り込みや短い反応を入れやすい度合い
- `topic_change`: 自分から話題を変える度合い
- `silence_tolerance`: 沈黙を気にしない度合い

これらは発言生成文そのものだけでなく、発言するか・黙るか・入室するかの判断にも使う。

### Interests

興味は固定カテゴリだけにせず、ユーザーが任意の項目を追加できる設計を目指す。

例:

```yaml
interests:
  physics: 70
  games: 35
  food: 25
  relationships: 40
  technology: 80
  astronomy: 90
```

現在の会話を話題分類し、話題と興味度の一致を発言欲求・入室スコア・Mood・Relationship成長へ反映する。

「興味が高い = 必ず発言する」にはしない。人格、関係、Mood、Desire、最近の発言頻度などと組み合わせる。

### Values

価値観は数値化しすぎず、自然言語のリストとして持つ。

例:

```yaml
values:
  - 努力している人を応援したい
  - 無理をしている人は放っておけない
  - 一人で過ごす時間も大切にする
  - 人に考えを押し付けることを好まない
```

数値パラメータは「どう動くか」、Valuesは「なぜそう動くか」を表現する。

## Current state

恒常Profileとは別に、以下の可変状態を持つ。

- `life_state`: `SLEEP` / `FREE`
- `room_state`: `IN` / `OUT`
- `mood`
- `desires`
- 関係ごとの一時的な `tension`

MoodやDesireはProfileそのものではなく、Conversation Analysis、時刻、Memory、Relationshipなどから変化する実行時状態として扱う。

## Initial example

以下は設計例であり、SAYOの正式値ではない。

```yaml
personality:
  sociability: 65
  initiative: 45
  curiosity: 70
  emotionality: 60
  humor: 55
  assertiveness: 40

conversation:
  talkativeness: 55
  interruption: 20
  topic_change: 35
  silence_tolerance: 70

interests:
  physics: 45
  games: 70
  food: 65
  relationships: 75
  technology: 30
```

## UI principle

チャット画面のキャラクターアイコンからCharacter Detail / Editorへ移動できるようにする。

ユーザーが主に編集する項目:

- Personality
- Conversation behavior
- Interests
- Values
- 初期関係性や明示的な関係設定

主に閲覧する実行時項目:

- Mood
- Current Desire
- Life state
- Room state
- 最近の判断理由

Developer Modeでは入室、沈黙、発言、退出などの判断要因を確認できるようにする。
