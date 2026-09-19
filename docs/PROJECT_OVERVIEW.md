# Project Overview

## 1. Vision

Local Character Labは、単に複数のAI人格を切り替えるチャットアプリではない。

目標は、複数キャラクターが同じ社会空間を共有し、それぞれが異なる人格・知識・関係性・記憶・気分・興味を持ちながら、USERを含めて自然に会話するCharacter Engineを作ること。

最終的には新躍進政策へ統合し、学習・生活支援機能の上に「キャラクターと一緒に生活している感覚」を乗せられる構造を目指す。

## 2. 普通のチャットAIと何が違うか

このプロジェクトでは、LLMそのものをキャラクター世界の全責任者にしない。

```text
Character Engine
  = 世界の状態、時間、関係、記憶、発言順、沈黙を管理

LLM
  = 意味を理解し、指定されたキャラクターとして自然な言葉を作る
```

これにより、Main LLMを8Bから4Bへ変更しても、人格・関係性・Memoryの根本が消えない設計を狙う。

## 3. Core experience

基本Roomは3人のResident + 1人のGuest Slot。

```text
Room
├ USER
├ Resident A
├ Resident B
├ Resident C
└ Guest Slot
```

Guest Slotは現在のTopic、Interest、Expertise、Relationship、Mood、Desireなどによって候補が変わる。Roomが満員だからといって既存メンバーを機械的に追い出さない。

USERはキャラクターを明示的にInviteできる。キャラクターは沈黙することも、Roomへ来ないことも、会話途中で退出することもある。

## 4. Character = 設定ではなく状態を持つ存在

各Characterは少なくとも以下を持つ。

### Stable profile

- Identity
- Personality
- Conversation behavior
- Speech Style
- Interests
- Expertise / Knowledge background
- Values
- Life-cycle tendency
- Character Goals

### Runtime state

- Life state: `SLEEP` / `FREE`
- Room state: `IN` / `OUT`
- Mood
- Desire
- Conversation Seed
- cooldown / fatigue

### Social state

- Directional Relationship
- Character-specific Memory access
- Character-specific Knowledge Access

恒常Profileと現在状態を分離することが重要。

## 5. Speech Style

Characterの再現を「語尾」だけに依存させない。

```text
Speech Identity
Speech Tendencies
Signature Expressions
Speech Examples
```

一人称や呼称などのHard Ruleと、語尾・口癖・文章長・冗談頻度などのSoft Ruleを分離する。

Relationship / Mood / Conversation Actに応じてその瞬間のSpeech Styleを解決し、Main LLMへ渡す。

## 6. Relationship

単一の好感度は使わない。方向ごとに独立した関係を持つ。

```text
Familiarity = 共有経験と相手理解
Affection   = 好意
Trust       = 信用して任せられる度合い
Comfort     = 一緒にいて自然でいられる度合い
Tension     = 現在残っている一時的な緊張
```

例えば `Affection 90 / Trust 45 / Comfort 80` なら、「かなり好きで一緒にいると楽しいが、重要なことを全面的に任せるほど信用してはいない」という関係を表せる。

## 7. Memory / Knowledge

同じMain LLMを使っていても、全Characterが同じ情報を知っている状態にはしない。

Knowledgeは次を分離する。

```text
Interest        その話題が好きか
Expertise       その分野に詳しいか
KnowledgeAccess その具体的情報を知っているか
Confidence      どの程度確信しているか
```

Knowledge Accessは `UNKNOWN / HEARD / KNOWN / DETAILED` を基本にする。

Memoryも「誰がその出来事を知っているか」を保持し、その場にいなかったCharacterへ自動共有しない。

## 8. Conversation Director

発言順をA -> B -> Cの固定ローテーションにしない。

Directorが以下を決める。

- 誰が話すか
- 誰も話さないか
- Conversation Actは何か
- Guest Slotを再評価するか
- 会話を続けるか沈黙へ戻るか

Core Conversation Acts:

`knowledge / question / agreement / counterpoint / backchannel / personal_reaction / topic_shift / silence`

Expertiseが高いCharacterばかりが百科事典のように話し続けないよう、Knowledge cooldownやRecent Act履歴を使う。

## 9. Desire / Seed / Character Goal

Conversation Seedは完成した台詞ではなく、「後で持ち出したい話題・理由」の候補。

現在採用しているSeed source:

- interest
- news
- memory
- relationship
- environment
- random_thought

Character Goalは正式採用した上位概念で、Character Profileから設定・編集できる。

```text
Goal
↓
Desire
↓
Seed
↓
Conversation
↓
Memory / Relationship / Goal progress
```

例:

```text
Goal: USERの物理の勉強を見守りたい
↓
Desire: 昨日の問題が解けたか聞きたい
↓
Seed: 昨日の問題について聞く
```

## 10. 生きている感

発言量を増やすことを「生きている感」とは定義しない。

重視する要素:

- 時間の連続性
- Life Cycle
- Mood
- Relationship
- Memory
- Desire / Seed / Goal
- オフライン時間の状態変化
- Character同士の関係
- 意見・興味の違い
- Follow-up
- 何も起きない時間と沈黙

## 11. Model strategy

Main ModelとHelperを役割分離する。

初期方向:

```text
PC
Main 8B-class
Helper 0.6B-class

Android 8GB-class
Main 4B-class
Helper 0.6B-class
```

HelperはTopic分類、importance、短いsummary、Event candidateなどの軽量処理候補。Mainは最終発話・高度な意味理解に集中する。

ただしHelperを増やせば必ず速くなるとは考えず、実測したボトルネックから役割を決める。

## 12. Latency target

通常のforeground turnは **10秒以内を目安** とする。

体感遅延を下げるため、最終文章の完成を待つのではなくStreaming表示を優先する。

Developer Modeではhelper analysis / memory retrieval / director / context build / prompt eval / generation / validation / total latencyを計測可能にする。

## 13. UI

Android UIは新躍進政策のChatを視覚・操作上の参考にする。

Main navigation:

```text
Chat
Characters
Settings
```

Chat内のCharacter iconをタップするとCharacter Profileを開く。Characters一覧からも同じProfileへ入る。

ProfileではPersonality、Speech Style、Interests、Expertise、Relationship、Memory、Character Goals、Desire / Seeds、Behavior、Developer情報などを確認・編集する。

## 14. MVP success condition

MVPの最重要成功条件:

> 3人前後のCharacterが同じRoomに存在し、それぞれ違う人格・興味・知識・関係を保ちながら、USERを含めて自然な雑談が成立すること。

機能数ではなく、この体験が成立するかを最初に検証する。
