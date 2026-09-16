# Character System

## Character Profile v1 baseline

各キャラクターは共通ベースモデルとは独立したProfileを持つ。Profileはキャラクターの恒常的な性質を表し、現在状態や一時感情とは分離する。

v1では以下の構造を正式な基本方針として採用する。

- Identity / speech style
- Personality
- Conversation behavior
- Interests
- Expertise / knowledge background
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

## Speech Style v1

Speech StyleはCharacter Profileの正式な必須領域として扱う。

語尾や口癖だけでキャラクターを作らず、以下の4層で表現する。

```text
Speech Identity
Speech Tendencies
Signature Expressions
Speech Examples
```

Character Profile UIからキャラクターごとに個別編集可能とする。

主な編集項目:

- 一人称
- 呼称ルール
- 丁寧さ
- warmth / 親しみやすさ
- directness
- sentence length
- emotional expression
- playfulness
- 特徴的な語尾
- 口癖
- 語尾 / 口癖の頻度・cooldown
- 話し方の自然言語特徴
- avoid rules
- Conversation Act / Mood別の例文

Hard ruleとSoft ruleを分離する。

- Hard rule: 一人称、呼称、禁止表現など
- Soft rule: 語尾、口癖、テンポ、文長など

特徴的な語尾は毎文強制しない。同じ表現の直近使用、Mood、Relationship、Conversation Actなどで頻度を補正する。

Speech StyleはRuntimeで以下のように解決する。

```text
Character Profile
+
Relationship
+
Mood
+
Conversation Act
+
Current Topic
↓
Speech Style Resolver
↓
Current Speech Style
↓
main local model
```

詳細仕様および本採用の東北イタコ型テストProfileは `docs/SPEECH_STYLE_SYSTEM.md` を参照する。

## Interest / Expertise model

InterestとExpertiseは別の概念として扱う。

- `interest`: その話をどれだけ好きか、参加したいか
- `expertise`: その分野についてどれくらい詳しいか

「好きだが詳しくない」「詳しいが自分からは話さない」を成立させる。

### Manual configuration scope

全話題へ手入力はしない。

ユーザーは主に以下だけ設定する。

1. 10-15個程度の大分類
2. キャラクター性を強く出す特徴的な話題
3. 必要な例外

例:

```yaml
domains:
  science:
    interest: 75
    expertise: 65
  technology:
    interest: 60
    expertise: 70
  games:
    interest: 90
    expertise: 55
  food:
    interest: 55
    expertise: 35
  travel:
    interest: 65
    expertise: 40

topics:
  physics:
    interest: 95
    expertise: 85
  astronomy:
    interest: 90
    expertise: 70
  cars:
    interest: 20
    expertise: 15
```

未設定TopicはSemantic Similarity、大分類、knowledge background、Personality / defaultの順で自動推定する。

詳細は `docs/KNOWLEDGE_SYSTEM.md` を参照。

### Inferred interests

会話履歴などから自動推定された興味は、ユーザー設定と分離する。

```yaml
hot_springs:
  interest: 72
  source: inferred
```

ユーザーが手動設定した値は `source: user` とし、自動推定で上書きしない。

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
- Topicごとの一時Knowledge Access / Confidence

MoodやDesireはProfileそのものではなく、Conversation Analysis、時刻、Memory、Relationshipなどから変化する実行時状態として扱う。

## UI principle

チャット画面のキャラクターアイコンからCharacter Detail / Editorへ移動できるようにする。

ユーザーが主に編集する項目:

- Personality
- Conversation behavior
- Speech Style
- Interests
- Expertise / knowledge background
- Values
- 初期関係性や明示的な関係設定

Interests / Expertise UIは次の3層を基本とする。

```text
[大分類]
科学          75
技術          60
ゲーム        90
食べ物        55
旅行          65

[特徴的な話題]
物理          95
宇宙          90
車            20

[自動推定された興味]
温泉          72
AI            68
数学          82
```

主に閲覧する実行時項目:

- Mood
- Current Desire
- Life state
- Room state
- 最近の判断理由
- 自動推定されたInterest / Knowledge state

Developer Modeでは入室、沈黙、発言、退出、Conversation Act、Topic推定、Speech Style Resolverの有効補正などの判断要因を確認できるようにする。
