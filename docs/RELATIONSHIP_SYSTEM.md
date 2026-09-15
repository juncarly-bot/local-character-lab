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

## Conversation-driven change policy

Relationshipはメッセージ1通ごとに機械的に加点しない。

会話内容、会話の質、Character Interests、そのキャラクターの実参加時間などをConversation Analysisで評価し、会話イベントまたはセッション単位で更新する。

特に、キャラクターが好きな話題を相手と楽しく話した場合は、AffectionやComfortが上がりやすくなる。

概念例:

- ordinary shared time -> Familiarity中心
- enjoyable favorite-topic conversation -> Affection / Comfort中心
- serious consultation / meaningful disclosure -> Trust中心
- positive long conversation -> Familiarity + small Affection / Comfort
- repeated unpleasant interaction -> Comfort低下、Tension上昇、必要ならAffection小低下
- promise broken -> Trust低下
- reconciliation -> Tension低下、Comfort回復

同じ長さの会話でも、本人のInterestsやValuesと合うかどうかで変化量は異なる。

Relationship growth should use `participation_duration`, not total room duration. A character who entered for only five minutes should not receive the same relationship update as a character who participated for one hour.

## Anti-farming principle

「物理」という単語を繰り返すだけでAffectionが上がり続けるような設計にはしない。

そのため:

- ターン単位ではなくセッション / conversation-event単位で評価する
- 同一内容の反復は効果を弱める
- 1セッションあたりの変化量に上限を持たせる
- 高い数値ほど成長を緩やかにすることを検討する

具体的なdelta表と上限値は実装前に正式化する。

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
