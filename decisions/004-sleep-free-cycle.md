# Decision 004: Start with SLEEP / FREE life cycle

Status: Adopted

## Decision

v1の生活状態は `SLEEP` と `FREE` の2つから始める。

入室・呼び出し・自発会話の候補になるのは原則 `FREE` 中のみとする。

## Reason

詳細な生活シミュレーションを作り込む前に、「寝ているから来ない」「自由時間だから話しに来る」という存在感を、単純でデバッグしやすい仕組みで実現するため。
