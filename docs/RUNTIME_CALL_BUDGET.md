# Runtime Call Budget v1

## Goal

ローカルLLMに世界状態の全処理を丸投げせず、意味理解と言語生成だけを担当させる。

時間・数値・状態遷移・上限・cooldownなどはCharacter Engineの決定論ロジックで処理する。

この方針はモデルサイズに依存しない。8B級モデルでは十分な意味理解余力を期待し、軽量モデルへ置き換える場合も同じAPI境界を維持する。

## Responsibility split

### LLM

- Character response generation
- Conversation Analysis
- Interaction Event extraction
- 必要時のDesire表現生成
- セッション終了時のMemory要約

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

```text
analysis if needed
+ character A
+ character B
```

目標: `2-3 calls`

3人以上が続けて反応するのは特殊場面のみ。

## Hard limits

初期v1では以下を採用する。

- 通常ユーザーターン: `1-2` LLM calls
- 複数キャラ反応: `2-3` calls
- foreground hard cap: `4` calls / user turn
- 4人全員に毎回返答させない
- local inferenceは原則逐次実行し、同一モデルを複数同時推論させない

4 callを超えそうな処理はbackground queueへ送るか次のsegmentへ延期する。

## Conversation Analysis refresh

毎発言でフル解析しない。

以下のいずれかで更新候補にする。

- 前回分析から `3-5` utterances経過
- 明確なtopic shift
- 新しいキャラクターの入室・主要キャラクターの退出
- conflict / disclosure / promise等の重大イベント候補
- 前回分析から数分経過
- analysis segment終了

短いやり取りでは直前のAnalysisを再利用する。

実装後に速度を測り、utterance数・時間閾値は調整可能とする。

## Background path

以下はforeground responseから外す。

- Memory summarization
- long-term Relationship commit
- Desire refresh when urgentではない
- topic canonicalization
- session summary

ユーザーへの返答を先に表示し、その後に処理できる構造を優先する。

## Memory calls

毎ターン「記憶すべきか」をLLMへ聞かない。

Conversation Analysisの `importance` とInteraction Eventsからコード側でMemory Candidateを作る。

LLMによるMemory summaryは主に以下でまとめて行う。

- analysis segment終了
- room session終了
- significant event発生時

通常は複数candidateを1回のcallでまとめる。

## Desire calls

Desire候補の発火条件はできるだけengine側で作る。

例:

- unresolved memory
- promised follow-up date
- favorite topic currently active
- specific person is present
- boredom / social desire

LLMは必要時に「その欲求をキャラクターらしい行動・発話へどう変換するか」を担当する。

全キャラクターへ毎ターンDesire生成を行わない。

## Autonomous conversation

ユーザー不在時の会話は無限ループさせない。

初期v1:

- 1 autonomous chainあたり最大 `3` generated utterances
- chain終了後にcooldownを置く
- 沈黙を正常状態として扱う
- 新しいDesire、入室、重要topic変化などがなければ再開しない

## Fallback and validation

構造化出力はJSON schema等で検証する。

- valid -> accept
- minor recoverable error -> deterministic repair where safe
- invalid -> 1回だけretry
- repeated invalid -> previous valid analysisを保持し、その処理をskip

分析失敗で会話全体を止めない。

## Design principle

8Bを「世界状態を全部管理する神様」にしない。

LLMは意味を読み、キャラクターとして言葉を作る。
Character Engineは世界の時間、状態、数値、上限、整合性を守る。
