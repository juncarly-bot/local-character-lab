# UI Specification

## Base UI

会話画面は新躍進政策のチャット欄を参考にする。

画面上部または会話参加者表示にキャラクターアイコンを置き、アイコンをタップするとCharacter Detailへ移動する。

## Character Detail

表示候補:

- アイコン / 名前
- 現在状態: `SLEEP` / `FREE`
- Room: `IN` / `OUT`
- Mood
- 現在のDesire
- Personality
- Conversation behavior
- Interests
- Values
- Relationships
- Memory

## Editable sections

### Personality

スライダーまたは数値入力で `0-100` を編集。

- sociability
- initiative
- curiosity
- emotionality
- humor
- assertiveness

### Conversation

- talkativeness
- interruption
- topic_change
- silence_tolerance

### Interests

任意の興味項目を追加・削除できる。

例:

```text
物理学          70
ゲーム          35
食べ物          25
人間関係        40
テクノロジー    80
[ + 興味を追加 ]
```

### Values

自然言語で追加・編集・削除できる。

## Relationship view

相手ごとに以下を表示する。

- Familiarity
- Affection
- Trust
- Comfort
- Tension
- Relationship Tags

通常表示では「親しい」「知人」などの要約も併記してよい。

## Desire view

Current Desireは基本的に閲覧用。

例:

```text
昨日USERが話していた物理の問題が解けたか聞きたい
Strength: 72 / 100
Source: 昨日の会話
```

## Developer Mode

キャラクターの内部判断を確認できるデバッグ表示を用意する。

例:

```text
現在: 入室候補

物理の話題への興味      +12
USERとのAffection        +18
TTが部屋にいる           +9
initiative               +4
疲労                      -6
recent_presence          -10

enter_score: 71 / 100
```

これは最終ユーザー体験だけでなく、パラメータ調整・不具合解析にも使う。

## Principle

普段のチャット画面はキャラクターとの会話を邪魔しないシンプルな構成にし、細かい数値・設定はアイコンタップ後のCharacter Detailへ退避する。
