# Knowledge / Interest System v1

## Goal

全キャラクターが同じ基盤LLMを使っていても、全員が同じ話題に同じ深さで答える状態を避ける。

キャラクターごとに「何が好きか」「何に詳しいか」「その具体的情報を知っているか」を分離する。

## Core concepts

### Interest

その話題をどれくらい好み、参加したいか。

- 範囲: `0-100`
- Presence、Desire、発言意欲へ強く影響する
- Expertiseとは独立

### Expertise

その分野にどれくらい詳しいか。

- 範囲: `0-100`
- 発話で許される知識の深さへ影響する
- Interestとは独立

例:

```text
physics:
  interest: 95
  expertise: 25
```

このキャラクターは物理の話が大好きだが、主に質問・反応側になる。

逆に:

```text
physics:
  interest: 50
  expertise: 95
```

自発的には話題に入りにくいが、聞かれればかなり詳しく答えられる。

### Knowledge Access

その具体的な情報を実際に知っているか。

状態候補:

- `UNKNOWN`: 知らない
- `HEARD`: 聞いたことがある
- `KNOWN`: 概要を知っている
- `DETAILED`: 詳しく知っている

ニュース、ユーザー個人情報、過去の会話などはキャラクターごとにKnowledge Accessが異なってよい。

### Confidence

知識に対する確信度。`0-100`。

低Confidenceなら「たしか」「聞いたことあるけど」など曖昧な表現を許す。

### Source

知識の由来を可能なら記録する。

候補:

- `model_prior`
- `memory`
- `conversation`
- `news`
- `retrieval`

## Do not configure every topic manually

全話題へInterest / Expertiseを手入力する設計にはしない。

ユーザーが設定するのは以下だけでよい。

1. 大分類
2. 特徴的な話題
3. 必要なら明示的な例外

それ以外は自動推定する。

## Hierarchical fallback

未設定TopicのInterest / Expertiseは以下の順で解決する。

```text
Explicit Topic Setting
        ↓
Semantic Similarity to configured topics
        ↓
Domain Setting
        ↓
Character Background
        ↓
Personality / Default
```

### Domain settings

大分類は10-15個程度を想定する。

例:

```yaml
domains:
  science:
    interest: 75
    expertise: 65
  technology:
    interest: 60
    expertise: 70
  entertainment:
    interest: 80
    expertise: 50
  food:
    interest: 55
    expertise: 35
  travel:
    interest: 65
    expertise: 40
```

### Explicit topics

キャラクター性を強く出したい項目だけ個別設定する。

```yaml
topics:
  physics:
    interest: 95
    expertise: 85
  astronomy:
    interest: 90
    expertise: 70
  cars:
    interest: 20
    expertise: 15
```

### Semantic similarity

未設定Topicが出た場合、embedding等で既存Topicとの近さを測る。

例:

```text
gravitational_waves
↔ physics     0.91
↔ astronomy   0.84
↔ games       0.08
```

この結果からInterest / Expertiseを補間する。

### Character background

さらに未解決なら、知識背景を利用する。

```yaml
knowledge_background:
  science: advanced
  humanities: basic
  technology: intermediate
  cooking: basic
```

### Personality fallback

完全に未知の話題では、Expertiseは低めに保ち、Interestは`curiosity`等で補正する。

高curiosityなら「知らないが興味を持つ」キャラクターを表現できる。

## Knowledge depth guard

同じ8Bが全知識を内部的には持っていても、キャラクターのExpertise以上の説明を自発的に出さない。

概念的な深さ:

- Level 0: 質問・相槌中心
- Level 1: 一般常識
- Level 2: 少し詳しい説明
- Level 3: 大学初級程度
- Level 4: 専門的説明

Expertiseから許可depthを決め、生成Promptへ渡す。

必要なら生成後に「設定Expertiseに対して専門的すぎないか」を軽く検査し、再生成できる構造にする。

## Knowledge propagation

会話を聞くことでKnowledge Accessは変化してよい。

例:

```text
UNKNOWN -> HEARD -> KNOWN
```

ただし、その場にいなかったキャラクターへ自動伝播させない。

Memoryの`known_by` / `observed_by`と整合させる。

## Inferred interests

会話履歴から新しい興味を推定してもよいが、ユーザー設定と混ぜない。

```yaml
hot_springs:
  interest: 72
  source: inferred
```

ユーザーが手動設定した場合:

```yaml
hot_springs:
  interest: 90
  source: user
```

`source: user`は自動推定で上書きしない。

## UI concept

```text
興味

[大分類]
科学          75
技術          60
ゲーム        90
食べ物        55
旅行          65

[特徴的な話題]
物理          95
宇宙          90
車            20

[自動推定された興味]
温泉          72
AI            68
数学          82
```

Expertiseも同様に確認・編集できるが、UIではInterestと混同しない。

## Design principle

全員が同じモデルを使うことと、全員が同じ知識を持つことは別問題として扱う。

「好きだが詳しくない」「詳しいが自分からは話さない」「聞いたことだけある」「その場にいなかったので知らない」という差を積極的に会話へ出す。
