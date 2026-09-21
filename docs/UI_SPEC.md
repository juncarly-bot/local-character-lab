# UI Specification

## Base UI

会話画面は新躍進政策のチャット機能を視覚・操作上の参考にする。

普段のチャット画面は会話を邪魔しない構成を優先し、詳細な数値や設定はCharacter Profileへ退避する。

チャット画面上部または参加者表示には、現在Roomにいるキャラクターのアイコンを表示する。

- アイコンをタップすると、そのキャラクターのCharacter Profileを開く
- Profileを閉じると同じ会話位置へ戻れる
- 招待 / 退出操作はチャット画面から行える
- 第4席Guest Slotも参加者表示上で確認できる

Character Profileはチャットから開く場合とCharacters一覧から開く場合で同じデータ・同じ画面コンポーネントを使う。

## Main navigation

MVPでは少なくとも以下を用意する。

- `Chat`
- `Characters`
- `Settings`

必要ならDeveloper Mode有効時のみDebug入口を追加する。

## Characters screen

チャットとは別に、登録キャラクターを一覧で確認できる画面を用意する。

一覧カード例:

```text
[SAYO icon]  SAYO
FREE / Room IN
Mood: HAPPY
USERとの関係: 親しい

[TT icon]    TT
FREE / Room OUT
Mood: QUIET
USERとの関係: 友人
```

一覧では情報を詰め込みすぎず、主に以下を表示する。

- アイコン
- 名前
- Life state: `SLEEP` / `FREE`
- Room state: `IN` / `OUT`
- 現在Moodの短い表示
- USERとのRelationship要約
- 必要なら現在の強いDesire / Seedがあることを示す小さなインジケータ

カードをタップするとCharacter Profileへ移動する。

Characters画面では将来的に以下も可能にする。

- キャラクター追加
- 並び替え
- 有効 / 無効
- 初期3人のResident指定
- アイコン変更

## Character Profile

Character Profileは「設定画面」だけではなく、そのキャラクターが現在どういう人物・状態なのかを見る場所として作る。

上部にはIdentity Headerを置く。

```text
[large icon]
SAYO
FREE / Room IN
Mood: HAPPY / SOCIAL

USERとの関係: 親しい
Affection 86
```

その下をセクションまたはタブで整理する。

推奨構造:

1. Overview
2. Personality
3. Interests & Expertise
4. Relationships
5. Memory
6. Goals
7. Desire & Seeds
8. Behavior
9. Developer

モバイルでは横タブを大量に並べるより、上部の少数タブ + 折りたたみSection、または縦スクロールSectionを優先する。

## 1. Overview

キャラクターを一目で把握する画面。

表示候補:

- アイコン / 名前
- キャラクター説明
- speech style要約
- Life state
- Room state
- Mood
- social energy
- sleepiness
- Current Desire
- 現在の強いConversation Seed
- USERとのRelationship要約
- 最近よく出たTopics

ここは日常的に見る画面なので内部数値を出しすぎない。

## 2. Personality

原則 `0-100`。

編集可能:

- `sociability`
- `initiative`
- `curiosity`
- `emotionality`
- `humor`
- `assertiveness`

スライダー + 数値表示を基本とする。

数値だけで意味が分かりにくいため、短い説明文を併記する。

例:

```text
好奇心  78
知らない話題にも比較的強く興味を示します
```

## 3. Interests & Expertise

InterestとExpertiseを混同しないUIにする。

### Interest

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

### Expertise

同じTopicについて、知識の深さを別表示する。

```text
得意分野

科学          70
技術          80
ゲーム        55

物理          85
宇宙          70
車            15
```

ユーザー設定値と自動推定値は視覚的に区別する。

- `source: user` は自動推定で上書きしない
- `source: inferred` は推定値として表示する
- 推定値をユーザーが編集したら `source: user` へ昇格する

未知TopicはKnowledge Systemのfallbackで自動評価するため、全Topicの手入力を要求しない。

## 4. Relationships

Relationshipは相手ごとに確認する。

表示項目:

- Familiarity
- Affection
- Trust
- Comfort
- Tension
- Relationship Tags

例:

```text
USER
親しい
Familiarity 82
Affection   86
Trust       78
Comfort     88
Tension      2

TT
close_friend
Familiarity 80
Affection   72
Trust       82
Comfort     70
Tension      5
```

通常表示では数値とともに「親しい」「友人」「少し気まずい」などの短い要約を出してよい。

Relationshipの方向性は内部では保持するが、通常UIで両方向を同時表示して複雑にしすぎない。Developer Modeでは双方を確認可能にする。

