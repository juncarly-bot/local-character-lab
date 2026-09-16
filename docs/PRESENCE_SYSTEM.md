# Presence System

## Goal

「空いているから入る」だけではなく、誰がいるか、何の話をしているか、会話がどれくらい続いているか、現在時刻、本人が今どういう気分かによって入室・退出を変える。

## Room rules

- 登録キャラクターは最大10人程度を想定
- 同時入室は最大4人
- 基本体験では3人を通常メンバーとして部屋に置き、第4席を流動的なGuest Slotとして使う
- Guest Slotは現在話題へのInterest、Relationship、Desireなどで入室候補を選ぶ
- 満室時に新しいキャラクターが入りたがっても、既存キャラクターを機械的に追い出さない
- ユーザーの明示的な招待は通常の自動入室より強いシグナルとして扱う

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
- 現在話題へのInterest
- Semantic Similarityで推定されたInterest
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

第4席では特に`topic_interest`を強めに使い、現在の話題が得意・好きなキャラクターが自然に寄ってきやすくする。

正確な重みは実装・試遊をしながら調整する。

## Base three + Guest Slot

通常は3人のキャラクターがチャット空間にいて、話題によって4人目が入りやすくなる。

例:

```text
SAYO
TT
A
[ Guest Slot ]
```

物理Topicが始まり、Bの`physics interest`が高ければGuest Slotへのenter scoreが上がる。

ゲームTopicへ移れば、別キャラクターが候補になってよい。

これにより10人全員を常時会話させず、話題に応じた入れ替わりを作る。

## Conversation duration

会話時間は2種類を区別する。

### room_conversation_duration

現在の部屋で会話自体がどれくらい続いているか。

主に未入室キャラクターのPresence判定に使う。

### participation_duration

そのキャラクター自身が現在の会話へ何分参加しているか。

主に以下へ使う。

- 疲労 / Mood
- 退出しやすさ
- Relationshipのセッション更新
- 長時間滞在によるrecent presence補正

## Time of day

現在時刻とLife CycleをPresenceへ反映する。

- 自由時間の中心: 通常の入室判定
- 睡眠時刻が近い: 入室スコア低下、退出スコア上昇
- `SLEEP`: 新規入室不可

## Invitations

UIに招待ボタンを用意する。

招待は以下の2種類を許可する。

- User invitation: ユーザーが好きなキャラクターを明示的に呼ぶ
- Character invitation: キャラクターが別キャラクターを呼びたがる

ユーザー招待はGuest Slotへの優先度を強く上げる。ただしLife stateがSLEEP等の場合の扱いは設定で決める。

招待はRelationshipにも小さな正方向イベントとして反映してよいが、連打でAffectionを稼げないよう1セッション内の効果に上限・cooldownを置く。

## Leaving / Exit button

自律退出に加えて、UIに退出ボタンを用意する。

自律退出の候補要因:

- 睡眠時刻が近い
- `participation_duration` が長い
- 興味のない話題が続いている
- Tensionが高い
- `be_alone` / `rest` Desire
- Moodが疲労・眠気寄り

ユーザーの退出操作は強制的なUI操作として尊重する。

ただし、操作しただけで長期Relationshipを大きく悪化させない。必要ならComfort / Tensionへごく小さい一時補正にとどめる。

## Silence

部屋にキャラクターがいることと、常に会話が発生することを分離する。

複数人が同じ部屋にいても、数分以上誰も話さない時間を許容する。

`silence_tolerance` が低いキャラクターほど沈黙を破りやすく、高いキャラクターほど静かな時間をそのまま受け入れやすい。
