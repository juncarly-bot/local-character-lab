# Memory System v1

## Status

本仕様を正式採用する。

Memoryは会話ログそのものではなく、キャラクターが経験した世界から「後で意味を持つ情報・出来事・解釈」を保存する。

物理保存方式は **shared Memory Store + character-specific `known_by`** をv1の基本方針とする。

同じ出来事をCharacterごとのDBへ複製するのではなく、1つのMemoryに「誰が知っているか」「誰の視点か」を保持する。

## Core principles

```text
Memory != transcript
Memory != global truth
Memory != Character Profile

Memory
= Characterが後で参照できる、意味のある経験・事実・解釈
```

必須原則:

- その場にいなかったCharacterへMemoryを自動共有しない
- USERやCharacterの明示的な訂正を履歴付きで扱う
- 主観的解釈と客観的事実を分ける
- 全発言を保存しない
- 忘却は基本的に削除ではなくretrieval strengthの低下で表現する
- Main LLMへ全Memoryを渡さず、現在Contextに関連する少数だけretrievalする
- Offline Catch-up由来Memoryはsourceを追跡可能にする
- USERはMemoryを確認・訂正・固定・忘却・削除できる

## Store model

v1では1つのshared Memory Storeを採用する。

例:

```yaml
memory:
  id: mem_102
  type: personal_fact
  summary: "USERの試験日は9月18日"

  participants:
    - user
    - sayo

  known_by:
    sayo:
      confidence: 98
      salience: 76
      learned_at: ...

  perspective_owner: null

  topics:
    - study
    - exam

  importance: 78
  emotional_weight: 30

  retention: normal
  status: active

  source: user_statement
  source_event_id: event_381

  supersedes: []
  superseded_by: null

  reinforcement_count: 1
  created_at: ...
  last_recalled_at: ...
  last_reinforced_at: ...
```

`known_by` はCharacterごとに独立する。

同じMemoryでも、参加者は強く覚えており、後から聞いただけのCharacterはconfidence / salienceが低い状態を表現できる。

## Memory types

v1では以下を基本型とする。

### episode

出来事そのもの。

例:

```text
昨日、USERとSAYOが物理の問題について話した
```

### personal_fact

USERまたはCharacterについての比較的安定した事実。

例:

```text
USERは来週試験を受ける
TTは辛い食べ物が苦手
```

### relationship_event

Relationshipが変化した根拠となる出来事。

例:

```text
USERがSAYOとの約束を守った
TTが困っていた時にイタコが助けた
```

Relationship値そのものではなく「なぜ変わったか」を残す。

### follow_up

将来確認・再開したい未解決事項。

例:

```text
USERに試験結果を聞きたい
SAYOが途中で話せなかった件について続きを聞きたい
```

解決後はresolvedにしてretrieval優先度を下げる。

### opinion

Character自身の意見、好み、評価、考え方。

例:

```text
SAYOは静かなEVの乗り味を面白いと思っている
```

Opinionは経験によって変化してよい。新しいOpinionが旧Opinionを置き換える場合はrevision historyを残す。

### shared_experience

誰かと共有した経験そのものが将来の関係に意味を持つ場合。

例:

```text
TTとSAYOが夜に長く雑談して楽しく過ごした
```

### knowledge

会話、World Event、NewsなどからCharacterが新しく知った情報。

例:

```text
TTはUSERから潟沼が強い酸性湖だと聞いた
```

Knowledge SystemのAccessと矛盾しないようにする。

## Perspective

Memoryは「世界の真実」と「そのCharacterにはそう見えた」を区別する。

例:

```text
TT:
"SAYOが怒っているように見えた"

SAYO:
"怒ってはいなかった"
```

この場合、以下のような主観Memoryを許す。

```yaml
type: episode
perspective_owner: tt
summary: "TTにはSAYOが怒っているように見えた"
```

`perspective_owner != null` のMemoryを、客観的なFactとして他Characterへ伝播しない。

## Revision / correction

Memoryは誤りが判明しても原則として旧Memoryを直接書き換えない。

状態:

