# Relationship System

## Goal

単一の「好感度」だけでは表現できない関係を扱う。

例:

- 好きで一緒にいると楽しいが、重要なことは任せにくい
- それほど親密ではないが、とても信用している
- 親しいが、今だけ少し気まずい

## Long-term axes

各関係は方向ごとに保存する。

`A -> B` と `B -> A` は別の値を持つ。

### Familiarity

範囲: `0-100`

どれだけ相手を知っているか。付き合いの長さや共有体験の蓄積を表す。

### Affection

範囲: `0-100`

親しさ・好意。マイナスにはしない。

関係悪化を「負の好感度」で表すのではなく、Trust、Comfort、Tensionなどとの組み合わせで表現する。

### Trust

範囲: `0-100`

どの程度、相手を信用できるか。大切なことを任せる、秘密を話す、といった行動に影響する。

### Comfort

範囲: `0-100`

相手と一緒にいてどれだけ気楽か。会話の自然さ、沈黙への耐性、入室したさなどに影響する。

## Temporary axis

### Tension

範囲: `0-100`

気まずさ、苛立ち、喧嘩直後の空気など、一時的な関係状態。

Tensionは時間経過で自然に減衰する。ただし、原因となった出来事はMemoryに残り得る。

## Relationship Tags

数値だけでは関係の意味を表現できないため、タグを別途持つ。

候補:

- `friend`
- `close_friend`
- `senior_junior`
- `sibling_like`
- `rival`
- `protector`
- `admirer`
- `childhood_friend`

Tagsは主として関係の種類を表し、数値は現在の深さ・状態を表す。

## Directionality

例:

```yaml
sayo_to_tt:
  familiarity: 80
  affection: 72
  trust: 82
  comfort: 70
  tension: 5
  tags: [close_friend]

tt_to_sayo:
  familiarity: 80
  affection: 65
  trust: 90
  comfort: 58
  tension: 5
  tags: [close_friend]
```

同じ2人でも感情・信頼・気楽さは非対称でよい。

## Initial values

関係性は全員0から始めず、キャラクター設定時に初期値を与える。

初期値の目安は `20-80` を中心とし、100は長期的に到達する特別な状態として残す。

例:

```text
初対面に近い: 10 / 35 / 20 / 25
普通の知人:   35 / 45 / 40 / 40
友人:         60 / 65 / 60 / 65
親しい友人:   80 / 80 / 78 / 85
```

順序は Familiarity / Affection / Trust / Comfort。

数値はテンプレートであり、正式な初期値は各キャラクターごとに設定する。

## Change policy

日常の挨拶だけで数値を上げ続けない。意味のある出来事に対して変化させる。

例:

- 一緒に長く楽しく過ごした: Familiarity小増加、Affection小増加
- 秘密を打ち明けた: Trust増加
- 助けてもらった: Affection / Trust増加
- 約束を破った: Trust低下
- 強く言い争った: Comfort低下、Tension大幅上昇、必要ならAffection小低下
- 仲直りした: Tension低下、Comfort回復

変化量は後でイベント表として正式化する。

## Use in behavior

Relationshipは以下へ影響させる。

- 入室したさ
- 招待への応答
- 発言相手の選択
- 秘密や弱音を話すか
- 冗談やツッコミの強さ
- 沈黙の気まずさ
- 退出判断
- 自発的欲求の発生

総合的な1つのAffinity値は内部の正規状態として保存しない。必要なら表示用に4軸から計算する。
