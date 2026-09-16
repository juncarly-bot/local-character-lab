# Conversation Acts v1

## Goal

会話を「知識を出す人が次々に現れる百科事典モード」にしない。

各発言の役割をConversation Actとして分け、知識・疑問・反論・共感・相槌・感想・話題転換・沈黙をバランスよく配置する。

## Core acts

- `knowledge`: 新しい知識・事実・説明を出す
- `question`: 疑問や確認を返す
- `agreement`: 共感・同意
- `counterpoint`: 反論・別視点
- `backchannel`: 相槌・短い反応
- `personal_reaction`: 感想、驚き、好み、経験
- `topic_shift`: 話題を少しずらす・広げる
- `silence`: 発言しない

## Selection inputs

Conversation Directorは、発話前に少なくとも以下を参照する。

- 直前数ターンの会話
- 現在Topic
- Character Interest
- Character Expertise
- Knowledge Access / Confidence
- Personality / Conversation behavior
- Relationship / Mood / Desire
- 直近のConversation Act履歴

## Knowledge role

`knowledge`は主にExpertiseが高く、Knowledge Accessが十分なキャラクターへ割り当てる。

Expertiseが低いキャラクターは、`question`、`backchannel`、`personal_reaction`側へ回りやすくする。

これにより同じ基盤LLMでも役割差を作る。

## Knowledge cooldown

新しい知識の連鎖を防ぐため、`knowledge`の直後は通常1-2発言程度、別Actを優先する。

例:

```text
knowledge
→ personal_reaction
→ question
→ answer / knowledge
```

避けたい流れ:

```text
knowledge
→ knowledge
→ knowledge
→ knowledge
```

ユーザーが「詳しく教えて」「続きは？」など明示的に深掘りを求めた場合はKnowledge cooldownを緩める。

## Balance principle

常に均等配分する必要はない。

- 得意分野: knowledge / counterpointが増える
- 苦手分野: question / backchannel / reactionが増える
- 親しい関係: personal_reaction / teasing等の余地が増える
- 高Tension: counterpointや短い反応が増え得る
- 低energy: backchannel / silenceが増える

Conversation Actは台詞そのものではなく、台詞生成の意図を指定する。

## Example

物理Topic:

```text
TT
interest 60
expertise 90
→ knowledge

SAYO
interest 80
expertise 35
→ personal_reaction

A
interest 70
expertise 15
→ question
```

会話例:

```text
TT: 新しい知識を1つ出す
SAYO: 自分の感想を返す
A: 疑問を出す
TT: 疑問へ答える
SAYO: 共感または相槌
```

## Design principle

「誰がどれだけ知っているか」だけでは自然な雑談にならない。

会話には、知識を出す人、質問する人、反応する人、異論を出す人、黙る人が必要である。

Conversation Directorは発言内容を全部決めるのではなく、まず役割を決め、その役割をLLMにキャラクターらしく演じさせる。
