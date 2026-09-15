# Model Plan

## Base model direction

最初は3B級の共通ベースモデルを1つロードし、複数キャラクターをProfile・Context・必要に応じたAdapterで分ける。

10キャラクター = 10個の3Bモデル、とはしない。

## Character separation

各キャラクターは少なくとも以下を別々に持つ。

- Character Profile
- Relationship state
- Memory access scope
- Mood / Desire / life state
- 会話Context
- 将来的には必要なキャラクターだけLoRA等のAdapter

## Generation policy

複数キャラクターを1回の生成でまとめて演じさせるのではなく、基本は1キャラクターずつ順次生成する。

例:

```text
USER
 -> SAYO生成
 -> transcript更新
 -> TTがSAYOの実際の発言を読んで判断・生成
 -> transcript更新
 -> 次のキャラクターが判断
```

固定のA -> B -> C -> Dローテーションにはしない。

Conversation Directorが「誰が発言候補か」を決め、発言しない選択も許す。

## Training direction

初期段階ではLoRA学習を急がず、まずProfile + Prompt + Few-shot + Memoryでキャラクター性を作る。

実際に使って観察した失敗パターンを集めた後、必要なキャラクターだけSFT / LoRA / QLoRAを検討する。

## Integration

Character Engineを独立APIとして扱える形を目標とする。

```text
[UI / 新躍進政策]
       |
       v
[Character Engine]
       |
       +-- Presence / Director
       +-- Relationship
       +-- Memory
       +-- Desire / Mood
       +-- 3B LLM Runtime
```

モデル・Runtime・量子化方式は実装開始時に改めて選定する。
