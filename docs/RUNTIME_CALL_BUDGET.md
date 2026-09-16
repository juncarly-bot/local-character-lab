# Runtime Call Budget v1

## Goal

ローカルLLMに世界状態の全処理を丸投げせず、意味理解と言語生成だけを担当させる。

時間・数値・状態遷移・上限・cooldownなどはCharacter Engineの決定論ロジックで処理する。

## Responsibility split

### Main local LLM

- Character response generation
- 複雑なConversation Analysis
- Interaction Event extraction
- 必要時のDesire表現生成
- セッション終了時のMemory要約
- 高度な意味理解が必要なKnowledge / News interpretation

### Deterministic engine

- SLEEP / FREE
- room capacity
- Presence score計算
- Relationship数値更新
- Mood数値・状態更新
- cooldown
- session / segment timer
- call budget enforcement
- Memory candidate queue
- schema validation / retry policy
- simple topic / domain lookup

## Foreground path

ユーザーが返答を待つ経路では、LLM呼び出し数を小さく保つ。

### Normal case

```text
USER message
  -> reuse recent analysis when valid
  -> speaker selection by engine
  -> 1 character generation
```

目標: `1 call`

### Analysis refresh needed

```text
USER message
  -> Conversation Analysis
  -> speaker selection
  -> 1 character generation
```

目標: `2 calls`

### Multiple character reactions

通常は1人、必要時のみ2人まで反応する。

目標: `2-3 calls`

## Hard limits

- 通常ユーザーターン: `1-2` LLM calls
- 複数キャラ反応: `2-3` calls
- foreground hard cap: `4` calls / user turn
- 4人全員に毎回返答させない
- local inferenceは原則逐次実行する

## Conversation Analysis refresh

毎発言でフル解析しない。

更新候補:

- 前回分析から `3-5` utterances経過
- 明確なtopic shift
- 新しいキャラクターの入室・主要キャラクターの退出
- conflict / disclosure / promise等の重大イベント候補
- 前回分析から数分経過
- analysis segment終了

短いやり取りでは直前のAnalysisを再利用する。

## Background path

以下はforeground responseから外す。

- Memory summarization
- long-term Relationship commit
- non-urgent Desire refresh
- topic canonicalization
- News collection / summarization
- session summary

ユーザーへの返答を先に表示し、その後に処理できる構造を優先する。

## Lightweight processing hierarchy

遅延対策として、最初から全処理をmain LLMへ送らない。

優先順位:

```text
Deterministic code
   ↓
Embedding / similarity
   ↓
Optional lightweight model
   ↓
Main 8B-class model
```

### Code

- room rules
- timers
- thresholds
- known topic lookup
- simple Relationship / Presence arithmetic

### Embedding / similarity

- 未設定Topicと既存Interest / Expertiseの近さ
- Topic canonicalization候補
- News CardとCharacter Interestのmatching

### Optional lightweight model

実測でmain modelの遅延が問題になった場合のみ追加する。

候補仕事:

- Topic classification
- simple Conversation Act classification
- importance estimation
- short summarization
- simple event candidate extraction

0.5B-1.5B級のQwen等を候補にできるが、v1では必須依存にしない。

まずmain model + deterministic code + embeddingで計測し、ボトルネックが確認されてから分業する。

### Main model

- final character utterance
- ambiguous semantic judgment
- subtle relationship / event interpretation
- natural Desire wording

main modelは「役者」と「高度な読解係」に集中させる。

## Memory calls

毎ターン「記憶すべきか」をLLMへ聞かない。

Conversation Analysisの`importance`とInteraction Eventsからコード側でMemory Candidateを作る。

LLMによるMemory summaryは主に以下でまとめて行う。

- analysis segment終了
- room session終了
- significant event発生時

## Desire calls

Desire候補の発火条件はできるだけengine側で作る。

例:

- unresolved memory
- promised follow-up date
- favorite topic currently active
- specific person is present
- boredom / social desire
- interesting News Card available

LLMは必要時に「その欲求をキャラクターらしい行動・発話へどう変換するか」を担当する。

## Autonomous conversation

ユーザー不在時の会話は無限ループさせない。

初期v1:

- 1 autonomous chainあたり最大 `3` generated utterances
- chain終了後にcooldownを置く
- 沈黙を正常状態として扱う
- 新しいDesire、入室、重要topic変化などがなければ再開しない

## News / realtime latency

News retrievalを通常foreground会話へ入れない。

```text
background News Collector
→ News Cache
→ foregroundはlocal cache参照
```

本当に「今」の情報が必要なときだけLive Retrievalを行う。

## Fallback and validation

構造化出力はJSON schema等で検証する。

- valid -> accept
- minor recoverable error -> deterministic repair where safe
- invalid -> 1回だけretry
- repeated invalid -> previous valid analysisを保持し、その処理をskip

分析失敗で会話全体を止めない。

## Design principle

main LLMを「世界状態を全部管理する神様」にしない。

LLMは意味を読み、キャラクターとして言葉を作る。
Character Engineは世界の時間、状態、数値、上限、整合性を守る。
