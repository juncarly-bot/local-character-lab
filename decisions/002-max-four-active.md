# Decision 002: Maximum four active characters

Status: Adopted

## Decision

登録キャラクターは最大10人程度を想定するが、同時に同じ部屋へ参加できるキャラクターは最大4人とする。

## Reason

- 会話が騒がしくなりすぎることを防ぐ
- 発言順や存在感を追いやすくする
- Contextと推論コストを抑える
- 誰が入るか・誰が退出するかというPresenceの意味を強くする

満室時は、新しいキャラクターを入れるために既存キャラクターを機械的に追い出さない。