- `active`: 現在有効
- `superseded`: より新しいMemoryに置き換えられた
- `disputed`: 食い違いがあり真偽未確定
- `retracted`: 情報そのものが撤回された
- `resolved`: follow-upなどが完了した

例:

```text
Memory A
"試験日は9月17日"
status: superseded

        ↓ superseded by

Memory B
"試験日は9月18日"
status: active
supersedes: Memory A
```

USERによる明示的な訂正は高confidenceで扱う。

ただし訂正を聞いていないCharacterへ新Memoryを自動共有しない。

### Subjective conflict

```text
TT: SAYOは怒っていた
SAYO: 怒っていなかった
```

のような場合は、双方のPerspective Memoryを両立させてよい。

主観の違いを無理に1つのglobal truthへ潰さない。

## Memory Candidate pipeline

全発言を即座にMemory化しない。

```text
Conversation / World Event
↓
Memory Candidate
↓
Analysis Segment end
↓
filter
↓
deduplicate / revise
↓
importance evaluation
↓
save / short-term only / discard
```

通常はAnalysis Segment単位でまとめて処理し、同じ意味の候補を統合する。

## Automatic save importance

Candidateごとに0-100のimportance scoreを作る。

v1の評価軸:

```text
future_relevance       0-25
personal_relevance     0-20
relationship_impact    0-15
emotional_significance 0-15
follow_up_value        0-10
novelty                0-10
explicit_memory_signal 0-5
--------------------------------
total                  0-100
```

### Meaning of each factor

`future_relevance`
: 後日の会話・行動・Goalに役立つ可能性。

`personal_relevance`
: USERやCharacter固有の事実・好み・経験か。

`relationship_impact`
: Familiarity / Affection / Trust / Comfort / Tensionへ意味のある影響があったか。

`emotional_significance`
: 当事者にとって感情的に強い出来事か。

`follow_up_value`
: 後で確認、継続、約束履行などが必要か。

`novelty`
: 既存Memoryにない新情報か。

`explicit_memory_signal`
: 「覚えておいて」「大事」など、記憶する意図が明示されたか。

### Default thresholds

```text
0-34
discard
長期Memoryには保存しない

35-49
short-term only
現在Session / Segmentの文脈には残すが長期Memoryには保存しない

50-69
transient
長期Storeには入るが比較的早くretrieval優先度が下がる

70-84
normal
通常の長期Memory

85-100
important
強く残す長期Memory
```

`protected` は自動importance scoreだけでは付与しない。

USERが明示的にPin / ProtectしたMemoryや、手動で作成された固定Memoryだけを原則protectedとする。

## Type-specific save rules

数値scoreだけでは不自然になるケースがあるため、以下の最低ルールを持つ。

### Always preserve as revision

- Memory訂正
- retraction
- dispute resolution
- user manual edit

旧Memoryとのrevision linkを作る。

### Stable personal facts

USERまたはCharacterが明示した比較的安定した事実・好みで、今後の会話に利用可能なら最低でも`transient`候補にする。

繰り返し確認された場合は新規Memoryを増やさずreinforcementする。

### Follow-up / promise / future event

以下は原則保存する。

- 明示的な約束
- 後日確認する必要があること
- 試験、予定、締切など将来イベント
- 「あとで聞く」「結果を教える」など未解決の会話

未解決中はretrieval bonusを持つ。

### Relationship events

中程度以上のInteraction Eventは`relationship_event`として原則保存する。

例:

- meaningful support
- promise kept / broken
- meaningful praise
- apology
- reconciliation
- significant conflict
- important disclosure

軽い相槌や毎回の小さな好意は保存しない。

### Opinion

以下の場合に保存しやすくする。

- 強い好み・嫌悪
- Characterらしさに関わる意見
- 以前のOpinionから変化した
- 今後のTopic選択へ影響する

瞬間的な軽い感想はshort-termだけでよい。

### Offline World Event

Offline Catch-upで生成されたWorld Eventは通常と同じimportance policyを通す。

例:

```text
TTが15分休んだ
→ Memoryにしない

TTとSAYOが大きな喧嘩をした
→ episode + relationship_event候補
```

