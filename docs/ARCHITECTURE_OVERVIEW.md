# Architecture Overview

このドキュメントは、Local Character Labの全体像を初見の共同開発者が短時間で把握するための図解資料。

## 1. High-level architecture

```mermaid
flowchart TD
    UI[Android UI] --> APP[Application Layer]
    APP --> ENG[Character Engine]

    ENG --> DIR[Conversation Director]
    ENG --> PRES[Presence / Guest Slot]
    ENG --> TOPIC[Topic Lifecycle]
    ENG --> REL[Relationship]
    ENG --> MEM[Memory]
    ENG --> MOOD[Mood / Desire / Seed]
    ENG --> KNOW[Knowledge / Interest / Expertise]

    DIR --> CTX[Runtime Context Builder]
    PRES --> CTX
    TOPIC --> CTX
    REL --> CTX
    MEM --> CTX
    MOOD --> CTX
    KNOW --> CTX

    CTX --> MODEL[CharacterModelRuntime]
    MODEL --> MAIN[Main LLM 8B / 4B / 2B]
    MODEL --> HELP[Optional Helper 0.6B-class]

    ENG --> DB[(Room / SQLite)]
    ENG --> PREF[DataStore]
```

## 2. Conversation flow

```mermaid
flowchart TD
    M[New message] --> AN[Conversation Analysis]
    AN --> PE[Presence / Guest check]
    PE --> SS[Speaker selection]
    SS --> ACT[Conversation Act]
    ACT --> KG[Knowledge depth guard]
    KG --> CB[Context Builder]
    CB --> LLM[Main LLM]
    LLM --> U[Character utterance]
    U --> CONT[continue_score]
    CONT -->|continue| SS
    CONT -->|stop| SIL[Silence / cooldown]
    U --> EV[Interaction Events]
    EV --> RM[Relationship / Mood / Memory update]
```

固定ローテーションは使わない。`SILENCE`は正常な出力。

## 3. Character runtime input

Main LLMへCharacterの全DBを投げない。Context Builderが必要部分だけ抽出する。

```mermaid
flowchart LR
    P[Relevant Profile Slice] --> C[Runtime Context]
    S[Current State] --> C
    R[Target Relationship] --> C
    T[Current Topic / Act] --> C
    K[Knowledge Guard] --> C
    M[Top Relevant Memories] --> C
    D[Used Seed / Desire] --> C
    H[Recent Transcript] --> C
    I[Director Instruction] --> C
    C --> L[Main LLM]
```

## 4. Character identity

```mermaid
flowchart TD
    CP[Character Profile] --> SP[Speech Style]
    CP --> PER[Personality]
    CP --> INT[Interest]
    CP --> EXP[Expertise]
    CP --> VAL[Values]
    CP --> BEH[Conversation Behavior]

    SP --> RES[Speech Style Resolver]
    REL[Relationship] --> RES
    MOOD[Mood] --> RES
    ACT[Conversation Act] --> RES
    TOP[Topic] --> RES
    RES --> GEN[Generation Context]
```

## 5. Social state

```mermaid
flowchart LR
    A[Character A] -->|directional| AB[A to B Relationship]
    B[Character B] -->|directional| BA[B to A Relationship]

    AB --> F1[Familiarity]
    AB --> F2[Affection]
    AB --> F3[Trust]
    AB --> F4[Comfort]
    AB --> F5[Tension]
```

`A -> B` と `B -> A` は独立。

## 6. Knowledge separation

```mermaid
flowchart TD
    TOP[Topic] --> I[Interest]
    TOP --> E[Expertise]
    FACT[Specific fact / event] --> KA[Knowledge Access]
    KA --> U[UNKNOWN]
    KA --> H[HEARD]
    KA --> K[KNOWN]
    KA --> D[DETAILED]
```

同じ基盤モデルが内部的に知っていることと、Characterが知っていることは分ける。

## 7. Memory to autonomous conversation

```mermaid
flowchart LR
    MEM[Memory] --> SEED[Conversation Seed]
    NEWS[News] --> SEED
    INT[Interest] --> SEED
    REL[Relationship] --> SEED
    ENV[Environment] --> SEED
    RAND[Random thought] --> SEED

    SEED --> DIR[Director]
    DIR --> ACT[Conversation]
    ACT --> EV[New Events]
    EV --> MEM
```

## 8. Character Goal concept

現在は設計候補。GoalはDesireより長い方向性を持つ。

```mermaid
flowchart LR
    G[Goal] --> D[Desire]
    D --> S[Seed]
    S --> C[Conversation]
    C --> M[Memory]
    C --> R[Relationship]
    M --> G
    R --> G
```

## 9. Model runtime

```mermaid
flowchart TD
    E[Character Engine] --> R[CharacterModelRuntime]
    R --> P8[PC Main 8B-class]
    R --> A4[Android Main 4B-class]
    R --> L2[Lightweight Main 2B-class]
    R --> H06[Helper 0.6B-class]
```

Model Runtimeは交換可能にし、Character Profile / Memory / Relationshipとは分離する。

## 10. Foreground latency budget

```mermaid
flowchart LR
    U[USER Send] --> D[Deterministic]
    D --> H[Optional Helper]
    H --> C[Director + Memory + Context]
    C --> M[Main LLM]
    M --> S[Streaming UI]
```

通常foreground turnの目安は10秒以内。Main LLM callは通常1回を第一目標にする。

## 11. Android UI

```mermaid
flowchart TD
    APP[App] --> CHAT[Chat]
    APP --> CHARS[Characters]
    APP --> SET[Settings]

    CHAT --> PROF[Character Profile]
    CHARS --> PROF
    CHAT --> INV[Invite / Exit]
    SET --> MODELS[Model Library]
    SET --> DEV[Developer Mode]
```

ProfileはChat iconからでもCharacters一覧からでも同じ画面・同じデータを使う。

## 12. Ownership boundaries

```text
UI
  表示・入力・navigation

Application
  use case orchestration

Domain / Character Engine
  world rules / Director / Presence / Relationship / Memory logic

Persistence
  Room / DataStore / repositories

Model Runtime
  Main / Helper inference
```

UIから直接Relationship値やMood値を書き換えない。必ずApplication / Domainを通す。
