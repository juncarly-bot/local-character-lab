# Character Goal System v1

## Status

本仕様を正式採用する。

Character Goalは、短命なDesireより上位にある「そのキャラクターが中長期的に何をしたいか」を表す。

Character ProfileからGoalを設定・編集できるようにし、実行時の進捗や一時的な活性度はRuntime Stateとして分離する。

## Core distinction

```text
Character Profile Goal
= そのキャラクターが持つ中長期的な方向性

Runtime Goal State
= 現在どこまで進んでいるか / 今どれくらい活性化しているか

Desire
= 今この瞬間にやりたいこと

Conversation Seed
= 会話へ持ち込める具体的な話題・行動候補
```

基本フロー:

```text
Profile Goal
↓
Memory / Relationship / Mood / Current Context
↓
Runtime Goal State
↓
Desire
↓
Conversation Seed
↓
Conversation / Action
↓
Interaction Event
↓
Memory / Relationship / Goal progress
```

## Examples

### USER-directed

```text
Goal:
USERの物理の勉強を見守りたい

USERが疲れている
→ Desire: 無理していないか気にしたい

昨日試験だった
→ Desire: 結果を聞きたい

最近物理の話をしていない
→ Desire: 進み具合が少し気になる
```

### Character-to-character

```text
Goal:
TTともう少し仲良くなりたい
```

このGoalは以下の傾向を少し強めてよい。

- TTへ話しかける
- TTのInterestに合う話題へ反応する
- TTが困っている時にsupport Desireを出す
- TTへHandoffする
- 二人きりの時に会話Seedを提案する

ただしGoalだけでRelationship数値を直接増減させない。

```text
Goal
→ behavior / Desire / Seed
→ actual interaction
→ Interaction Event
→ Relationship update
```

## Profile configuration

Character Profileでは、Goalの定義を編集できる。

v1で最低限持つ項目:

```yaml
goal_definition:
  id: goal_123
  description: "USERの物理の勉強を見守りたい"
  type: relationship
  target: user
  horizon: medium
  strength: 70
  enabled: true
  source: user
```

主な編集項目:

- description
- type
- target
- horizon
- strength
- enabled / disabled

ユーザーが明示的に作成・編集したGoalは `source: user` とし、自動推定で勝手に上書きしない。

将来的にCharacter EngineからGoal候補を提案してもよいが、その場合は `source: inferred` や `source: generated` として区別する。

## Runtime Goal State

進捗や一時状態はCharacter Profileそのものへ書き込まず、Runtime側に保持する。

例:

```yaml
goal_state:
  goal_id: goal_123
  status: active
  progress: 35
  activation: 62
  created_at: ...
  last_progress_at: ...
```

候補status:

- active
- completed
- paused
- abandoned
- replaced

`progress` や `activation` の正確な更新式は実装・実測で調整可能とする。

## Goal activation

Goalは常に会話を支配しない。

現在の状況から関連性がある時だけ強く効かせる。

主な入力:

- Memory
- Relationship
- Mood
- Desire
- Topic
- target character presence
- time
- recent Goal activation
- cooldown
- current USER need

例えば「USERの物理の勉強を見守りたい」というGoalがあっても、USERが旅行の話をしている最中に毎回物理へ戻さない。

Goalは会話を乗っ取る命令ではなく、行動傾向への長期バイアスとして扱う。

## Goal evolution

Goalは固定台詞ではない。

同じGoalから、状況に応じて異なるDesire / Seedが生まれる。

Goalは以下の変化を持てる。

- progress
- pause
- completion
- replacement
- abandonment
- temporary low activation

Conflictなどにより、一時的に別のGoalが優先される場合もある。

例:

```text
Goal:
TTと仲良くなりたい

↓ conflict

temporary priority:
まず気まずさを解消したい
```

## Active Goal cap

Goalを無制限に活性化しない。

v1では1 Characterあたり、おおむね3-5個程度のactive Goalを想定する。

これは厳密な永続上限というより、Runtime Contextへ渡すGoal数と挙動の解釈可能性を保つための設計目安。

## UI

Character Profileに `Goals` セクションを追加する。

通常UIでは:

- Goal description
- target
- horizon
- enabled
- progressの短い表示
- status

を確認できる。

編集画面では:

- Add Goal
- Edit Goal
- Enable / Disable
- Delete / Archive

を提供できる。

Runtime由来の `progress` や `activation` は原則ユーザーが直接数値編集する対象にはしない。

Developer Modeでは:

- current activation
- progress reason
- source Memory
- source Relationship
- generated Desire
- generated Seed
- Directorへのscore contribution

を確認できるようにする。

## Important principle

```text
Goal != script
Goal != forced dialogue
Goal != direct Relationship reward
```

Goalは「そのキャラクターが何を大切にし、どちらへ進もうとしているか」を長い時間軸で表す。

実際の発話はCharacter Profile、Relationship、Mood、Conversation Act、Topic、Memoryなどと統合した上でMain Modelが生成する。