保存時は必ず:

```text
source: offline_simulation
source_event_id: ...
```

を持つ。

## Do-not-auto-save filters

以下は通常、自動で長期Memoryへ保存しない。

- 挨拶、相槌、短いbackchannel
- 直後に意味を失う雑談
- 既存Memoryと同一の内容
- LLMがUSERについて勝手に推測しただけの事実
- 一時的なMoodだけを表す発言
- 確認されていない推測を客観Factとして扱うこと
- password / API key / auth token / one-time codeなどのcredential情報

最後のcredential情報は通常の会話Memory対象から除外する。

## Deduplication / reinforcement

同じ意味の情報が再度現れた場合、原則新しいMemoryを増殖させない。

```text
same or semantically equivalent
↓
existing Memory
↓
reinforcement_count + 1
last_reinforced_at update
confidence / salience adjustment
```

矛盾する場合はreinforcementではなくrevision / disputed処理へ送る。

## Retention and forgetting

Retention class:

- `transient`
- `normal`
- `important`
- `protected`

忘却はMemoryの物理削除ではなく、基本的にretrieval strengthを徐々に下げる。

大まかな寿命感:

```text
transient
数日から数週間でかなり思い出しにくくなる

normal
数週間から数か月単位で緩やかに弱くなる

important
長期間残り、かなりゆっくり弱くなる

protected
自動decayしない
```

正確なdecay curveは実測で調整する。

再度言及されたMemoryはreinforcementされる。

ただし「最近retrievalされた」という理由だけで同じMemoryを何度も会話へ出さないよう、retrieval cooldownを持たせる。

## Retrieval

Main LLMへMemory Store全体を渡さない。

まずCharacterの`known_by`でfilterし、その後Contextに関連するMemoryをrankingする。

主なranking要素:

- semantic relevance
- current person / target relevance
- importance
- character-specific salience
- recency
- unresolved follow-up
- Goal / Desire / Seed relevance
- emotional relevance
- reinforcement
- recent retrieval penalty

通常はTop 3-6程度をRuntime Contextへ渡す。

関連Memoryがなければ0件でもよい。

`superseded` / `retracted` は通常retrievalから除外する。

`disputed` を使う場合は「確定事実」としてではなく、不確実・食い違いありと明示したContextで渡す。

## Relationship integration

Relationship値と、その理由を分離する。

例:

```text
Trust = 78

related Memory:
"USERがSAYOとの約束を守った"
```

Relationship変化を説明・再現できるよう、重要Interaction EventからMemoryへの参照を保持する。

Memoryを削除しても過去のRelationship履歴を自動で時間逆行させない。

誤解が訂正された場合はclarification / reconciliationなど新しいInteraction Eventを通して現在Relationshipを調整する。

## User controls

Character Profile > Memoryから以下を行えるようにする。

- View
- Correct
- Pin / Protect
- Forget
- Delete
- Advanced: known_by correction

### Correct

旧Memoryを`superseded`にし、新しいMemoryを作る。

履歴は保持する。

### Pin / Protect

`retention: protected`へ変更する。

### Forget

通常retrievalから除外するsoft operation。

実装上はarchived / suppressed相当の状態を使ってよい。

### Delete

Memory本体を永続Storeから削除するhard operation。

関連参照がある場合はdangling referenceを残さないよう整理する。

### known_by correction

誤って「知っている」ことになったCharacterをUSERが修正できる。

通常UIではAdvanced操作として扱う。

## Character Profile UI

通常表示は数値パネルではなく自然文中心にする。

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

Memory詳細では:

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

を確認できる。

Developer Modeでは:

- raw score breakdown
- deduplication result
- retrieval score
- source_event_id
- known_by per Character
- revision chain

まで表示できるようにする。

## Final principle

```text
Conversation
↓
意味のある出来事を抽出
↓
必要なものだけ記憶
↓
時間とともに思い出しにくくなる
↓
関係する時だけ思い出す
↓
必要なら訂正される
```

Memoryは会話履歴の倉庫ではなく、Characterが世界の中で経験を積み重ねるための長期状態として扱う。
