# Decision 005: Affection never becomes negative

Status: Adopted

## Decision

`Affection` は `0-100` の範囲とし、マイナス値を取らない。

関係悪化は主に `Trust`、`Comfort`、一時値 `Tension`、必要に応じたAffection低下で表現する。

## Reason

「嫌い度」を1本の負の好感度で表すよりも、

- 好きだが今は気まずい
- 親しいが信用を失った
- 信頼しているが一緒にいると疲れる

といった複雑な関係を表現しやすくするため。
