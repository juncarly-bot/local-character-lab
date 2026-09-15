# Decision 003: Preserve personal memory semantics

Status: Adopted in principle

## Decision

キャラクターごとに「何を知っているか」を分離する。

物理保存方式は未確定であり、キャラクター別Memory Storeでも、共通Memory Store + `known_by` 方式でもよい。

ただし、見ていない出来事を自動的に全員が知る設計にはしない。

## Reason

情報差そのものが自然な会話を生むため。

例として、SAYOとTTだけが経験した出来事を、後から入室したCは知らない状態を維持する。
