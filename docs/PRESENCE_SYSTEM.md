# Presence System

## Goal

「空いているから入る」だけではなく、誰がいるか、何の話をしているか、本人が今どういう気分かによって入室・退出を変える。

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

- 現在話題への興味
- 部屋にいる相手へのAffection
- 部屋にいる相手へのComfort
- Tension
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
+ sociability
+ initiative
+ desire
+ mood_modifier
- tension_penalty
- recent_presence_penalty
+ randomness
```

正確な重みは実装・試遊をしながら調整する。

## Invitations

キャラクターは別のキャラクターを呼ぶことができる。

招待は強制ではなく、受け手側が以下を考慮して判断する。

- `FREE` か
- 招待者とのRelationship
- 部屋の他メンバーとのRelationship
- Current Desire
- Mood

## Leaving

退出も入室と同じくらい重要。

候補要因:

- 睡眠時刻が近い
- 長く滞在している
- 興味のない話題が続いている
- Tensionが高い
- 一人でいたいDesire
- 発言機会が長くない
- Moodが疲労・眠気寄り

## Silence

部屋にキャラクターがいることと、常に会話が発生することを分離する。

複数人が同じ部屋にいても、数分以上誰も話さない時間を許容する。

`silence_tolerance` が低いキャラクターほど沈黙を破りやすく、高いキャラクターほど静かな時間をそのまま受け入れやすい。
