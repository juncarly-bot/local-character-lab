# Presence System

## Goal

「空いているから入る」だけではなく、誰がいるか、何の話をしているか、会話がどれくらい続いているか、現在時刻、本人が今どういう気分かによって入室・退出を変える。

## Room rules

- 登録キャラクターは最大10人程度を想定
- 同時入室は最大4人
- 満室時に新しいキャラクターが入りたがっても、既存キャラクターを機械的に追い出さない
- 誰かが自然に退出した後で再判定する

## Eligibility

入室候補になる基本条件:

```text
life_state == FREE
room_state == OUT
room_count < 4
```

## Enter score

入室スコアは複数要因を組み合わせる。

候補:

- Conversation Analysisが示す現在話題
- 現在話題への興味
- 部屋にいる相手へのAffection
- 部屋にいる相手へのComfort
- Tension
- `room_conversation_duration`
- 現在時刻 / 睡眠時刻までの距離
- sociability
- initiative
- Current Desire
- Mood
- 最近どのくらい部屋にいたか
- 少量のランダム性

概念例:

```text
enter_score =
  topic_interest
+ relationship_pull
+ conversation_duration_modifier
+ time_of_day_modifier
+ sociability
+ initiative
+ desire
+ mood_modifier
- tension_penalty
- recent_presence_penalty
+ randomness
```

正確な重みは実装・試遊をしながら調整する。

## Conversation duration

会話時間は2種類を区別する。

### room_conversation_duration

現在の部屋で会話自体がどれくらい続いているか。

主に未入室キャラクターのPresence判定に使う。

例:

- 開始直後: まだ様子が分からないため補正は小さい
- 数分から十数分: 興味のある話題なら入室したくなりやすい
- 長時間: 新規参加への魅力は話題・Relationship次第。既存メンバーには退出方向の補正が生まれ得る

固定時間で強制入退室させず、スコア補正として使う。

### participation_duration

そのキャラクター自身が現在の会話へ何分参加しているか。

主に以下へ使う。

- 疲労 / Mood
- 退出しやすさ
- Relationshipのセッション更新
- 長時間滞在によるrecent presence補正

1時間続いた部屋へ最後の5分だけ参加したキャラクターを、1時間参加した扱いにはしない。

## Time of day

現在時刻とLife CycleをPresenceへ反映する。

例:

- 自由時間の中心: 通常の入室判定
- 睡眠時刻が近い: 入室スコア低下、退出スコア上昇
- すでにSLEEPへ遷移すべき時刻: 新規入室不可

時刻だけで機械的な行動を強制せず、Life state、Mood、Desireと組み合わせる。

## Invitations

キャラクターは別のキャラクターを呼ぶことができる。

招待は強制ではなく、受け手側が以下を考慮して判断する。

- `FREE` か
- 招待者とのRelationship
- 部屋の他メンバーとのRelationship
- 現在話題へのInterests
- Current Desire
- Mood
- 現在時刻

## Leaving

退出も入室と同じくらい重要。

候補要因:

- 睡眠時刻が近い
- `participation_duration` が長い
- 興味のない話題が続いている
- Tensionが高い
- 一人でいたいDesire
- 発言機会が長くない
- Moodが疲労・眠気寄り
- 会話のenergyが本人の好みと合わない

## Silence

部屋にキャラクターがいることと、常に会話が発生することを分離する。

複数人が同じ部屋にいても、数分以上誰も話さない時間を許容する。

`silence_tolerance` が低いキャラクターほど沈黙を破りやすく、高いキャラクターほど静かな時間をそのまま受け入れやすい。