## 5. Memory

そのキャラクターが知っている / 覚えているMemoryを確認・管理する。

Memoryはshared Storeに保存し、Characterごとの `known_by` で知識差を維持する。

通常UIでは内部数値より自然文を優先する。

例:

```text
Memory

最近の記憶
- 昨日USERが試験について話した
- TTと勉強の話をした

大切な記憶
- USERが約束を守ってくれた

意見・好み
- 静かなEVには少し好感を持っている

Follow-up
- USERに試験結果を聞きたい
```

Memory詳細では以下を確認できる。

- summary
- type
- source
- who knows
- perspective
- importance
- confidence
- retention
- created_at
- revision history

ユーザー操作:

- Correct
- Pin / Protect
- Forget
- Delete
- Advanced: known_by correction

Correctは旧Memoryを直接上書きせず、新Memoryを作成して旧Memoryを `superseded` にする。

Forgetは通常retrievalから外すsoft operation、DeleteはStoreからのhard deleteとして区別する。

Developer ModeではMemory ID、importance score breakdown、known_by、retention、retrieval score、source_event_id、revision chainなどを確認できる。

詳細は `docs/MEMORY_SYSTEM.md` を参照する。

## 6. Goals

Character GoalはCharacter Profileから設定・編集できる正式な領域とする。

通常表示例:

```text
Goals

USERの物理の勉強を見守りたい
Target: USER
Horizon: medium
Status: active
Progress: 35%

TTともう少し仲良くなりたい
Target: TT
Horizon: medium
Status: active
Progress: 20%
```

編集可能項目:

- description
- type
- target
- horizon
- strength
- enabled / disabled

操作候補:

- Add Goal
- Edit Goal
- Enable / Disable
- Delete / Archive

`progress` や現在の `activation` はRuntime Stateであり、通常UIから直接数値編集しない。

Developer ModeではGoal activation、progress reason、source Memory / Relationship、そこから生成されたDesire / Seedを確認できる。

## 7. Desire & Seeds

Current DesireとConversation Seedを表示する。

Current Desireは基本的に閲覧用。

例:

```text
現在の欲求
USERに昨日の試験結果を聞きたい
Strength: 72 / 100
```

Seed表示例:

```text
話したいこと
- 昨日の試験結果を聞きたい [memory]
- ゲームについて少し話したい [interest]
- 甘いものが食べたい [random_thought]
```

通常UIでは内部scoreを必ずしも表示しない。

## 8. Behavior

会話傾向を編集する。

- `talkativeness`
- `interruption`
- `topic_change`
- `silence_tolerance`

必要に応じてLife Cycle設定もここから編集できる。

## 9. Values / Identity

キャラクターのIdentity、speech style、Valuesは明示的に編集可能にする。

Valuesは自然言語のリスト形式。

例:

```text
- 努力している人を応援したい
- 無理をしている人は放っておけない
- 一人で過ごす時間も大切にする
```

Character Profileを単なる数値パネルにせず、「どんな人物なのか」が読めることを重視する。

## Developer Mode

Developer Modeでは通常UIで隠している内部判断を確認できる。

例:

```text
Speaker decision: SAYO
speak_score: 73

topic_interest      +18
desire              +14
relationship         +9
initiative           +7
recent_speaker       -4
random               +2

Conversation Act: question
Expertise: 32
Knowledge depth: Level 1
Knowledge Access: KNOWN
```

他にも以下を確認可能にする。

- Guest enter score
- Seed proposal score
- Topic Lifecycle state
- recent Conversation Acts
- Memory retrieval結果
- Runtime Contextに実際に渡されたProfile slice
- cooldown / penalties

これはパラメータ調整・不具合解析・会話品質改善の主要ツールとして扱う。

## Chat-to-Profile UX

チャット中のキャラクターアイコンタップは、会話から離れた感覚を弱くする。

推奨:

- Androidではfull-screen sheetまたはmodal sheetでProfileを表示
- 閉じると元のscroll位置・入力途中テキストを維持
- Profile内からRelationship / Memory等の詳細へ移動可能
- Invite / ExitのようなRoom操作はチャットへ戻らなくても実行可能にしてよい

Characters一覧から開いた場合も同じCharacter Profileを使う。

## Principle

UIは2つの入口を持つ。

```text
Chat
  -> character icon
  -> Character Profile

Characters
  -> character list
  -> Character Profile
```

Character Profileは詳細に作るが、Chat画面そのものは軽く保つ。

「会話中はキャラクターを見る」「一覧では世界の住人全体を見る」という役割分担にする。
