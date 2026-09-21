# Character Goal design memo - 2026-09-17

## Status

Promoted on 2026-09-19. The core Character Goal concept is now canonical in `docs/CHARACTER_GOAL_SYSTEM.md`. This file remains as the original design history.

## Character Goal concept

Add a layer above short-lived Desire so each character can retain a longer-term direction such as what they want to do for the USER, for themselves, or toward another character.

Examples:

```text
Short-term:
USERを元気づけたい

Medium-term:
USERの物理の勉強を見守りたい

Toward TT:
もう少しTTと仲良くなりたい
```

The important distinction is:

```text
Goal
= 長期的な方向性

Desire
= 今この瞬間にやりたいこと

Seed
= 実際に会話へ持ち込める話題候補
```

Conceptual flow:

```text
Goal
「USERの物理の勉強を見守りたい」
        ↓
current context / Memory / Relationship / Mood
        ↓
Desire
「昨日の問題が解けたか聞きたい」
        ↓
Seed
「そういえば昨日の問題、どうでしたの？」
        ↓
Conversation
        ↓
Memory / Relationship / Goal progress
```

The same Goal should be able to produce different Desires depending on context rather than repeating the same line or action.

Example:

```text
Goal:
USERの物理の勉強を見守りたい

USERが疲れている
→ 無理していないか聞きたい

昨日試験だった
→ 結果を聞きたい

最近物理の話をしていない
→ 進み具合が少し気になる

USERが自信をなくしている
→ 励ましたい
```

## Character-to-character Goals

Goals may target another character as well as the USER.

Example:

```text
Itako Goal:
TTともう少し仲良くなりたい
```

Possible behavioral consequences:

- TTの好きな話題へ少し反応しやすくなる
- TTへHandoffする確率が少し上がる
- TTが困っている時に助けるDesireが生まれやすくなる
- 二人きりになった時に話しかけるSeedが生まれやすくなる

A Goal should not directly add Relationship points. It changes behavior tendencies, and the resulting interactions may later change Relationship through the normal Interaction Event pipeline.

```text
Goal
→ behavior / Desire / Seed
→ actual interaction
→ Interaction Event
→ Relationship change
```

## Candidate schema

```yaml
goal:
  id: goal_123
  owner: itako
  type: relationship
  target: tt
  description: "TTともう少し仲良くなりたい"
  horizon: medium
  strength: 68
  progress: 25
  source:
    - relationship
    - memory
    - personality
  created_at: ...
  last_progress_at: ...
  status: active
```

Possible statuses:

- `active`
- `completed`
- `paused`
- `abandoned`
- `replaced`

## Goal evolution

Goals may complete, weaken, be replaced, or generate a new Goal after events.

Example:

```text
Goal:
TTと仲良くなりたい
↓
shared conversations
↓
Comfort / Familiarity rise
↓
progress increases
↓
completed
```

Conflict may temporarily replace the active direction:

```text
conflict
↓
Tension rises
↓
new Goal candidate:
「まず気まずさを解消したい」
```

## Active Goal cap candidate

Keep only a small number of active Goals per character, approximately 3-5, so the system remains interpretable and does not generate contradictory motivations.

Candidate slots:

```text
1. self goal
2. USER-directed goal
3. character-to-character goal
4. current life goal
5. optional special goal
```

## Architectural role

Character Goal could become the longer-timescale connective layer across existing systems:

```text
Memory
Relationship
Personality
Values
      ↓
Character Goal
      ↓
Desire
      ↓
Conversation Seed
      ↓
Conversation Director / Handoff / Presence
      ↓
Conversation
      ↓
Memory / Relationship / Goal progress
```

This is currently a design memo. Exact generation rules, progress math, decay, UI, and persistence should be decided before promoting it to the canonical specification.
