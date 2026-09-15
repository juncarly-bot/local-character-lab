# Life Cycle

## v1 state model

生活状態は最初から複雑な一日シミュレーションにせず、以下の2状態から始める。

- `SLEEP`
- `FREE`

`SLEEP` 中は原則として入室・呼び出し不可。

`FREE` 中のみ、入室・呼び出し・自発会話の候補になる。

## Daily cycle

各キャラクターは睡眠開始時刻と起床時刻を持つ。

例:

```yaml
life:
  sleep_start: "00:30"
  sleep_end: "08:30"
```

将来的には曜日差や多少の揺らぎを入れてもよいが、v1では予測可能でデバッグしやすいことを優先する。

## Transition

```text
SLEEP -> FREE
FREE  -> SLEEP
```

入室状態は生活状態とは別に管理する。

```text
FREE + OUT
FREE + IN
SLEEP + OUT
```

原則として `SLEEP + IN` は許さず、睡眠移行時に自然な退出処理を行う。

## Design goal

生活状態を作る目的は詳細な生活ゲームを再現することではなく、「今は寝ているから来ない」「自由時間だから話しに来る」という存在感を作ることにある。
