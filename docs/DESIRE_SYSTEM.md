# Desire System

## Goal

キャラクターを「話しかけられた時だけ反応するAI」にしない。

Personality、Interests、Values、Relationships、Memory、Moodから、その時点の自発的な欲求を発生させ、入室・発言・話題再開につなげる。

## Examples

```text
- TTと話したい
- 一人でいたい
- 昨日の話の続きを聞きたい
- USERに結果を聞きたい
- 物理の話をしたい
- 特に何もしたくない
```

## Structure

Desireは複数同時に存在してよい。

例:

```yaml
desires:
  - type: follow_up
    target: user
    strength: 72
    source: memory:2026-09-14-topic-physics
    summary: "昨日の物理の問題が解けたか聞きたい"

  - type: talk_to
    target: tt
    strength: 48
    source: relationship

  - type: solitude
    strength: 20
    source: mood
```

## Behavior

Desireは以下へ影響する。

- 入室スコア
- 誰へ話しかけるか
- どの話題を出すか
- 過去の話題を再開するか
- 招待するか
- 退出するか

## User control

Desireは基本的にキャラクター自身から生成される現在状態とする。

通常UIでは閲覧可能にするが、ユーザーが常時直接編集するものにはしない方針。

Developer Modeでは発生理由・強さ・参照Memoryを確認できるようにする。

## Important principle

Desireは台詞そのものではない。

「昨日の結果を聞きたい」という欲求を決めた後で、実際にどう言うかはCharacter Profileと3B LLMに生成させる。
