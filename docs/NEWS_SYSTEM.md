# News / Realtime Information System v1

## Goal

最近のニュースや現在情報を会話の話題源として利用しつつ、ローカルLLMへ毎回リアルタイム検索をさせない。

キャラクターごとに「そのニュースを見たか」「どれくらい詳しく読んだか」を変え、知識差と話題の偏りを作る。

## Three information layers

```text
1. Model prior
   一般知識・比較的安定した知識

2. News Cache
   最近取得したニュース・話題

3. Live Retrieval
   今この瞬間の確認が必要な情報
```

通常会話ではNews Cacheを優先し、本当に鮮度が必要な質問だけLive Retrievalへ送る。

## News Collector

バックグラウンドでニュースを取得し、短いNews Cardへ正規化する。

概念例:

```json
{
  "id": "news_2026_xxx",
  "title": "...",
  "summary": "...",
  "topics": ["space", "physics"],
  "published_at": "...",
  "source": "...",
  "freshness": "recent"
}
```

本文を毎回そのまま会話Contextへ入れず、要約・topic・source metadataを保存する。

## Character distribution

全キャラクターへ同じニュースを同じ深さで配布しない。

Character Interests / Expertise / reading probabilityからKnowledge Accessを決める。

例:

```text
宇宙ニュース

TT
interest match = 0.92
→ DETAILED

SAYO
interest match = 0.48
→ HEARD / KNOWN

A
interest match = 0.10
→ UNKNOWN
```

これにより会話では、詳しいキャラが紹介し、見出しだけ見たキャラが反応し、知らないキャラが質問できる。

## Desire integration

強くInterestへ一致するNews CardはDesire候補になる。

例:

```text
News Card: space topic
Character: space interest 95
↓
share Desire
「このニュースについて話したい」
```

自由時間や会話の空きがあるとき、自発的な話題として持ち出せる。

## Realtime lookup

News Cacheで足りない場合のみLive Retrievalを使う。

例:

- 「最近の宇宙開発ニュース」 -> News Cache
- 「今日の打ち上げは成功した？」 -> Live Retrieval
- 「今の株価・天気・試合結果」 -> 専用のリアルタイム情報源

取得した事実はLLMへ渡し、キャラクターらしい会話表現だけをLLMが担当する。

## Latency policy

ニュース取得や要約は可能な限りforeground会話から外す。

```text
Internet
  ↓ background
News Collector
  ↓
News Cache
  ↓ foregroundではローカル参照
Character Engine
```

これにより通常会話でネット待ちを発生させない。

## Knowledge / Memory integration

ニュースを知ったキャラクターだけにKnowledge Accessを与える。

会話で他キャラクターがそのニュースを聞いた場合:

```text
UNKNOWN -> HEARD -> KNOWN
```

と伝播してよい。

古いNews Cardは鮮度を落とし、必要ならarchiveまたは破棄する。

## Safety / factuality principle

ニュース事実とキャラクターの感想を内部的に分離する。

- Source-backed fact: News Card / Retrieval
- Character reaction: LLM generation

キャラクターが知らない最新情報をモデルpriorだけで断定させない。
